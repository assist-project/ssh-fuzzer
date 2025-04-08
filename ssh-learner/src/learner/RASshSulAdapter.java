package learner;
// Adapter from PSymbolInstance to your RASshOutput

import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.AbstractSul;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.SulAdapter;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.config.SulConfig;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.sulwrappers.DynamicPortProvider;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.Mapper;
import com.github.protocolfuzzing.protocolstatefuzzer.utils.CleanupTasks;

import de.learnlib.ralib.words.PSymbolInstance;

public class RASshSulAdapter implements AbstractSul<PSymbolInstance, PSymbolInstance, Object> {

    private final AbstractSul<RASshOutput, RASshOutput, Object> realSul;

    public RASshSulAdapter(AbstractSul<RASshOutput, RASshOutput, Object> sul) {
        this.realSul = sul;
    }

    @Override
    public void pre() {
        realSul.pre();
    }

    @Override
    public PSymbolInstance step(PSymbolInstance input) {
        RASshOutput output = realSul.step((RASshOutput) input); // Cast safely if guaranteed
        return output; // RASshOutput is a PSymbolInstance
    }

    @Override
    public void post() {
        realSul.post();
    }

    @Override
    public SulConfig getSulConfig() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSulConfig'");
    }

    @Override
    public CleanupTasks getCleanupTasks() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCleanupTasks'");
    }

    @Override
    public void setDynamicPortProvider(DynamicPortProvider dynamicPortProvider) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setDynamicPortProvider'");
    }

    @Override
    public DynamicPortProvider getDynamicPortProvider() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDynamicPortProvider'");
    }

    @Override
    public Mapper<PSymbolInstance, PSymbolInstance, Object> getMapper() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMapper'");
    }

    @Override
    public SulAdapter getSulAdapter() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSulAdapter'");
    }
}
