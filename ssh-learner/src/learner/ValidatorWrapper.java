package learner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.function.BiFunction;

import de.learnlib.api.oracle.MembershipOracle.MealyMembershipOracle;
import de.learnlib.api.query.DefaultQuery;
import de.learnlib.api.query.Query;
import net.automatalib.automata.AutomatonCreator;
import net.automatalib.automata.transducers.MealyMachine;
import net.automatalib.automata.transducers.MutableMealyMachine;
import net.automatalib.automata.transducers.impl.compact.CompactMealy;
import net.automatalib.commons.util.Pair;
import net.automatalib.serialization.InputModelData;
import net.automatalib.serialization.InputModelDeserializer;
import net.automatalib.serialization.dot.DOTParsers;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;

public class ValidatorWrapper implements MealyMembershipOracle<String, String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private MealyMachine<?, String, ?, String> refMealy;
	private Alphabet<String> refAlphabet;
	private MealyMembershipOracle<String, String> oracle;

	public ValidatorWrapper(MealyMembershipOracle<String, String> oracle, String ref) {
		this.oracle = oracle;
		try {
			InputModelData<String, CompactMealy<String, String>> dataModel = parse(new CompactMealy.Creator<String, String>(), new FileInputStream(ref), (i,o) -> Pair.of(i.trim(), o.trim()));
			refMealy = dataModel.model;
			refAlphabet = dataModel.alphabet;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}

	@Override
	public void processQueries(Collection<? extends Query<String, Word<String>>> queries) {
		oracle.processQueries(queries);
		for (Query<String, Word<String>> query : queries) {
			DefaultQuery<String, Word<String>> dQuery = (DefaultQuery<String, Word<String>>) query;
			Word<String> refOutput = refMealy.computeOutput(query.getInput());
			if (!dQuery.getOutput().equals(refOutput)) {
				throw new RuntimeException(String.format("Divergence on input: %s" + System.lineSeparator() + "Ref. Output: %s"  + System.lineSeparator() + "Actual Output: %s", query.getInput(), refOutput, dQuery.getOutput()));
			}
		}
		
	}
	
	public static <I,O, A extends MutableMealyMachine<?, I, ?, O>> InputModelData<I, A> parse(AutomatonCreator<A, I> creator, InputStream inputStream, BiFunction<String, String, Pair<I,O>> processor) throws IOException {
		InputModelDeserializer<I, A> parser = DOTParsers.mealy(creator, (map)
				-> {
					Pair<String,String> ioStringPair = DOTParsers.DEFAULT_MEALY_EDGE_PARSER.apply(map);
					Pair<I,O> ioPair = processor.apply(ioStringPair.getFirst(), ioStringPair.getSecond());
					return ioPair;
				});
		return parser.readModel(inputStream);
	}
	
}
