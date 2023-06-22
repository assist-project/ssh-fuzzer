package learner;

import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.config.SulServerConfig;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.core.config.StateFuzzerServerConfig;

public class SshStateFuzzerServerConfig extends StateFuzzerServerConfig {

    public SshStateFuzzerServerConfig(SulServerConfig sulServerConfig) {
        super(sulServerConfig);
    }
}
