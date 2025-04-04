package learner;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.AbstractSul;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.SulAdapter;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.config.SulConfig;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.sulwrappers.DynamicPortProvider;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.sulwrappers.ProcessHandler;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.Mapper;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.context.ExecutionContext;
import com.github.protocolfuzzing.protocolstatefuzzer.utils.CleanupTasks;

public class RASshMapperSul
        implements AbstractSul<RASshInput, RASshInput, ExecutionContext<RASshInput, RASshInput, String>> {
    private RASocketMapperSul socketSul;

    /** Stores the constructor parameter. */
    protected SulConfig sulConfig;

    /** Stores the constructor parameter. */
    protected CleanupTasks cleanupTasks;

    /** Stores the provided dynamic port provider. */
    protected DynamicPortProvider dynamicPortProvider;

    /** Stores the Mapper instance. */
    protected RASshMapper mapper;

    /** Stores the SulAdapter instance. */
    protected SulAdapter sulAdapter;
    protected RASshInputBuilder outputBuilder;

    public <T extends SulConfig & SshMapperConfigProvider> RASshMapperSul(T sulConfig, CleanupTasks cleanupTasks)
            throws UnknownHostException, IOException {

        // copied from the commit before the introduction of generics
        // -------------------------------------------------------------------
        this.sulConfig = sulConfig;
        this.cleanupTasks = cleanupTasks;
        // outputBuilder = new RASshInputBuilder(); maybe delete the class file now?

        // mapper and sulAdapter will be provided in subclasses
        this.mapper = new RASshMapper();
        this.sulAdapter = new SshSulAdapter();
        // -------------------------------------------------------------------

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

        socketSul = new RASocketMapperSul(sock);
    }

    @Override
    public void pre() {
        socketSul.reset();
    }

    @Override
    public void post() {
    }

    private static class MapperProcessHandler extends ProcessHandler {

        protected MapperProcessHandler(String command, long startWait) {
            super(command, startWait);
        }

    }

    @Override
    public RASshInput step(RASshInput in) {
        RASshInput output = socketSul.sendInput(in);
        return output;
    }

    @Override
    public SulConfig getSulConfig() {
        return sulConfig;
    }

    @Override
    public CleanupTasks getCleanupTasks() {
        return cleanupTasks;
    }

    @Override
    public void setDynamicPortProvider(DynamicPortProvider dynamicPortProvider) {
        this.dynamicPortProvider = dynamicPortProvider;
    }

    @Override
    public DynamicPortProvider getDynamicPortProvider() {
        return dynamicPortProvider;
    }

    @Override
    public Mapper<RASshInput, RASshInput, ExecutionContext<RASshInput, RASshInput, String>> getMapper() {
        return mapper;
    }

    @Override
    public SulAdapter getSulAdapter() {
        return sulAdapter;
    }
}
