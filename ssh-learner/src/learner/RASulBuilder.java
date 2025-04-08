package learner;

import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.AbstractSul;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.SulBuilder;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.config.SulConfig;
import com.github.protocolfuzzing.protocolstatefuzzer.utils.CleanupTasks;

import java.io.IOException;

public class RASulBuilder
        implements SulBuilder<RASshOutput, RASshOutput, Object> {

    @Override
    public AbstractSul<RASshOutput, RASshOutput, Object> build(
            SulConfig sulConfig,
            CleanupTasks cleanupTasks) {
        try {
            AbstractSul<RASshOutput, RASshOutput, Object> sshSulConfig = null;
            sshSulConfig = new RASshMapperSul((SshSulServerConfig) sulConfig, cleanupTasks);
            return sshSulConfig;
            // }
        } catch (IOException e) {
            throw new MapperException("Error creating SshMapperSul", e);
        }
    }

}