package learner;

import com.beust.jcommander.ParametersDelegate;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.config.SulClientConfigStandard;

public class SshSulClientConfig extends SulClientConfigStandard implements SshMapperConfigProvider {

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
