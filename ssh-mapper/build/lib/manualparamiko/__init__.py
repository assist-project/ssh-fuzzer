# Copyright (C) 2003-2011  Robey Pointer <robeypointer@gmail.com>
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
# 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA.

# flake8: noqa
#import sys
from manualparamiko._version import __version__, __version_info__
from manualparamiko.transport import SecurityOptions, Transport
from manualparamiko.client import (
    SSHClient,
    MissingHostKeyPolicy,
    AutoAddPolicy,
    RejectPolicy,
    WarningPolicy,
)
#from manualparamiko.auth_handler import AuthHandler
#from manualparamiko.ssh_gss import GSSAuth, GSS_AUTH_AVAILABLE
from manualparamiko.channel import Channel
#     ChannelFile,
#     ChannelStderrFile,
#     ChannelStdinFile,
# )
from manualparamiko.ssh_exception import (
    AuthenticationException,
    BadAuthenticationType,
    BadHostKeyException,
    ChannelException,
    ConfigParseError,
    CouldNotCanonicalize,
    IncompatiblePeer,
    PasswordRequiredException,
    ProxyCommandFailure,
    SSHException,
)
from manualparamiko.server import ServerInterface, SubsystemHandler, InteractiveQuery
from manualparamiko.rsakey import RSAKey
from manualparamiko.dsskey import DSSKey
from manualparamiko.ecdsakey import ECDSAKey
from manualparamiko.ed25519key import Ed25519Key
from manualparamiko.sftp import SFTPError, BaseSFTP
from manualparamiko.sftp_client import SFTP, SFTPClient
from manualparamiko.sftp_server import SFTPServer
from manualparamiko.sftp_attr import SFTPAttributes
from manualparamiko.sftp_handle import SFTPHandle
from manualparamiko.sftp_si import SFTPServerInterface
from manualparamiko.sftp_file import SFTPFile
from manualparamiko.message import Message
#from manualparamiko.packet import Packetizer
from manualparamiko.file import BufferedFile
from manualparamiko.agent import Agent, AgentKey
from manualparamiko.pkey import PKey, PublicBlob
from manualparamiko.hostkeys import HostKeys
from manualparamiko.config import SSHConfig, SSHConfigDict
from manualparamiko.proxy import ProxyCommand

# from manualparamiko.common import (
#     AUTH_SUCCESSFUL,
#     AUTH_PARTIALLY_SUCCESSFUL,
#     AUTH_FAILED,
#     OPEN_SUCCEEDED,
#     OPEN_FAILED_ADMINISTRATIVELY_PROHIBITED,
#     OPEN_FAILED_CONNECT_FAILED,
#     OPEN_FAILED_UNKNOWN_CHANNEL_TYPE,
#     OPEN_FAILED_RESOURCE_SHORTAGE,
# )

# from manualparamiko.sftp import (
#     SFTP_OK,
#     SFTP_EOF,
#     SFTP_NO_SUCH_FILE,
#     SFTP_PERMISSION_DENIED,
#     SFTP_FAILURE,
#     SFTP_BAD_MESSAGE,
#     SFTP_NO_CONNECTION,
#     SFTP_CONNECTION_LOST,
#     SFTP_OP_UNSUPPORTED,
# )

from manualparamiko.common import io_sleep


__author__ = "Jeff Forcier <jeff@bitprophet.org>"
__license__ = "GNU Lesser General Public License (LGPL)"

__all__ = [
    "Agent",
    "AgentKey",
    "AuthenticationException",
    "AutoAddPolicy",
    "BadAuthenticationType",
    "BadHostKeyException",
    "BufferedFile",
    "Channel",
    "ChannelException",
    "ConfigParseError",
    "CouldNotCanonicalize",
    "DSSKey",
    "ECDSAKey",
    "Ed25519Key",
    "HostKeys",
    "Message",
    "MissingHostKeyPolicy",
    "PKey",
    "PasswordRequiredException",
    "ProxyCommand",
    "ProxyCommandFailure",
    "RSAKey",
    "RejectPolicy",
    "SFTP",
    "SFTPAttributes",
    "SFTPClient",
    "SFTPError",
    "SFTPFile",
    "SFTPHandle",
    "SFTPServer",
    "SFTPServerInterface",
    "SSHClient",
    "SSHConfig",
    "SSHConfigDict",
    "SSHException",
    "SecurityOptions",
    "ServerInterface",
    "SubsystemHandler",
    "Transport",
    "WarningPolicy",
    "io_sleep",
    "util",
]
