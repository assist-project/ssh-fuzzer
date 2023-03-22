# Copyright (C) 2003-2011  Robey Pointer <robeypointer@gmail.com>
#
# This file is part of manualparamiko.
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

import sys
from manualparamiko._version import __version__, __version_info__

if sys.version_info < (2, 6):
    raise RuntimeError('You need Python 2.6+ for this module.')


__author__ = "Jeff Forcier <jeff@bitprophet.org>"
__license__ = "GNU Lesser General Public License (LGPL)"


from manualparamiko.transport import SecurityOptions, Transport
from manualparamiko.client import SSHClient, MissingHostKeyPolicy, AutoAddPolicy, RejectPolicy, WarningPolicy
from manualparamiko.auth_handler import AuthHandler
from manualparamiko.ssh_gss import GSSAuth, GSS_AUTH_AVAILABLE
from manualparamiko.channel import Channel, ChannelFile
from manualparamiko.ssh_exception import SSHException, PasswordRequiredException, \
    BadAuthenticationType, ChannelException, BadHostKeyException, \
    AuthenticationException, ProxyCommandFailure
from manualparamiko.server import ServerInterface, SubsystemHandler, InteractiveQuery
from manualparamiko.rsakey import RSAKey
from manualparamiko.dsskey import DSSKey
from manualparamiko.ecdsakey import ECDSAKey
from manualparamiko.sftp import SFTPError, BaseSFTP
from manualparamiko.sftp_client import SFTP, SFTPClient
from manualparamiko.sftp_server import SFTPServer
from manualparamiko.sftp_attr import SFTPAttributes
from manualparamiko.sftp_handle import SFTPHandle
from manualparamiko.sftp_si import SFTPServerInterface
from manualparamiko.sftp_file import SFTPFile
from manualparamiko.message import Message
from manualparamiko.packet import Packetizer
from manualparamiko.file import BufferedFile
from manualparamiko.agent import Agent, AgentKey
from manualparamiko.pkey import PKey
from manualparamiko.hostkeys import HostKeys
from manualparamiko.config import SSHConfig
from manualparamiko.proxy import ProxyCommand

from manualparamiko.common import AUTH_SUCCESSFUL, AUTH_PARTIALLY_SUCCESSFUL, AUTH_FAILED, \
    OPEN_SUCCEEDED, OPEN_FAILED_ADMINISTRATIVELY_PROHIBITED, OPEN_FAILED_CONNECT_FAILED, \
    OPEN_FAILED_UNKNOWN_CHANNEL_TYPE, OPEN_FAILED_RESOURCE_SHORTAGE

from manualparamiko.sftp import SFTP_OK, SFTP_EOF, SFTP_NO_SUCH_FILE, SFTP_PERMISSION_DENIED, SFTP_FAILURE, \
    SFTP_BAD_MESSAGE, SFTP_NO_CONNECTION, SFTP_CONNECTION_LOST, SFTP_OP_UNSUPPORTED

from manualparamiko.common import io_sleep

__all__ = [ 'Transport',
            'SSHClient',
            'MissingHostKeyPolicy',
            'AutoAddPolicy',
            'RejectPolicy',
            'WarningPolicy',
            'SecurityOptions',
            'SubsystemHandler',
            'Channel',
            'PKey',
            'RSAKey',
            'DSSKey',
            'Message',
            'SSHException',
            'AuthenticationException',
            'PasswordRequiredException',
            'BadAuthenticationType',
            'ChannelException',
            'BadHostKeyException',
            'ProxyCommand',
            'ProxyCommandFailure',
            'SFTP',
            'SFTPFile',
            'SFTPHandle',
            'SFTPClient',
            'SFTPServer',
            'SFTPError',
            'SFTPAttributes',
            'SFTPServerInterface',
            'ServerInterface',
            'BufferedFile',
            'Agent',
            'AgentKey',
            'HostKeys',
            'SSHConfig',
            'util',
            'io_sleep' ]
