package learner;

import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.Mapper;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.abstractsymbols.OutputBuilder;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.abstractsymbols.OutputChecker;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.config.MapperConfig;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.context.ExecutionContext;

public class RASshMapper
        implements Mapper<RASshInput, RASshInput, ExecutionContext<RASshInput, RASshInput, String>> {

    @Override
        public RASshInput execute(RASshInput input, ExecutionContext<RASshInput, RASshInput, String> context) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'execute'");
        }

        @Override
        public MapperConfig getMapperConfig() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMapperConfig'");
        }

        @Override
        public OutputBuilder<RASshInput> getOutputBuilder() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getOutputBuilder'");
        }

        @Override
        public OutputChecker<RASshInput> getOutputChecker() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getOutputChecker'");
        }

    // public RASshMapper(MapperConfig mapperConfig, OutputBuilder<RASshInput>
    // outputBuilder) {
    // super(mapperConfig, outputBuilder);
    // // TODO Auto-generated constructor stub
    // }

    // @Override
    // public RASshInput receiveOutput(ExecutionContext<RASshInput, RASshInput,
    // String> context) {
    // // TODO Auto-generated method stub
    // throw new UnsupportedOperationException("Unimplemented method
    // 'receiveOutput'");
    // }

    // @Override
    // protected RASshInput buildOutput(String name, List<RASshInput> messages) {
    // return messages.get(0);
    // }

}