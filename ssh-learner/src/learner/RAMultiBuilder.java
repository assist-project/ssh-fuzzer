package learner;

import java.util.LinkedHashMap;
import java.util.Map;
import com.github.protocolfuzzing.protocolstatefuzzer.components.learner.alphabet.AlphabetBuilder;
import com.github.protocolfuzzing.protocolstatefuzzer.components.learner.alphabet.AlphabetBuilderStandard;
import com.github.protocolfuzzing.protocolstatefuzzer.components.learner.config.LearnerConfigRA;
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
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.core.config.StateFuzzerServerConfigStandard;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.testrunner.core.TestRunner;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.testrunner.core.TestRunnerBuilder;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.testrunner.core.config.TestRunnerConfigStandard;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.testrunner.core.config.TestRunnerEnabler;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.testrunner.timingprobe.TimingProbe;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.testrunner.timingprobe.TimingProbeBuilder;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.testrunner.timingprobe.config.TimingProbeConfigStandard;
import com.github.protocolfuzzing.protocolstatefuzzer.statefuzzer.testrunner.timingprobe.config.TimingProbeEnabler;
import de.learnlib.ralib.data.DataType;
import de.learnlib.ralib.theory.Theory;
import de.learnlib.ralib.words.PSymbolInstance;
import de.learnlib.ralib.words.ParameterizedSymbol;

public class RAMultiBuilder
        implements StateFuzzerConfigBuilder,
        StateFuzzerBuilder<RegisterAutomatonWrapper<ParameterizedSymbol, PSymbolInstance>>, TestRunnerBuilder,
        TimingProbeBuilder {

    protected AlphabetBuilder<ParameterizedSymbol> alphabetBuilder = new AlphabetBuilderStandard<ParameterizedSymbol>(
            new RASshSerializer(RASshInput.class,
                    RASshAlphabetPojoXml.class));

    protected SulBuilder<PSymbolInstance, PSymbolInstance, Object> sulBuilder;

    SulWrapper<PSymbolInstance, PSymbolInstance, Object> sulWrapper = new SulWrapperStandard<PSymbolInstance, PSymbolInstance, Object>();

    @Override
    public StateFuzzerClientConfig buildClientConfig() {
        return new SshStateFuzzerClientConfig(new SshSulClientConfig());
    }

    @Override
    public StateFuzzerServerConfig buildServerConfig() {
        return new StateFuzzerServerConfigStandard(
                new LearnerConfigRA(),
                new RASulServerConfig(),
                new TestRunnerConfigStandard(),
                new TimingProbeConfigStandard());
    }

    @Override
    public StateFuzzer<RegisterAutomatonWrapper<ParameterizedSymbol, PSymbolInstance>> build(
            StateFuzzerEnabler stateFuzzerEnabler) {

        @SuppressWarnings("rawtypes")
        final Map<DataType, Theory> teachers = new LinkedHashMap<>();

        sulBuilder = new RASulBuilder();
        StateFuzzerComposerRA<ParameterizedSymbol, Object> stateFuzzerComposer = new StateFuzzerComposerRA<ParameterizedSymbol, Object>(
                stateFuzzerEnabler, alphabetBuilder,
                sulBuilder, sulWrapper, teachers);

        return new StateFuzzerRA<ParameterizedSymbol, Object>(stateFuzzerComposer.initialize());
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
