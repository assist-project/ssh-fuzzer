package learner;

import com.github.protocolfuzzing.protocolstatefuzzer.components.learner.LearnerResult;
import com.github.protocolfuzzing.protocolstatefuzzer.entrypoints.CommandLineParser;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        // multibuilder implements all necessary builders
        MultiBuilder mb = new MultiBuilder();

        CommandLineParser commandLineParser = new CommandLineParser(mb, mb, mb, mb);
        List<LearnerResult> results = commandLineParser.parse(args);
    }
}
