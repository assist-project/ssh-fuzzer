package learner;

import com.github.protocolfuzzing.protocolstatefuzzer.components.learner.config.LearnerConfigStandard;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.config.SulServerConfig;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.core.config.StateFuzzerServerConfigStandard;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.testrunner.core.config.TestRunnerConfigStandard;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.testrunner.timingprobe.config.TimingProbeConfigEmpty;

public class SshStateFuzzerServerConfig extends StateFuzzerServerConfigStandard {

    public SshStateFuzzerServerConfig(SulServerConfig sulServerConfig) {
        super(new LearnerConfigStandard(), sulServerConfig, new TestRunnerConfigStandard(), new TimingProbeConfigEmpty());
    }
}
