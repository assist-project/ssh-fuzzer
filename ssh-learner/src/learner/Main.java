package learner;

import com.beust.jcommander.Parameter;
import com.github.protocolfuzzing.protocolstatefuzzer.components.learner.LearnerResult;
import com.github.protocolfuzzing.protocolstatefuzzer.components.learner.statistics.MealyMachineWrapper;
import com.github.protocolfuzzing.protocolstatefuzzer.components.learner.statistics.RegisterAutomatonWrapper;
import com.github.protocolfuzzing.protocolstatefuzzer.entrypoints.CommandLineParser;
import java.io.IOException;
import java.util.List;

public class Main {

    @Parameter(names = "-ra", description = "whether to run Mealy Machine learner or the RA learner?")
    static boolean isRaLearner = false;

    public static void main(String[] args) throws IOException {
        if (isRaLearner) {
            System.exit(0);
            // logic for RA learner
        } else {
            runMealyLearner(args);
        }

    }

    static void runMealyLearner(String[] args) {
        // multibuilder implements all necessary builders
        MultiBuilder mb = new MultiBuilder();

        CommandLineParser<MealyMachineWrapper<SshInput, SshOutput>> commandLineParser = new CommandLineParser<>(mb, mb,
                mb, mb);
        List<LearnerResult<MealyMachineWrapper<SshInput, SshOutput>>> results = commandLineParser.parse(args);
        System.out.println("Done with Mealy Machine learning");
    }
}
