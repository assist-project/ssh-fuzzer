package learner;

import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.Mapper;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.abstractsymbols.OutputBuilder;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.abstractsymbols.OutputChecker;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.config.MapperConfig;

import de.learnlib.ralib.words.PSymbolInstance;

public class RASshMapper
        implements Mapper<PSymbolInstance, PSymbolInstance, Object> {

    private OutputBuilder<PSymbolInstance> outputBuilder = new RASshOutputBuilder();
    private OutputChecker<PSymbolInstance> outputChecker = new DummyOutputChecker();

    @Override
    public PSymbolInstance execute(PSymbolInstance input, Object context) {
        return input;
    }

    @Override
    public MapperConfig getMapperConfig() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMapperConfig'");
    }

    @Override
    public OutputBuilder<PSymbolInstance> getOutputBuilder() {
        return this.outputBuilder;
    }

    @Override
    public OutputChecker<PSymbolInstance> getOutputChecker() {
        return this.outputChecker;
    }

}

class DummyOutputChecker implements OutputChecker<PSymbolInstance> {

    @Override
    public boolean hasInitialClientMessage(PSymbolInstance output) {
        return false;
    }

    @Override
    public boolean isTimeout(PSymbolInstance output) {
        return false;
    }

    @Override
    public boolean isUnknown(PSymbolInstance output) {
        return false;
    }

    @Override
    public boolean isSocketClosed(PSymbolInstance output) {
        return false;
    }

    @Override
    public boolean isDisabled(PSymbolInstance output) {
        return false;
    }

}