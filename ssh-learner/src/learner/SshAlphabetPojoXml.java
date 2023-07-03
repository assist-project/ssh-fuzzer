package learner;

import com.github.protocolfuzzing.protocolstatefuzzer.components.learner.alphabet.xml.AlphabetPojoXml;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.abstractsymbols.AbstractInput;
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
public class SshAlphabetPojoXml extends AlphabetPojoXml {

    @XmlElements(value = {
            @XmlElement(type = SshInputPojoXml.class, name = "SshInput")
    })
    private List<SshInputPojoXml> xmlInputs;

    public SshAlphabetPojoXml() {
        xmlInputs = new ArrayList<>();
    }


    public List<AbstractInput> getInputs() {
        return xmlInputs.stream().map(xmlInput -> new SshInput(xmlInput.name)).collect(Collectors.toList());
    }

    public static class SshInputPojoXml {
        @XmlAttribute(name = "name", required = true)
        private String name;

        public SshInputPojoXml( ) {
        }
    }
}
