# Copyright (C) 2003-2007  Robey Pointer <robeypointer@gmail.com>
#
# This file is part of paramiko.
#
# Paramiko is free software; you can redistribute it and/or modify it under the
# terms of the GNU Lesser General Public License as published by the Free
# Software Foundation; either version 2.1 of the License, or (at your option)
# any later version.
#
# Paramiko is distributed in the hope that it will be useful, but WITHOUT ANY
# WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
# A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
# details.
#
# You should have received a copy of the GNU Lesser General Public License
# along with Paramiko; if not, write to the Free Software Foundation, Inc.,
# 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

"""
Packet handling
"""

import errno
import os
import socket
import struct
import threading
import time
from hmac import HMAC

from paramiko import util
from paramiko.common import linefeed_byte, cr_byte_value, asbytes, MSG_NAMES, \
    DEBUG, xffffffff, zero_byte
from paramiko.py3compat import u, byte_ord
from paramiko.ssh_exception import SSHException, ProxyCommandFailure
from paramiko.message import Message

from socket import error as SocketError

MSG_NO_RESP = -2
MSG_NO_CONN = -1


def compute_hmac(key, message, digest_class):
    return HMAC(key, message, digest_class).digest()


class NeedRekeyException (Exception):
    pass


class NoTimelyResponse (Exception):
    pass


class Packetizer (object):
    """
    Implementation of the base SSH packet protocol.
    """

    # READ the secsh RFC's before raising these values.  if anything,
    # they should probably be lower.
    REKEY_PACKETS = pow(2, 29)
    REKEY_BYTES = pow(2, 29)

    REKEY_PACKETS_OVERFLOW_MAX = pow(2, 29)     # Allow receiving this many packets after a re-key request before terminating
    REKEY_BYTES_OVERFLOW_MAX = pow(2, 29)       # Allow receiving this many bytes after a re-key request before terminating

    def __init__(self, socket):
        self.__socket = socket
        self.__logger = None
        self.__closed = False
        self.__dump_packets = False
        self.__need_rekey = False
        self.__init_count = 0
        self.__remainder = bytes()

        # used for noticing when to re-key:
        self.__sent_bytes = 0
        self.__sent_packets = 0
        self.__received_bytes = 0
        self.__received_packets = 0
        self.__received_bytes_overflow = 0
        self.__received_packets_overflow = 0

        # current inbound/outbound ciphering:
        self.__block_size_out = 8
        self.__block_size_in = 8
        self.__mac_size_out = 0
        self.__mac_size_in = 0
        self.__block_engine_out = None
        self.__block_engine_in = None
        self.__sdctr_out = False
        self.__mac_engine_out = None
        self.__mac_engine_in = None
        self.__mac_key_out = bytes()
        self.__mac_key_in = bytes()
        self.__compress_engine_out = None
        self.__compress_engine_in = None
        self.__sequence_number_out = 0
        self.__sequence_number_in = 0

        # lock around outbound writes (packet computation)
        self.__write_lock = threading.RLock()

        # keepalives:
        self.__keepalive_interval = 0
        self.__keepalive_last = time.time()
        self.__keepalive_callback = None

    def set_log(self, log):
        """
        Set the Python log object to use for logging.
        """
        self.__logger = log

    def set_timeout(self, timeout):
        self.__socket.settimeout(timeout)

    def set_outbound_cipher(self, block_engine, block_size, mac_engine, mac_size, mac_key, sdctr=False):
        """
        Switch outbound data cipher.
        """
        self.__block_engine_out = block_engine
        self.__sdctr_out = sdctr
        self.__block_size_out = block_size
        self.__mac_engine_out = mac_engine
        self.__mac_size_out = mac_size
        self.__mac_key_out = mac_key
        self.__sent_bytes = 0
        self.__sent_packets = 0
        # wait until the reset happens in both directions before clearing rekey flag
        self.__init_count |= 1
        if self.__init_count == 3:
            self.__init_count = 0
            self.__need_rekey = False

    def set_inbound_cipher(self, block_engine, block_size, mac_engine, mac_size, mac_key):
        """
        Switch inbound data cipher.
        """
        self.__block_engine_in = block_engine
        self.__block_size_in = block_size
        self.__mac_engine_in = mac_engine
        self.__mac_size_in = mac_size
        self.__mac_key_in = mac_key
        self.__received_bytes = 0
        self.__received_packets = 0
        self.__received_bytes_overflow = 0
        self.__received_packets_overflow = 0
        # wait until the reset happens in both directions before clearing rekey flag
        self.__init_count |= 2
        if self.__init_count == 3:
            self.__init_count = 0
            self.__need_rekey = False

    def set_outbound_compressor(self, compressor):
        self.__compress_engine_out = compressor

    def set_inbound_compressor(self, compressor):
        self.__compress_engine_in = compressor

    def close(self):
        self.__closed = True
        self.__socket.close()

    def set_hexdump(self, hexdump):
        self.__dump_packets = hexdump

    def get_hexdump(self):
        return self.__dump_packets

    def get_mac_size_in(self):
        return self.__mac_size_in

    def get_mac_size_out(self):
        return self.__mac_size_out

    def need_rekey(self):
        """
        Returns ``True`` if a new set of keys needs to be negotiated.  This
        will be triggered during a packet read or write, so it should be
        checked after every read or write, or at least after every few.
        """
        return self.__need_rekey

    def set_keepalive(self, interval, callback):
        """
        Turn on/off the callback keepalive.  If ``interval`` seconds pass with
        no data read from or written to the socket, the callback will be
        executed and the timer will be reset.
        """
        self.__keepalive_interval = interval
        self.__keepalive_callback = callback
        self.__keepalive_last = time.time()

    def read_all(self, n, check_rekey=False):
        """
        Read as close to N bytes as possible, blocking as long as necessary.

        :param int n: number of bytes to read
        :return: the data read, as a `str`

        :raises EOFError:
            if the socket was closed before all the bytes could be read
        """
        out = bytes()

        # handle over-reading from reading the banner line
        if len(self.__remainder) > 0:
            out = self.__remainder[:n]
            self.__remainder = self.__remainder[n:]
            n -= len(out)
        starttime = time.time()
        while n > 0:
            got_timeout = False
            try:
                x = self.__socket.recv(n)
                if len(x) == 0:
                    # Nothing could be read, but EOF could be ambigious so raise an SocketError
                    # raise EOFError()
                    raise SocketError()
                out += x
                n -= len(x)
            except socket.timeout:
                got_timeout = True

                raise NoTimelyResponse('Got no immediate reponse after timemout, returning...')
            except socket.error as e:
                # on Linux, sometimes instead of socket.timeout, we get
                # EAGAIN.  this is a bug in recent (> 2.6.9) kernels but
                # we need to work around it.
                if (type(e.args) is tuple) and (len(e.args) > 0) and (e.args[0] == errno.EAGAIN):
                    got_timeout = True
                elif (type(e.args) is tuple) and (len(e.args) > 0) and (e.args[0] == errno.EINTR):
                    # syscall interrupted; try again
                    pass
                elif self.__closed:
                    raise EOFError()
                else:
                    raise
            if got_timeout:
                if self.__closed:
                    raise EOFError()
                if check_rekey and (len(out) == 0) and self.__need_rekey:
                    raise NeedRekeyException()
                self._check_keepalive()
        return out

    def write_all(self, out):
        self.__keepalive_last = time.time()
        iteration_with_zero_as_return_value = 0
        while len(out) > 0:
            retry_write = False
            try:
                n = self.__socket.send(out)
            except socket.timeout:
                retry_write = True
            except socket.error as e:
                if (type(e.args) is tuple) and (len(e.args) > 0) and (e.args[0] == errno.EAGAIN):
                    retry_write = True
                elif (type(e.args) is tuple) and (len(e.args) > 0) and (e.args[0] == errno.EINTR):
                    # syscall interrupted; try again
                    retry_write = True
                else:
                    n = -1
            except ProxyCommandFailure:
                raise  # so it doesn't get swallowed by the below catchall
            except Exception:
                # could be: (32, 'Broken pipe')
                n = -1
            if retry_write:
                n = 0
                if self.__closed:
                    n = -1
            else:
                if n == 0 and iteration_with_zero_as_return_value > 10:
                # We shouldn't retry the write, but we didn't
                # manage to send anything over the socket. This might be an
                # indication that we have lost contact with the remote side,
                # but are yet to receive an EOFError or other socket errors.
                # Let's give it some iteration to try and catch up.
                    n = -1
                iteration_with_zero_as_return_value += 1
            if n < 0:
                raise EOFError()
            if n == len(out):
                break
            out = out[n:]
        return

    def readline(self, timeout):
        """
        Read a line from the socket.  We assume no data is pending after the
        line, so it's okay to attempt large reads.
        """
        buf = self.__remainder
        while not linefeed_byte in buf:
            buf += self._read_timeout(timeout)
        n = buf.index(linefeed_byte)
        self.__remainder = buf[n + 1:]
        buf = buf[:n]
        if (len(buf) > 0) and (buf[-1] == cr_byte_value):
            buf = buf[:-1]
        return u(buf)

    def send_message(self, data):
        """
        Write a block of data using the current cipher, as an SSH block.
        """
        # encrypt this sucka
        data = asbytes(data)
        cmd = byte_ord(data[0])
        if cmd in MSG_NAMES:
            cmd_name = MSG_NAMES[cmd]
        else:
            cmd_name = '$%x' % cmd
        orig_len = len(data)
        self.__write_lock.acquire()
        try:
            if self.__compress_engine_out is not None:
                data = self.__compress_engine_out(data)
            packet = self._build_packet(data)
            if self.__dump_packets:
                self._log(DEBUG, 'Write packet <%s>, length %d' % (cmd_name, orig_len))
                self._log(DEBUG, util.format_binary(packet, 'OUT: '))
            if self.__block_engine_out is not None:
                out = self.__block_engine_out.encrypt(packet)
            else:
                out = packet
            # + mac
            if self.__block_engine_out is not None:
                payload = struct.pack('>I', self.__sequence_number_out) + packet
                out += compute_hmac(self.__mac_key_out, payload, self.__mac_engine_out)[:self.__mac_size_out]
            self.__sequence_number_out = (self.__sequence_number_out + 1) & xffffffff

            try:
                self.write_all(out)
            except EOFError:
                pass  # We couldnt sent what we wanted because socket is closed

            self.__sent_bytes += len(out)
            self.__sent_packets += 1
            if (self.__sent_packets >= self.REKEY_PACKETS or self.__sent_bytes >= self.REKEY_BYTES)\
                    and not self.__need_rekey:
                # only ask once for rekeying
                self._log(DEBUG, 'Rekeying (hit %d packets, %d bytes sent)' %
                          (self.__sent_packets, self.__sent_bytes))
                self.__received_bytes_overflow = 0
                self.__received_packets_overflow = 0
                self._trigger_rekey()
        finally:
            self.__write_lock.release()

    def read_message(self):
        """
        Only one thread should ever be in this function (no other locking is
        done).

        :raises SSHException: if the packet is mangled
        :raises NeedRekeyException: if the transport should rekey
        """
        header = self.read_all(self.__block_size_in, check_rekey=True)

        if self.__block_engine_in is not None:
            header = self.__block_engine_in.decrypt(header)
        if self.__dump_packets:
            self._log(DEBUG, util.format_binary(header, 'IN: '))
        packet_size = struct.unpack('>I', header[:4])[0]
        # leftover contains decrypted bytes from the first block (after the length field)
        leftover = header[4:]
        if (packet_size - len(leftover)) % self.__block_size_in != 0:
            raise SSHException('Invalid packet blocking')
        buf = self.read_all(packet_size + self.__mac_size_in - len(leftover))
        packet = buf[:packet_size - len(leftover)]
        post_packet = buf[packet_size - len(leftover):]
        if self.__block_engine_in is not None:
            packet = self.__block_engine_in.decrypt(packet)
        if self.__dump_packets:
            self._log(DEBUG, util.format_binary(packet, 'IN: '))
        packet = leftover + packet

        if self.__mac_size_in > 0:
            mac = post_packet[:self.__mac_size_in]
            mac_payload = struct.pack('>II', self.__sequence_number_in, packet_size) + packet
            my_mac = compute_hmac(self.__mac_key_in, mac_payload, self.__mac_engine_in)[:self.__mac_size_in]
            if not util.constant_time_bytes_eq(my_mac, mac):
                raise SSHException('Mismatched MAC')
        padding = byte_ord(packet[0])
        payload = packet[1:packet_size - padding]

        if self.__dump_packets:
            self._log(DEBUG, 'Got payload (%d bytes, %d padding)' % (packet_size, padding))

        if self.__compress_engine_in is not None:
            payload = self.__compress_engine_in(payload)

        msg = Message(payload[1:])
        msg.seqno = self.__sequence_number_in
        self.__sequence_number_in = (self.__sequence_number_in + 1) & xffffffff

        # check for rekey
        raw_packet_size = packet_size + self.__mac_size_in + 4
        self.__received_bytes += raw_packet_size
        self.__received_packets += 1
        if self.__need_rekey:
            # we've asked to rekey -- give them some packets to comply before
            # dropping the connection
            self.__received_bytes_overflow += raw_packet_size
            self.__received_packets_overflow += 1
            if (self.__received_packets_overflow >= self.REKEY_PACKETS_OVERFLOW_MAX) or \
               (self.__received_bytes_overflow >= self.REKEY_BYTES_OVERFLOW_MAX):
                raise SSHException('Remote transport is ignoring rekey requests')
        elif (self.__received_packets >= self.REKEY_PACKETS) or \
             (self.__received_bytes >= self.REKEY_BYTES):
            # only ask once for rekeying
            self._log(DEBUG, 'Rekeying (hit %d packets, %d bytes received)' %
                      (self.__received_packets, self.__received_bytes))
            self.__received_bytes_overflow = 0
            self.__received_packets_overflow = 0
            self._trigger_rekey()

        cmd = byte_ord(payload[0])
        if cmd in MSG_NAMES:
            cmd_name = MSG_NAMES[cmd]
        else:
            cmd_name = '$%x' % cmd
        if self.__dump_packets:
            self._log(DEBUG, 'Read packet <%s>, length %d' % (cmd_name, len(payload)))
        return cmd, msg

    ##########  protected

    def _log(self, level, msg):
        if self.__logger is None:
            return
        if issubclass(type(msg), list):
            for m in msg:
                self.__logger.log(level, m)
        else:
            self.__logger.log(level, msg)

    def _check_keepalive(self):
        if (not self.__keepalive_interval) or (not self.__block_engine_out) or \
                self.__need_rekey:
            # wait till we're encrypting, and not in the middle of rekeying
            return
        now = time.time()
        if now > self.__keepalive_last + self.__keepalive_interval:
            self.__keepalive_callback()
            self.__keepalive_last = now

    def _read_timeout(self, timeout):
        start = time.time()
        while True:
            try:
                x = self.__socket.recv(128)
                if len(x) == 0:
                    raise EOFError()
                break
            except socket.timeout:
                pass
            except EnvironmentError as e:
                if (type(e.args) is tuple and len(e.args) > 0 and
                        e.args[0] == errno.EINTR):
                    pass
                else:
                    raise
            if self.__closed:
                raise EOFError()
            now = time.time()
            if now - start >= timeout:
                raise socket.timeout()
        return x

    def _build_packet(self, payload):
        # pad up at least 4 bytes, to nearest block-size (usually 8)
        bsize = self.__block_size_out
        padding = 3 + bsize - ((len(payload) + 8) % bsize)
        packet = struct.pack('>IB', len(payload) + padding + 1, padding)
        packet += payload
        if self.__sdctr_out or self.__block_engine_out is None:
            # cute trick i caught openssh doing: if we're not encrypting or SDCTR mode (RFC4344),
            # don't waste random bytes for the padding
            packet += (zero_byte * padding)
        else:
            packet += os.urandom(padding)
        return packet

    def _trigger_rekey(self):
        # outside code should check for this flag
        self.__need_rekey = True
