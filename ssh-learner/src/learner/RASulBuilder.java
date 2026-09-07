package learner;

import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.AbstractSul;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.SulBuilder;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.config.SulConfig;
import com.github.protocolfuzzing.protocolstatefuzzer.utils.CleanupTasks;

import de.learnlib.ralib.words.PSymbolInstance;
import net.automatalib.alphabet.Alphabet;

import java.io.IOException;

public class RASulBuilder
        implements SulBuilder<PSymbolInstance, PSymbolInstance, Object> {

    protected Alphabet<RASshInput> alphabet;

    public RASulBuilder() {
        // Alphabet<RASshInput> alphabet
        // this.alphabet = alphabet;
    }

    @Override
    public AbstractSul<PSymbolInstance, PSymbolInstance, Object> build(
            SulConfig sulConfig,
            CleanupTasks cleanupTasks) {
        try {
            return new RASshMapperSul(
                    (RASulServerConfig) sulConfig, cleanupTasks);
            // }
        } catch (IOException e) {
            throw new MapperException("Error creating SshMapperSul", e);
        }
    }

}