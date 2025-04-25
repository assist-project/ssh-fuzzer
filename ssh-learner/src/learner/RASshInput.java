package learner;

import java.util.ArrayList;
import java.util.List;
import de.learnlib.ralib.data.DataType;
import de.learnlib.ralib.data.DataValue;
import de.learnlib.ralib.words.InputSymbol;

public class RASshInput
        extends
        InputSymbol {
    private String name;
    private List<RASshParams> params;

    public RASshInput(String name, List<RASshParams> paramList) {
        super(name, paramList.stream().map(param -> {
            return new DataType(param.getType(), getClassFromString(param.getClassName()));
        }).toArray(DataType[]::new)); // Fill in ptypes from the param list
        this.name = name;
        this.params = paramList;
    }

    public RASshInput(String name, DataType[] data) {
        super(name, data);
        this.name = name;
        this.params = new ArrayList<>();
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
        return name;
    }

    public List<RASshParams> getParams() {
        return params;
    }

    public void addParam(String name, String type, String value, String className) {
        this.params.add(new RASshParams(name, type, value, className));
    }

    // Helper method to convert string class name to Class object
    private static Class<?> getClassFromString(String className) {
        try {
            return Class.forName(className); // Convert class name to Class object
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Class not found: " + className, e);
        }
    }

    public String toString() {
        return super.toString();
    }
}
