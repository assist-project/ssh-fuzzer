package learner;

import com.beust.jcommander.ParametersDelegate;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.config.SulServerConfigStandard;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.config.MapperConnectionConfig;

public class SshSulServerConfig extends SulServerConfigStandard  implements SshMapperConfigProvider {

    @ParametersDelegate
    private SshMapperConfig sshMapperConfig;

    public SshSulServerConfig() {
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
