package learner;

import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.abstractsymbols.AbstractOutput;

public class SshOutput extends AbstractOutput<SshOutput, String> {

	public SshOutput(String name) {
		super(name);
	}

	@Override
	protected SshOutput buildOutput(String name) {
		return new SshOutput(name);
	}

	@Override
	protected SshOutput convertOutput() {
		return new SshOutput(this.name);
	}
}
