package learner;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.config.SulClientConfig;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.config.MapperConnectionConfig;

public class SshSulClientConfig extends SulClientConfig implements SshMapperConfigProvider {

    @ParametersDelegate
    private SshMapperConfig sshMapperConfig;
    
    public SshSulClientConfig() {
        super.setPort(0);
        super.setResponseWait(0L);
        super.setStartWait(0L);
        sshMapperConfig = new SshMapperConfig();
    }

    @Override
    public void applyDelegate(MapperConnectionConfig config) {
    }

    @Override
    public SshMapperConfig getSshMapperConfig() {
        return sshMapperConfig;
    }
}
