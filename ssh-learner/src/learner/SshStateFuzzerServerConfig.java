package learner;

import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.config.SulServerConfig;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.core.config.StateFuzzerServerConfig;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.core.config.StateFuzzerServerConfigStandard;

public class SshStateFuzzerServerConfig extends StateFuzzerServerConfigStandard {

    public SshStateFuzzerServerConfig(SulServerConfig sulServerConfig) {
        super(sulServerConfig);
    }
}
