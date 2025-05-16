package learner;

import com.github.protocolfuzzing.protocolstatefuzzer.components.learner.alphabet.PSFOutputSymbols;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.abstractsymbols.OutputBuilder;

import de.learnlib.ralib.words.OutputSymbol;
import de.learnlib.ralib.words.PSymbolInstance;

public class RASshOutputBuilder extends OutputBuilder<PSymbolInstance> {

    public PSymbolInstance buildOutput(String name) {
        OutputSymbol baseSymbol = new OutputSymbol(name);
        return new PSymbolInstance(baseSymbol);
    }

    public PSymbolInstance buildOutputExact(String name) {
        return buildOutput(name);
    }

    public PSymbolInstance buildOutput(PSFOutputSymbols type) {
        OutputSymbol baseSymbol = new OutputSymbol(type.name());
        return new PSymbolInstance(baseSymbol);
    }

}
