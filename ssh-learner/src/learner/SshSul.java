package learner;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.AbstractSul;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.config.SulConfig;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.abstractsymbols.AbstractInput;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.abstractsymbols.AbstractOutput;
import com.github.protocolfuzzing.protocolstatefuzzer.utils.CleanupTasks;

public class SshSul extends AbstractSul {
    private SocketSut socketSul;

    public <T extends SulConfig & SshMapperAddress> SshSul(T sulConfig, CleanupTasks cleanupTasks) throws UnknownHostException, IOException {
        super(sulConfig, cleanupTasks);
        String host = sulConfig.getHost();
        String[] hostSplit = host.split("\\:");
        if (hostSplit.length != 1) {
            throw new RuntimeException("Invalid host, expected hostAddress:hostPort");
        }
        String hostAddress = hostSplit[0];
        Integer hostPort = Integer.valueOf(hostSplit[1]);
        Socket sock = new Socket(hostAddress, hostPort);
        cleanupTasks.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    sock.getInputStream().close();
                    sock.getOutputStream().close();
                    sock.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        
        socketSul = new SocketSut(new Socket(hostAddress, hostPort));
    }

    @Override
    public void pre() {
        socketSul.reset();
    }

    @Override
    public void post() {
    }

    @Override
    public AbstractOutput step(AbstractInput in) {
        String output = socketSul.sendInput(in.getName());
        return new SshOutput(output);
    }

}
