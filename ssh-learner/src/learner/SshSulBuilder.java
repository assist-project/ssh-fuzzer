package learner;

import java.io.IOException;

import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.SulBuilder;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.config.SulConfig;
import com.github.protocolfuzzing.protocolstatefuzzer.utils.CleanupTasks;

public class SshSulBuilder implements SulBuilder {

    @Override
    public SshSul build(SulConfig sulConfig, CleanupTasks cleanupTasks) {
        try {
            if (sulConfig.isFuzzingClient()) {
                    return new SshSul((SshSulClientConfig) sulConfig, cleanupTasks);
                
            } else {
                return new SshSul((SshSulServerConfig) sulConfig, cleanupTasks);
            }
        } catch (IOException  e) {
            e.printStackTrace();
        } 
        return null;
    }

}
