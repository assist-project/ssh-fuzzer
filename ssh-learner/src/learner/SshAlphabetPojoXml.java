package learner;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;

import com.github.protocolfuzzing.protocolstatefuzzer.components.learner.alphabet.xml.AlphabetPojoXml;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.abstractsymbols.AbstractInput;


@XmlRootElement(name = "alphabet")
@XmlAccessorType(XmlAccessType.FIELD)
public class SshAlphabetPojoXml extends AlphabetPojoXml {
    
    @XmlElements(value = {
            @XmlElement(type = SshInput.class, name = "SshInput")
    })
    private List<SshInput> inputs;
    
    public SshAlphabetPojoXml() {
    }
    
    
    public List<AbstractInput> getInputs() {
        return new ArrayList<>(inputs);
    }
}
