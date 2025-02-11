package learner;

import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.abstractsymbols.AbstractInputXml;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.abstractsymbols.OutputChecker;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.context.ExecutionContext;

public class SshInput extends AbstractInputXml<SshOutput, String, ExecutionContext<SshInput, SshOutput, String>> {

    public SshInput(String name) {
        super(name);
    }

    @Override
    public void preSendUpdate(ExecutionContext<SshInput, SshOutput, String> context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String generateProtocolMessage(ExecutionContext<SshInput, SshOutput, String> context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void postSendUpdate(ExecutionContext<SshInput, SshOutput, String> context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void postReceiveUpdate(SshOutput output, OutputChecker<SshOutput> outputChecker,
            ExecutionContext<SshInput, SshOutput, String> context) {
        throw new UnsupportedOperationException();
    }
}
