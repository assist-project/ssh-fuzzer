package learner;

import java.io.IOException;
import java.util.List;

import com.github.protocolfuzzing.protocolstatefuzzer.components.learner.LearnerResult;
import com.github.protocolfuzzing.protocolstatefuzzer.components.learner.statistics.RegisterAutomatonWrapper;
import com.github.protocolfuzzing.protocolstatefuzzer.entrypoints.CommandLineParser;

import de.learnlib.ralib.words.PSymbolInstance;

public class RALearnerMain {
    public static void main(String[] args) throws IOException {
        RAMultiBuilder mb = new RAMultiBuilder();

        CommandLineParser<RegisterAutomatonWrapper<RASshInput, PSymbolInstance>> commandLineParser = new CommandLineParser<>(
                mb, mb,
                mb, mb);
        List<LearnerResult<RegisterAutomatonWrapper<RASshInput, PSymbolInstance>>> results = commandLineParser
                .parse(args);

        System.out.println("✅ Done with RA learning");
    }
}
