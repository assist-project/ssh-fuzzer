package learner;

import java.util.ArrayList;
import java.util.List;

import de.learnlib.ralib.data.DataType;
import de.learnlib.ralib.data.DataValue;
import de.learnlib.ralib.words.OutputSymbol;

public class RASshOutput extends OutputSymbol {
    // private RASshInput input;
    // private DataValue<?>[] values;
    private String name;
    private List<RASshParams> params;

    public RASshOutput(String name, List<RASshParams> paramList) {
        super(name, paramList.stream().map(param -> {
            return new DataType(param.getType(), RASshInput.getClassFromString(param.getClassName()));
        }).toArray(DataType[]::new));
        this.name = name;
        this.params = paramList;
    }

    public static DataValue<?>[] toDataValues(RASshInput input) {
        List<DataValue<?>> dataValues = new ArrayList<>();
        List<RASshParams> params = input.getParams();
        DataType[] types = input.getPtypes(); // from ParameterizedSymbol

        for (int i = 0; i < params.size(); i++) {
            RASshParams param = params.get(i);
            DataType type = types[i];

            // Convert value string to appropriate type
            Object value = param.getClassName();

            // Create DataValue
            dataValues.add(new DataValue<>(type, value));
        }

        return dataValues.toArray(new DataValue[0]);
    }

    public String getName() {
        return this.name;
    }

    public List<RASshParams> getParams() {
        return params;
    }

    public void addParam(String name, String type, String value, String className) {
        this.params.add(new RASshParams(name, type, value, className));
    }
}