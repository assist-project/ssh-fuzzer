package learner;

import com.github.protocolfuzzing.protocolstatefuzzer.components.learner.alphabet.AlphabetBuilder;
import com.github.protocolfuzzing.protocolstatefuzzer.components.learner.alphabet.AlphabetBuilderStandard;
import com.github.protocolfuzzing.protocolstatefuzzer.components.learner.alphabet.xml.AlphabetSerializerXml;
import com.github.protocolfuzzing.protocolstatefuzzer.components.learner.statistics.MealyMachineWrapper;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.SulBuilder;
import com.github.protocolfuzzing.protocolstatefuzzer.components.sul.core.SulWrapper;
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

public class MultiBuilder
        implements StateFuzzerConfigBuilder,
        StateFuzzerBuilder<MealyMachineWrapper<SshInput, SshOutput>>,
        TestRunnerBuilder, TimingProbeBuilder {

    // AlphabetPojoXmlImpl needs to be implemented
    protected AlphabetBuilder<SshInput> alphabetBuilder = new AlphabetBuilderStandard<SshInput>(
            new AlphabetSerializerXml<SshInput, SshAlphabetPojoXml>(SshInput.class, SshAlphabetPojoXml.class));

    // SulBuilderImpl needs to be implemented
    protected SulBuilder<SshInput, SshOutput, ExecutionContext<SshInput, SshOutput, String>> sulBuilder = new SshSulBuilder();
    protected SulWrapper<SshInput, SshOutput, ExecutionContext<SshInput, SshOutput, String>> sulWrapper = new SulWrapperStandard<>();

    // SulClientConfigImpl and MapperConfigImpl need to be implemented
    @Override
    public StateFuzzerClientConfig buildClientConfig() {
        return new SshStateFuzzerClientConfig(new SshSulClientConfig());
    }

    // SulServerConfigImpl (and MapperConfigImpl) need to be implemented
    @Override
    public StateFuzzerServerConfig buildServerConfig() {
        return new SshStateFuzzerServerConfig(new SshSulServerConfig());
    }

    @Override
    public StateFuzzer<MealyMachineWrapper<SshInput, SshOutput>> build(StateFuzzerEnabler stateFuzzerEnabler) {
        StateFuzzerComposerStandard<SshInput, SshOutput, ExecutionContext<SshInput, SshOutput, String>> stateFuzzerComposer = new StateFuzzerComposerStandard<>(
                stateFuzzerEnabler, alphabetBuilder,
                sulBuilder, sulWrapper).initialize();

        StateFuzzerStandard<SshInput, SshOutput> stateFuzzerStd = new StateFuzzerStandard<>(stateFuzzerComposer);
        return stateFuzzerStd;
    }

    @Override
    public TestRunner build(TestRunnerEnabler testRunnerEnabler) {
        return new TestRunnerStandard<SshInput, SshOutput, String, ExecutionContext<SshInput, SshOutput, String>>(
                testRunnerEnabler, alphabetBuilder,
                sulBuilder, sulWrapper).initialize();
    }

    @Override
    public TimingProbe build(TimingProbeEnabler timingProbeEnabler) {
        return new TimingProbeStandard<SshInput, SshOutput, String, ExecutionContext<SshInput, SshOutput, String>>(
                timingProbeEnabler, alphabetBuilder, sulBuilder, sulWrapper).initialize();
    }
}
