package learner;

import com.github.protocolfuzzing.protocolstatefuzzer.components.learner.alphabet.AlphabetSerializer;
import com.github.protocolfuzzing.protocolstatefuzzer.components.learner.alphabet.AlphabetSerializerException;
import com.github.protocolfuzzing.protocolstatefuzzer.components.learner.alphabet.xml.AlphabetPojoXml;

import de.learnlib.ralib.words.ParameterizedSymbol;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import learner.RASshAlphabet.Builder;
import net.automatalib.alphabet.Alphabet;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RASshSerializer implements AlphabetSerializer<ParameterizedSymbol> {

    /** Stores the singleton JAXB context. */
    protected JAXBContext context;

    /** Stores the constructor parameter. */
    protected Class<RASshInput> inputClass;

    /** Stores the constructor parameter. */
    protected Class<RASshAlphabetPojoXml> alphabetPojoXmlChildClass;

    /**
     * Returns the {@link #context} or creates and returns a new JAXBContext
     * if {@link #context} is null.
     *
     * @return the JAXBContext instance stored in {@link #context}
     *
     * @throws JAXBException if the instance cannot be created
     */
    protected synchronized JAXBContext getJAXBContext() throws JAXBException {
        if (context == null) {
            context = JAXBContext.newInstance(inputClass, alphabetPojoXmlChildClass);
        }
        return context;
    }

    public RASshSerializer(Class<RASshInput> inputClass, Class<RASshAlphabetPojoXml> alphabetPojoXmlChildClass) {
        this.inputClass = inputClass;
        this.alphabetPojoXmlChildClass = alphabetPojoXmlChildClass;
    }

    @Override
    public Alphabet<ParameterizedSymbol> read(InputStream alphabetStream) throws AlphabetSerializerException {
        try {
            Unmarshaller unmarshaller = getJAXBContext().createUnmarshaller();
            XMLInputFactory xif = XMLInputFactory.newFactory();
            xif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
            xif.setProperty(XMLInputFactory.SUPPORT_DTD, false);
            XMLStreamReader xsr = xif
                    .createXMLStreamReader(new InputStreamReader(alphabetStream, StandardCharsets.UTF_8));
            Object unmarshalled = unmarshaller.unmarshal(xsr);

            List<RASshInput> inputList = List.of();
            if (unmarshalled instanceof AlphabetPojoXml) {
                inputList = alphabetPojoXmlChildClass.cast(unmarshalled).getInputs();
            }
            Builder alphBuilder = new RASshAlphabet.Builder();
            alphBuilder.withOutput(new RASshInput("NO_CONN", new ArrayList<>()));
            alphBuilder.withOutput(new RASshInput("NO_RESP", new ArrayList<>()));
            alphBuilder.withOutput(new RASshInput("UNIMPL", new ArrayList<>()));
            alphBuilder.withOutput(new RASshInput("KEX31+NEWKEYS+BUFFERED", new ArrayList<>()));
            alphBuilder.withOutput(new RASshInput("UA_FAILURE", new ArrayList<>()));
            alphBuilder.withOutput(new RASshInput("SR_ACCEPT", new ArrayList<>()));
            alphBuilder.withOutput(new RASshInput("KEX31+NEWKEYS", new ArrayList<>()));

            for (ParameterizedSymbol a : inputList) {
                alphBuilder.withInput(a).withOutput(a)
                        .withOutput(new RASshInput(a.getName() + "+UNIMPL", new ArrayList<>()));
            }
            return alphBuilder.build();

        } catch (JAXBException | XMLStreamException e) {
            throw new AlphabetSerializerException("Cannot read the alphabet", e);
        }
    }

    @Override
    public void write(OutputStream alphabetStream, Alphabet<ParameterizedSymbol> alphabet)
            throws AlphabetSerializerException {
        try {
            Marshaller m = getJAXBContext().createMarshaller();
            m.setProperty(Marshaller.JAXB_FRAGMENT, true);
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            RASshAlphabetPojoXml alphabetPojo = alphabetPojoXmlChildClass.getConstructor(List.class)
                    .newInstance(new ArrayList<>(alphabet));
            m.marshal(alphabetPojo, alphabetStream);

        } catch (JAXBException | NoSuchMethodException | InvocationTargetException | IllegalAccessException
                | InstantiationException e) {
            throw new AlphabetSerializerException("Cannot write the alphabet", e);
        }
    }

    @Override
    public String getAlphabetFileExtension() {
        return ".xml";
    }
}
