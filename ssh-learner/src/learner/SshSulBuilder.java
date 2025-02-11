package learner;

import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.AbstractSul;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.SulBuilder;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.config.SulConfig;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.context.ExecutionContext;
import com.github.protocolfuzzing.protocolstatefuzzer.utils.CleanupTasks;
import java.io.IOException;

public class SshSulBuilder implements SulBuilder<SshInput, SshOutput, ExecutionContext<SshInput, SshOutput, String>> {

    @Override
    public AbstractSul<SshInput, SshOutput, ExecutionContext<SshInput, SshOutput, String>> build(SulConfig sulConfig,
            CleanupTasks cleanupTasks) {
        try {
            AbstractSul<SshInput, SshOutput, ExecutionContext<SshInput, SshOutput, String>> sshSulConfig = null;
            if (sulConfig.isFuzzingClient()) {
                sshSulConfig = new SshMapperSul(
                        (SshSulClientConfig) sulConfig, cleanupTasks);
                return sshSulConfig;
            } else {
                sshSulConfig = new SshMapperSul(
                        (SshSulServerConfig) sulConfig, cleanupTasks);
                return sshSulConfig;
            }
        } catch (IOException e) {
            throw new MapperException("Error creating SshMapperSul", e);
        }
    }

}
