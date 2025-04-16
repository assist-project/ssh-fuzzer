package learner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.github.protocolfuzzing.protocolstatefuzzer.components.learner.alphabet.AlphabetBuilder;
import com.github.protocolfuzzing.protocolstatefuzzer.components.learner.alphabet.AlphabetSerializerException;
import com.github.protocolfuzzing.protocolstatefuzzer.components.learner.config.LearnerConfig;

import net.automatalib.alphabet.Alphabet;
import net.automatalib.alphabet.ListAlphabet;

public class RAAlphabetBuilder implements AlphabetBuilder<RASshInput> {
    String input1 = "KEXINIT,kex_algo,STRING,server_host_key, java.lang.String,";
    List<RASshInput> allInputs = new ArrayList<RASshInput>();

    public RAAlphabetBuilder() {
        String[] split = input1.split(",");
        List<RASshParams> paramList = new ArrayList<>();
        RASshInput in = new RASshInput(split[0], paramList);
        in.addParam(split[1], split[2], split[3], split[4]);
        allInputs.add(in);
    }

    @Override
    public Alphabet<RASshInput> build(LearnerConfig learnerConfig) {
        return new ListAlphabet<>(allInputs);
    }

    @Override
    public InputStream getAlphabetFileInputStream(LearnerConfig learnerConfig) {
        String in = "";
        for (RASshInput input : allInputs) {
            in += input.toString();
        }

        return new ByteArrayInputStream(
                in.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String getAlphabetFileExtension() {
        return "xml";
    }

    @Override
    public void exportAlphabetToFile(String outputFileName, Alphabet<RASshInput> alphabet)
            throws IOException, AlphabetSerializerException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'exportAlphabetToFile'");
    }
}
