package learner;

import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.protocol.ProtocolMessage;

public class SshMessage implements ProtocolMessage {
    private String name;

    public SshMessage(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
