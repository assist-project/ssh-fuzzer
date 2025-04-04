package learner;

import com.github.protocolfuzzing.protocolstatefuzzer.components.learner.alphabet.xml.AlphabetPojoXml;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "alphabet")
@XmlAccessorType(XmlAccessType.FIELD)
public class RASshAlphabetPojoXml extends AlphabetPojoXml<RASshInput> {

    // Parameterized constructor to initialize inputs
    public RASshAlphabetPojoXml(List<RASshInput> inputs) {
        super(inputs);
        System.out.println("Initializing RASshAlphabetPojoXml with inputs: " + inputs);
    }

    // Override the getInputs method to return the actual inputs
    @Override
    public List<RASshInput> getInputs() {
        return super.getInputs(); // Returns the list of inputs from AlphabetPojoXml
    }
}