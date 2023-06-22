package learner;

import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.config.SulClientConfig;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.core.config.StateFuzzerClientConfig;

public class SshStateFuzzerClientConfig extends StateFuzzerClientConfig {

    public SshStateFuzzerClientConfig(SulClientConfig sulClientConfig) {
        super(sulClientConfig);
        sulClientConfig.setPort(0);
    }
}
