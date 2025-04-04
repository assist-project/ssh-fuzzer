package learner;

import java.util.LinkedHashMap;
import java.util.Map;

import com.github.protocolfuzzing.protocolstatefuzzer.components.learner.alphabet.AlphabetBuilder;
import com.github.protocolfuzzing.protocolstatefuzzer.components.learner.alphabet.AlphabetBuilderStandard;
import com.github.protocolfuzzing.protocolstatefuzzer.components.learner.alphabet.xml.AlphabetSerializerXml;
import com.github.protocolfuzzing.protocolstatefuzzer.components.learner.statistics.MealyMachineWrapper;
import com.github.protocolfuzzing.protocolstatefuzzer.components.learner.statistics.RegisterAutomatonWrapper;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.SulBuilder;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.SulWrapperStandard;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.mapper.context.ExecutionContext;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.core.StateFuzzer;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.core.StateFuzzerBuilder;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.core.StateFuzzerComposerStandard;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.core.StateFuzzerStandard;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.core.config.StateFuzzerClientConfig;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.core.config.StateFuzzerConfigBuilder;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.core.config.StateFuzzerEnabler;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.core.config.StateFuzzerServerConfig;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.testrunner.core.TestRunner;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.testrunner.core.TestRunnerBuilder;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.testrunner.core.TestRunnerStandard;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.testrunner.core.config.TestRunnerEnabler;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.testrunner.timingprobe.TimingProbe;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.testrunner.timingprobe.TimingProbeBuilder;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.testrunner.timingprobe.TimingProbeStandard;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.testrunner.timingprobe.config.TimingProbeEnabler;

import de.learnlib.ralib.data.Constants;
import de.learnlib.ralib.data.DataType;
import de.learnlib.ralib.theory.Theory;
import de.learnlib.ralib.words.PSymbolInstance;
import de.learnlib.ralib.words.ParameterizedSymbol;

public class RAMultiBuilder
        implements StateFuzzerConfigBuilder,
        StateFuzzerBuilder<RegisterAutomatonWrapper<ParameterizedSymbol, PSymbolInstance>>,
        TestRunnerBuilder, TimingProbeBuilder {

    @SuppressWarnings("rawtypes")
    final Map<DataType, Theory> teachers = new LinkedHashMap<>();

    // AlphabetPojoXmlImpl needs to be implemented
    protected AlphabetBuilder<RASshInput> alphabetBuilder = new AlphabetBuilderStandard<RASshInput>(
            new AlphabetSerializerXml<RASshInput, RASshAlphabetPojoXml>(RASshInput.class,
                    RASshAlphabetPojoXml.class));

    protected SulBuilder<RASshInput, RASshInput, ExecutionContext<RASshInput, RASshInput, String>> sulBuilder = new RASulBuilder();

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
    public StateFuzzer<RegisterAutomatonWrapper<ParameterizedSymbol, PSymbolInstance>> build(
            StateFuzzerEnabler stateFuzzerEnabler) {
        StateFuzzerComposerStandard<ParameterizedSymbol, PSymbolInstance, ExecutionContext<ParameterizedSymbol, PSymbolInstance, Object>> stateFuzzerComposer = new StateFuzzerComposerStandard<ParameterizedSymbol, PSymbolInstance, ExecutionContext<ParameterizedSymbol, PSymbolInstance, Object>>(
                stateFuzzerEnabler, alphabetBuilder,
                sulBuilder, sulWrapper).initialize();

        StateFuzzerStandard<ParameterizedSymbol, PSymbolInstance> stateFuzzerStd = new StateFuzzerStandard<>(
                stateFuzzerComposer);
        return stateFuzzerStd;
    }

    @Override
    public TestRunner build(TestRunnerEnabler testRunnerEnabler) {
        return new TestRunnerStandard<ParameterizedSymbol, PSymbolInstance, Object, ExecutionContext<ParameterizedSymbol, PSymbolInstance, Object>>(
                testRunnerEnabler, alphabetBuilder,
                sulBuilder, sulWrapper).initialize();
    }

    @Override
    public TimingProbe build(TimingProbeEnabler timingProbeEnabler) {
        return new TimingProbeStandard<ParameterizedSymbol, PSymbolInstance, Object, ExecutionContext<ParameterizedSymbol, PSymbolInstance, Object>>(
                timingProbeEnabler, alphabetBuilder, sulBuilder, sulWrapper).initialize();
    }
}
