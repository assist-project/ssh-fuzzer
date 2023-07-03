package learner;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.AbstractSul;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.config.SulConfig;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.sulwrappers.ProcessHandler;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.abstractsymbols.AbstractInput;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.abstractsymbols.AbstractOutput;
import com.github.protocolfuzzing.protocolstatefuzzer.utils.CleanupTasks;

public class SshMapperSul extends AbstractSul {
    private SocketMapperSul socketSul;

    public <T extends SulConfig & SshMapperConfigProvider> SshMapperSul(T sulConfig, CleanupTasks cleanupTasks) throws UnknownHostException, IOException {
        super(sulConfig, cleanupTasks);
        String mapperAddress = sulConfig.getSshMapperConfig().getMapperAddress();
        String[] addressSplit = mapperAddress.split("\\:");
        if (addressSplit.length != 2) {
            throw new MapperException("Invalid mapper host, expected hostAddress:hostPort");
        }
        String mapperIpAddress = addressSplit[0];
        Integer mapperPort = Integer.valueOf(addressSplit[1]);
        if (sulConfig.getSshMapperConfig().getMapperCommand() != null) {
            MapperProcessHandler handler = new MapperProcessHandler(sulConfig.getSshMapperConfig().getMapperCommand(), 
                    sulConfig.getSshMapperConfig().getMapperStartWait());
            handler.launchProcess();
        }
        Socket sock = new Socket(mapperIpAddress, mapperPort);
        cleanupTasks.submit(new Runnable() {
            @Override
            public void run() {
                if (socketSul != null) {
                    socketSul.reset();
                }
                try {
                    sock.close();
                } catch (IOException e) {
                    throw new MapperException(e);
                }
            }
        });
        
        socketSul = new SocketMapperSul(sock);
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
    
    private static class MapperProcessHandler extends ProcessHandler {

        protected MapperProcessHandler(String command, long startWait) {
            super(command, startWait);
        }
        
    }
}
