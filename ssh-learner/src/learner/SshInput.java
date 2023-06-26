package learner;

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
    }

    @Override
    public SshMessage generateProtocolMessage(ExecutionContext context) {
        return new SshMessage(super.getName());
    }

    @Override
    public void postSendUpdate(ExecutionContext context) {
    }

    @Override
    public void postReceiveUpdate(AbstractOutput output, AbstractOutputChecker abstractOutputChecker,
            ExecutionContext context) {
    }

    @Override
    public Enum<?> getInputType() {
        return null;
    }

}
