package learner;

import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.Mapper;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.abstractsymbols.OutputBuilder;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.abstractsymbols.OutputChecker;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.config.MapperConfig;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.context.ExecutionContext;

public class SshMapper implements Mapper<SshInput, SshOutput, ExecutionContext<SshInput, SshOutput, String>> {

    @Override
    public SshOutput execute(SshInput input, ExecutionContext<SshInput, SshOutput, String> context) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'execute'");
    }

    @Override
    public MapperConfig getMapperConfig() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMapperConfig'");
    }

    @Override
    public OutputBuilder<SshOutput> getOutputBuilder() {
        // TODO Auto-generated method stub
        return new SshOutputBuilder();
    }

    @Override
    public OutputChecker<SshOutput> getOutputChecker() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getOutputChecker'");
    }

}
