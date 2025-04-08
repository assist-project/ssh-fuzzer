package learner;

import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.Mapper;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.abstractsymbols.OutputBuilder;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.abstractsymbols.OutputChecker;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.config.MapperConfig;

public class RASshMapper
        implements Mapper<RASshOutput, RASshOutput, Object> {

    @Override
    public RASshOutput execute(RASshOutput input, Object context) {
        // TODO Auto-generated method stub
        return input;
    }

    @Override
    public MapperConfig getMapperConfig() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMapperConfig'");
    }

    @Override
    public OutputBuilder<RASshOutput> getOutputBuilder() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getOutputBuilder'");
    }

    @Override
    public OutputChecker<RASshOutput> getOutputChecker() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getOutputChecker'");
    }
}