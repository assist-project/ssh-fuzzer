package learner;

import com.github.protocolfuzzing.protocolstatefuzzer.entrypoints.CommandLineParser;

public class Main {
    public static void main(String[] args) {
        // multibuilder implements all necessary builders
        MultiBuilder mb = new MultiBuilder();

        // single parentLogger, since Main resides in the outmost package
        String[] lgrs = {"learner"};

        CommandLineParser commandLineParser = new CommandLineParser(mb, mb, mb, mb, lgrs);
        commandLineParser.parse(args);
    }
}
