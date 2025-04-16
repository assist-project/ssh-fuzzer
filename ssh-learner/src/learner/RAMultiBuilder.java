package learner;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.github.protocolfuzzing.protocolstatefuzzer.components.learner.alphabet.AlphabetBuilder;
import com.github.protocolfuzzing.protocolstatefuzzer.components.learner.alphabet.AlphabetBuilderStandard;
import com.github.protocolfuzzing.protocolstatefuzzer.components.learner.alphabet.xml.AlphabetSerializerXml;
import com.github.protocolfuzzing.protocolstatefuzzer.components.learner.statistics.RegisterAutomatonWrapper;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.SulBuilder;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.SulWrapper;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.SulWrapperStandard;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.core.StateFuzzer;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.core.StateFuzzerBuilder;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.core.StateFuzzerComposerRA;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.core.StateFuzzerRA;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.core.config.StateFuzzerClientConfig;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.core.config.StateFuzzerConfigBuilder;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.core.config.StateFuzzerEnabler;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.core.config.StateFuzzerServerConfig;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.testrunner.core.TestRunner;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.testrunner.core.TestRunnerBuilder;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.testrunner.core.config.TestRunnerEnabler;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.testrunner.timingprobe.TimingProbe;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.testrunner.timingprobe.TimingProbeBuilder;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.testrunner.timingprobe.config.TimingProbeEnabler;
import de.learnlib.ralib.data.DataType;
import de.learnlib.ralib.theory.Theory;
import de.learnlib.ralib.words.PSymbolInstance;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;

public class RAMultiBuilder
        implements StateFuzzerConfigBuilder,
        StateFuzzerBuilder<RegisterAutomatonWrapper<RASshInput, PSymbolInstance>>, TestRunnerBuilder,
        TimingProbeBuilder {

    // AlphabetPojoXmlImpl needs to be implemented
    protected AlphabetBuilder<RASshInput> alphabetBuilder = new AlphabetBuilderStandard<RASshInput>(
            new AlphabetSerializerXml<RASshInput, RASshAlphabetPojoXml>(RASshInput.class, RASshAlphabetPojoXml.class));

    protected SulBuilder<PSymbolInstance, PSymbolInstance, Object> sulBuilder;

    // TODO: Delete new RASshSulBuilderAdapter() class

    SulWrapper<PSymbolInstance, PSymbolInstance, Object> sulWrapper = new SulWrapperStandard<PSymbolInstance, PSymbolInstance, Object>();

    // SulClientConfigImpl and MapperConfigImpl need to be implemented
    @Override
    public StateFuzzerClientConfig buildClientConfig() {
        return new SshStateFuzzerClientConfig(new SshSulClientConfig());
    }

    // SulServerConfigImpl (and MapperConfigImpl) need to be implemented
    @Override
    public StateFuzzerServerConfig buildServerConfig() {
        return new SshStateFuzzerServerConfig(new RASulServerConfig());
    }

    @Override
    public StateFuzzer<RegisterAutomatonWrapper<RASshInput, PSymbolInstance>> build(
            StateFuzzerEnabler stateFuzzerEnabler) {
        // RASshAlphabetBuilder();

        // Alphabet<RASshInput> alphabet =
        // alphabetBuilder.build(stateFuzzerEnabler.getLearnerConfig());

        // for (RASshInput input : alphabet) {
        // DataType[] paramTypes = input.getPtypes();
        // builder.withInput(input, paramTypes);
        // }

        // final DataType CH_OPEN = new DataType("CH_OPEN", Integer.class);
        // IntegerEqualityTheory theory = new IntegerEqualityTheory(CH_OPEN);

        @SuppressWarnings("rawtypes")
        final Map<DataType, Theory> teachers = new LinkedHashMap<>();
        // teachers.put(CH_OPEN, theory);

        sulBuilder = new RASulBuilder();
        StateFuzzerComposerRA<RASshInput, Object> stateFuzzerComposer = new StateFuzzerComposerRA<RASshInput, Object>(
                stateFuzzerEnabler, alphabetBuilder,
                sulBuilder, sulWrapper, teachers);

        return new StateFuzzerRA<RASshInput, Object>(stateFuzzerComposer.initialize());
    }

    @Override
    public TimingProbe build(TimingProbeEnabler timingProbeEnabler) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'build'");
    }

    @Override
    public TestRunner build(TestRunnerEnabler testRunnerEnabler) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'build'");
    }

    public void RASshAlphabetBuilder() {
        try {
            File file = new File("inputs/alphabets/servers/ra_input.xml");

            // 2. Create JAXB context with your class
            JAXBContext jaxbContext = JAXBContext.newInstance(RASshAlphabetPojoXml.class);

            // 3. Create unmarshaller
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            // 4. Unmarshal the XML to POJO
            RASshAlphabetPojoXml alphabetPojo = (RASshAlphabetPojoXml) jaxbUnmarshaller.unmarshal(file);

            System.out.println("Unmarshalled POJO: " + alphabetPojo);
            System.out.println("Raw inputs: " + alphabetPojo.getInputs());

            // 5. Get the RASshInput list
            List<RASshInput> inputs = alphabetPojo.getInputs();

            // 6. Print the inputs
            System.out.println("Parsed Inputs:");
            for (RASshInput input : inputs) {
                System.out.println("  " + input.toString());

                // Optional: print data values
                System.out.println("    DataValues:");
                for (var dv : RASshInput.toDataValues(input)) {
                    System.out.println("      " + dv);
                }
            }
        } catch (

        Exception e) {
            e.printStackTrace();
        }
    }

}
