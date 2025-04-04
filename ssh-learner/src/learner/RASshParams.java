package learner;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class RASshParams {
    @XmlAttribute(name = "name")
    private String name;
    @XmlAttribute(name = "type")
    private String type;
    @XmlElement(name = "value")
    private String value;
    @XmlAttribute(name = "baseClass")
    private String className;

    public RASshParams(String name, String type, String value, String className) {
        this.name = name; // input name e.g. KEXINIT
        this.type = type; // type e.g. string, int
        this.value = value; // value e.g. ext-info-c
        this.className = className;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public String getClassName() {
        return className;
    }
}
