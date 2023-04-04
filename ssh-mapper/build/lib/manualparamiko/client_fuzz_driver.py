
import socket



class ClientFuzzer:

    def __init__(self, host, port):
        self.host = host
        self.port = port
        self.sock = socket.socket()
        self.sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        self.sock.bind((self.ssh_host, self.ssh_port))
        self.sock.listen(10)

    def reset(self):
        pass

    def connect(self):
        pass

    def event_loop(self):
        pass