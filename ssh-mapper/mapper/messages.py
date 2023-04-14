from manualparamiko.common import *

MSG_CH_NONE = -4
MSG_CH_MAX = -3
MSG_NO_RESP = -2
MSG_NO_CONN = -1

# Message to function mapping
MSG_MAPPING = {
    # L1
    'DISCONNECT': 'fuzz_disconnect',
    'IGNORE': 'fuzz_ignore',
    'UNIMPL': 'fuzz_unimplemented',
    'DEBUG': 'fuzz_debug',

    'KEXINIT': 'fuzz_kex_init',
    'KEXINIT_PROCEED': 'fuzz_kex_init_proceed',
    'KEX30': 'fuzz_kexdh_init',
    'NEWKEYS': 'fuzz_newkeys',
    'NEWKEYS_NOP': 'fuzz_newkeysnop',

    'SR_AUTH': 'fuzz_service_request_auth',
    'SR_CONN': 'fuzz_service_request_conn',

    # L2
    'UA_PK_NOK': 'fuzz_userauth_pk_nok',
    'UA_PK_OK': 'fuzz_userauth_pk_ok',
    'UA_PW_NOK': 'fuzz_userauth_pw_nok',
    'UA_PW_OK': 'fuzz_userauth_pw_ok',
    'UA_NONE': 'fuzz_userauth_none',

    # L3
    'CH_OPEN': 'fuzz_channel_open',
    'CH_CLOSE': 'fuzz_channel_close',
    'CH_EOF': 'fuzz_channel_eof',

    'CH_REQUEST_PTY': 'fuzz_channel_request_pty',
    'CH_REQUEST_ENV': 'fuzz_channel_request_env',

    'CH_DATA': 'fuzz_channel_data',
    'CH_EXTENDED_DATA': 'fuzz_channel_extended_data',
    'CH_WINDOW_ADJUST': 'fuzz_channel_window_adjust',

    'TCP_FORWARD_START': 'fuzz_global_request_tcp',
    'TCP_FORWARD_CANCEL': 'fuzz_global_request_tcp_cancel',

    # Special
    'REKEY': 'fuzz_rekey',
    
	
    'SEQ_DISCONNECT': 'seq_disconnect',
    'SEQ_IGNORE': 'seq_ignore',
    'SEQ_UNIMPL': 'seq_unimplemented',
    'SEQ_DEBUG': 'seq_debug',

    'SEQ_KEXINIT': 'seq_kex_init',
    'SEQ_KEXINIT_PROCEED': 'seq_kex_init_proceed',
    'SEQ_KEX30': 'seq_kexdh_init',
    'SEQ_NEWKEYS': 'seq_newkeys',

    'SEQ_SR_AUTH': 'seq_service_request_auth',
    'SEQ_SR_CONN': 'seq_service_request_conn',

    # L2
    'SEQ_UA_PK_NOK': 'seq_userauth_pk_nok',
    'SEQ_UA_PK_OK': 'seq_userauth_pk_ok',
    'SEQ_UA_PW_NOK': 'seq_userauth_pw_nok',
    'SEQ_UA_PW_OK': 'seq_userauth_pw_ok',
    'SEQ_UA_NONE': 'seq_userauth_none',

    # L3
    'SEQ_CH_OPEN': 'seq_channel_open',
    'SEQ_CH_CLOSE': 'seq_channel_close',
    'SEQ_CH_EOF': 'seq_channel_eof',

    'SEQ_CH_REQUEST_PTY': 'seq_channel_request_pty',
    'SEQ_CH_REQUEST_ENV': 'seq_channel_request_env',

    'SEQ_CH_DATA': 'seq_channel_data',
    'SEQ_CH_EXTENDED_DATA': 'seq_channel_extended_data',
    'SEQ_CH_WINDOW_ADJUST': 'seq_channel_window_adjust',

    'SEQ_TCP_FORWARD_START': 'seq_global_request_tcp',
    'SEQ_TCP_FORWARD_CANCEL': 'seq_global_request_tcp_cancel',
    
    # Special
    'SEQ_REKEY': 'seq_rekey',
}

# Message names:
MSG_NAMES = {
    MSG_CH_NONE: 'CH_NONE',
    MSG_CH_MAX: 'CH_MAX',
    MSG_NO_RESP: 'NO_RESP',  # A custom value if no response has been received
    MSG_NO_CONN: 'NO_CONN',  # A custom value for disconnected sessions that is never state-changing
    MSG_DISCONNECT: 'DISCONNECT',
    MSG_IGNORE: 'IGNORE',
    MSG_UNIMPLEMENTED: 'UNIMPL',
    MSG_DEBUG: 'DEBUG',
    MSG_SERVICE_REQUEST: 'SR_REQUEST',
    MSG_SERVICE_ACCEPT: 'SR_ACCEPT',
    MSG_EXT_INFO: 'EXT_INFO',
    MSG_KEXINIT: 'KEXINIT',
    MSG_NEWKEYS: 'NEWKEYS',
    30: 'KEX30',  # MSG_KEXDH_INIT
    31: 'KEX31',  # MSG_KEXDH_REPLY
    32: 'KEX32',
    33: 'KEX33',
    34: 'KEX34',
    40: 'KEX40',
    41: 'KEX41',
    MSG_USERAUTH_REQUEST: 'UA_REQUEST',
    MSG_USERAUTH_FAILURE: 'UA_FAILURE',
    MSG_USERAUTH_SUCCESS: 'UA_SUCCESS',
    MSG_USERAUTH_BANNER: 'UA_BANNER',

    MSG_CHANNEL_OPEN_SUCCESS: 'CH_OPEN_SUCCESS',
    MSG_CHANNEL_OPEN_FAILURE: 'CH_OPEN_FAILURE',
    MSG_CHANNEL_CLOSE: 'CH_CLOSE',
    MSG_CHANNEL_WINDOW_ADJUST: 'CH_WINDOW_ADJUST',
    MSG_CHANNEL_DATA: 'CH_DATA',
    MSG_CHANNEL_EXTENDED_DATA: 'CH_EXTENDED_DATA',

    MSG_CHANNEL_SUCCESS: 'CH_SUCCESS',
    MSG_CHANNEL_FAILURE: 'CH_FAILURE',

    MSG_REQUEST_SUCCESS: 'GLOBAL_SUCCESS',
    MSG_REQUEST_FAILURE: 'GLOBAL_FAILURE',

    MSG_CHANNEL_EOF: 'CH_EOF',
    MSG_GLOBAL_REQUEST: 'GLOBAL_REQUEST',
}
