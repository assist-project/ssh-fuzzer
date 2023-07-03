package learner;

import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.protocol.ProtocolMessage;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.abstractsymbols.AbstractInput;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.abstractsymbols.AbstractOutput;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.abstractsymbols.AbstractOutputChecker;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.context.ExecutionContext;

public class SshInput extends AbstractInput {

    public SshInput(String name) {
        super(name);
    }

    @Override
    public void preSendUpdate(ExecutionContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ProtocolMessage generateProtocolMessage(ExecutionContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void postSendUpdate(ExecutionContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void postReceiveUpdate(AbstractOutput output, AbstractOutputChecker abstractOutputChecker,
            ExecutionContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Enum<?> getInputType() {
        throw new UnsupportedOperationException();
    }
}
