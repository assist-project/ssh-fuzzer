package learner;

public class RASshParams {
    private String name;
    private String type;
    private String value;
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
