package learner;

import com.beust.jcommander.Parameter;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.config.SulClientConfig;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.config.MapperConnectionConfig;

public class SshSulClientConfig extends SulClientConfig implements SshMapperAddress {
    @Parameter(names = "-host", required = true, description = "The target port "
            + "of the SUL server on which the state fuzzer client should listen")
    protected String host;
    public SshSulClientConfig() {
        super.setPort(0);
    }

    @Override
    public void applyDelegate(MapperConnectionConfig config) {
    }
    

    public String getHost() {
        return host;
    }
}
