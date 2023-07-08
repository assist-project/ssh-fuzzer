package learner;

import com.beust.jcommander.ParametersDelegate;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.config.SulClientConfigEmpty;

public class SshSulClientConfig extends SulClientConfigEmpty implements SshMapperConfigProvider {

    @ParametersDelegate
    private SshMapperConfig sshMapperConfig;

    public SshSulClientConfig() {
        sshMapperConfig = new SshMapperConfig();
    }

    @Override
    public SshMapperConfig getSshMapperConfig() {
        return sshMapperConfig;
    }
}
