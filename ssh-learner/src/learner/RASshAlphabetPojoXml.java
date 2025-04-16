package learner;

import com.github.protocolfuzzing.protocolstatefuzzer.components.learner.alphabet.xml.AlphabetPojoXml;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@XmlRootElement(name = "alphabet")
@XmlAccessorType(XmlAccessType.FIELD)
public class RASshAlphabetPojoXml extends AlphabetPojoXml<RASshInput> {

    @XmlElements(value = {
            @XmlElement(type = RASshInputPojoXml.class, name = "RASshInput")
    })
    private List<RASshInputPojoXml> inputs = new ArrayList<>();

    public List<RASshInput> getInputs() {
        System.out.println("getInputs: " + inputs);
        List<RASshInput> allInputs = inputs.stream().map(xmlInput -> {
            List<RASshParams> params = xmlInput.getParams().stream()
                    .map(param -> new RASshParams(param.getName(), param.getType(), param.getValue(),
                            param.getBaseClass()))
                    .collect(Collectors.toList());
            return new RASshInput(xmlInput.getName(), params);
        }).collect(Collectors.toList());
        return allInputs;
    }

    // maybe not neeeded
    // public void setInputs(List<RASshInput> inputs) {
    // this.inputs = inputs;
    // }
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class RASshInputPojoXml {

        @XmlAttribute(name = "name", required = true)
        private String name;

        @XmlElement(name = "param")
        private List<ParamPojoXml> params = new ArrayList<>();

        public String getName() {
            return name;
        }

        public List<ParamPojoXml> getParams() {
            return params;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setParams(List<ParamPojoXml> params) {
            this.params = params;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ParamPojoXml {

        @XmlAttribute(name = "pName", required = true)
        private String name;

        @XmlAttribute(name = "type", required = true)
        private String type;

        @XmlAttribute(name = "baseClass", required = true)
        private String baseClass;

        @XmlAttribute(name = "value", required = true)
        private String value;

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public String getBaseClass() {
            return baseClass;
        }

        public String getValue() {
            return value;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setBaseClass(String baseClass) {
            this.baseClass = baseClass;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}