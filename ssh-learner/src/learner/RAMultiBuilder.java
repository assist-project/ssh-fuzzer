package learner;

import java.util.LinkedHashMap;
import java.util.Map;

import com.github.protocolfuzzing.protocolstatefuzzer.components.learner.alphabet.AlphabetBuilder;
import com.github.protocolfuzzing.protocolstatefuzzer.components.learner.alphabet.AlphabetBuilderStandard;
import com.github.protocolfuzzing.protocolstatefuzzer.components.learner.alphabet.xml.AlphabetSerializerXml;
import com.github.protocolfuzzing.protocolstatefuzzer.components.learner.statistics.RegisterAutomatonWrapper;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.SulBuilder;
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

public class RAMultiBuilder
        implements StateFuzzerConfigBuilder,
        StateFuzzerBuilder<RegisterAutomatonWrapper<RASshInput, PSymbolInstance>>, TestRunnerBuilder,
        TimingProbeBuilder {

    @SuppressWarnings("rawtypes")
    final Map<DataType, Theory> teachers = new LinkedHashMap<>();

    // AlphabetPojoXmlImpl needs to be implemented
    protected AlphabetBuilder<RASshInput> alphabetBuilder = new AlphabetBuilderStandard<RASshInput>(
            new AlphabetSerializerXml<RASshInput, RASshAlphabetPojoXml>(RASshInput.class,
                    RASshAlphabetPojoXml.class));

    protected SulBuilder<PSymbolInstance, PSymbolInstance, Object> sulBuilder = new RASshSulBuilderAdapter(
            new RASulBuilder());

    SulWrapperStandard<PSymbolInstance, PSymbolInstance, Object> sulWrapper = new SulWrapperStandard<PSymbolInstance, PSymbolInstance, Object>();

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

        StateFuzzerComposerRA<RASshInput, Object> stateFuzzerComposer = new StateFuzzerComposerRA<RASshInput, Object>(
                stateFuzzerEnabler, alphabetBuilder,
                sulBuilder, sulWrapper, teachers);

        return new StateFuzzerRA<RASshInput, Object>(
                stateFuzzerComposer.initialize());
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

}
