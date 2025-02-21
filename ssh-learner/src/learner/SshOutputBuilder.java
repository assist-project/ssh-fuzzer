package learner;

import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.abstractsymbols.OutputBuilder;

public class SshOutputBuilder implements OutputBuilder<SshOutput> {

    @Override
    public SshOutput buildOutput(String name) {
        return new SshOutput(name);
    }

}
