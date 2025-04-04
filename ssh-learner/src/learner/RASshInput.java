package learner;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import de.learnlib.ralib.data.DataType;
import de.learnlib.ralib.words.ParameterizedSymbol;

@XmlAccessorType(XmlAccessType.FIELD)
public class RASshInput
        extends ParameterizedSymbol {
    @XmlAttribute(name = "name")
    private String name;
    @XmlElement(name = "param")
    private List<RASshParams> params;

    public RASshInput(String name, List<RASshParams> paramList) {
        super(name, paramList.stream().map(param -> {
            return new DataType(param.getType(), getClassFromString(param.getClassName()));
        }).toArray(DataType[]::new)); // Fill in ptypes from the param list
        this.name = name;
        this.params = new ArrayList<>();
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
}
