package learner;

import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.AbstractSul;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.SulBuilder;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.config.SulConfig;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.context.ExecutionContext;
import com.github.protocolfuzzing.protocolstatefuzzer.utils.CleanupTasks;

import java.io.IOException;

public class RASulBuilder
        implements SulBuilder<RASshInput, RASshInput, ExecutionContext<RASshInput, RASshInput, String>> {

    @Override
    public AbstractSul<RASshInput, RASshInput, ExecutionContext<RASshInput, RASshInput, String>> build(
            SulConfig sulConfig,
            CleanupTasks cleanupTasks) {
        try {
            AbstractSul<RASshInput, RASshInput, ExecutionContext<RASshInput, RASshInput, String>> sshSulConfig = null;
            sshSulConfig = new RASshMapperSul((SshSulServerConfig) sulConfig, cleanupTasks);
            return sshSulConfig;
            // }
        } catch (IOException e) {
            throw new MapperException("Error creating SshMapperSul", e);
        }
    }

}