package learner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.learnlib.ralib.words.InputSymbol;
import de.learnlib.ralib.words.OutputSymbol;
import de.learnlib.ralib.words.ParameterizedSymbol;
import net.automatalib.alphabet.impl.ListAlphabet;

public class RASshAlphabet extends ListAlphabet<ParameterizedSymbol> {

    private final Map<String, ParameterizedSymbol> symbolMap;

    private RASshAlphabet(Map<String, ParameterizedSymbol> symbols) {
        super(new ArrayList<ParameterizedSymbol>(symbols.values()));
        System.out.println("Alphabet symbols: "+ symbols.toString());
        this.symbolMap = symbols;
    }

    public ParameterizedSymbol getPSymbol(String enum_member) {

        ParameterizedSymbol symbol = symbolMap.get(enum_member);
        if (symbol == null) {
            throw new RuntimeException("The symbol " + enum_member
                    + " is not present in the alphabet map, the map may have not been initialised properly.");
        }
        return symbol;
    }

    public static class Builder {

        /** Stores the symbols as they are being built, allows overwriting */
        private HashMap<String, ParameterizedSymbol> symbolMap = new HashMap<>();

        public Builder() {
        }

        public Builder withInput(ParameterizedSymbol input) {
            InputSymbol inputS = new InputSymbol(input.getName(), input.getPtypes());
            symbolMap.put(input.getName(), inputS);
            return this;
        }

        public Builder withOutput(ParameterizedSymbol input) {
            String name = "O_" + input.getName();
            OutputSymbol output = new OutputSymbol(name, input.getPtypes());
            symbolMap.put(name, output);
            return this;
        }

        public Builder withInputs(ParameterizedSymbol[] inputs) {
            for (ParameterizedSymbol i : inputs) {
                InputSymbol inputS = new InputSymbol(i.getName(), i.getPtypes());
                symbolMap.put(i.getName(), inputS);
            }
            return this;
        }

        public Builder withOutputs(ParameterizedSymbol[] inputs) {
            for (ParameterizedSymbol i : inputs) {
                OutputSymbol outputS = new OutputSymbol(i.getName(), i.getPtypes());
                symbolMap.put(i.getName(), outputS);
            }
            return this;
        }

        public RASshAlphabet build() {
            return new RASshAlphabet(symbolMap);
        }

    }
}
