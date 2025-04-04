package learner;

import java.util.ArrayList;
import java.util.List;

import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.abstractsymbols.OutputBuilder;

public class RASshInputBuilder implements OutputBuilder<RASshInput> {

    @Override
    public RASshInput buildOutput(String name) {
        List<RASshParams> params = new ArrayList<>();
        return new RASshInput(name, params); // Create an empty input with a name
    }

    public RASshInput buildInput(String name, String[][] paramData) {
        List<RASshParams> params = new ArrayList<>();
        for (String[] data : paramData) {
            if (data.length == 4) {
                params.add(new RASshParams(data[0], data[1], data[2], data[3]));
            } else {
                throw new IllegalArgumentException("Each param entry must contain name, type, value, and class name.");
            }
        }
        RASshInput input = new RASshInput(name, params);
        return input;
    }
}
