package learner;

import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.AbstractSul;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.SulBuilder;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.config.SulConfig;
import com.github.protocolfuzzing.protocolstatefuzzer.utils.CleanupTasks;

import de.learnlib.ralib.words.PSymbolInstance;

public class RASshSulBuilderAdapter implements SulBuilder<PSymbolInstance, PSymbolInstance, Object> {

    private final RASulBuilder realBuilder;

    public RASshSulBuilderAdapter(RASulBuilder realBuilder) {
        this.realBuilder = realBuilder;
    }

    @Override
    public AbstractSul<PSymbolInstance, PSymbolInstance, Object> build(
            SulConfig sulConfig, CleanupTasks cleanupTasks) {

        // Build the real RASshOutput SUL using your original builder
        AbstractSul<RASshOutput, RASshOutput, Object> realSul = realBuilder.build(sulConfig, cleanupTasks);

        // Wrap it with your adapter
        return new RASshSulAdapter(realSul);
    }
}
