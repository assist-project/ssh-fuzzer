package learner;

import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.SulBuilder;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.config.SulConfig;
import com.github.protocolfuzzing.protocolstatefuzzer.utils.CleanupTasks;
import java.io.IOException;

public class SshSulBuilder implements SulBuilder {

    @Override
    public SshMapperSul build(SulConfig sulConfig, CleanupTasks cleanupTasks) {
        try {
            if (sulConfig.isFuzzingClient()) {
                    return new SshMapperSul((SshSulClientConfig) sulConfig, cleanupTasks);
            } else {
                return new SshMapperSul((SshSulServerConfig) sulConfig, cleanupTasks);
            }
        } catch (IOException  e) {
            throw new MapperException("Error creating SshMapperSul", e);
        }
    }

}
