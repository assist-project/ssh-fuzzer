package learner;

import com.beust.jcommander.ParametersDelegate;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.config.SulServerConfigEmpty;

public class SshSulServerConfig extends SulServerConfigEmpty  implements SshMapperConfigProvider {

    @ParametersDelegate
    private SshMapperConfig sshMapperConfig;

    public SshSulServerConfig() {
        sshMapperConfig = new SshMapperConfig();
    }

    @Override
    public SshMapperConfig getSshMapperConfig() {
        return sshMapperConfig;
    }

}
