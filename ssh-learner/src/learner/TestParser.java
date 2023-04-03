package learner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.automatalib.words.Word;

/**
 * Reads tests from a file, checking that their inputs are included in the alphabet.
 * 
 */
public class TestParser {

	public TestParser() {
	}

	/**
	 * Reads reset-separated tests (test queries to be precise).
	 * It stops reading once it reaches the end, or an empty line. 
	 * A non-empty line may contain:
	 * <ul>
	 * <li>reset - marking the end of the current test, and the beginning of a new
	 * test</li>
	 * <li>space-separated regular inputs and resets</li>
	 * <li>commented line (starts with # or !)</li>
	 * </ul>
	 */
	public List<Word<String>> readTests(Collection<String> alphabet, Reader reader) throws IOException {
		List<String> inputStrings = readTestStrings(reader);
		List<String> flattenedInputStrings = inputStrings.stream()
				.map(i -> i.split("\\s+")).flatMap(a -> Arrays.stream(a))
				.collect(Collectors.toList());

		List<Word<String>> tests = new LinkedList<>();
		LinkedList<String> currentTestStrings = new LinkedList<>();
		for (String inputString : flattenedInputStrings) {
			if (inputString.equals("reset")) {
				tests.add(readTest(alphabet, currentTestStrings));
				currentTestStrings.clear();
			} else {
				currentTestStrings.add(inputString);
			}
		}
		if (!inputStrings.isEmpty()) {
			tests.add(readTest(alphabet, currentTestStrings));
		}
		return tests;
	}
	
	private List<String> readTestStrings(Reader reader) throws IOException {
		List<String> tests = new ArrayList<>();
		try (BufferedReader bufferReader = new BufferedReader(reader)) {
			String line;
			while ((line = bufferReader.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty()) {
					break;
				}
				if (!line.startsWith("#") && !line.startsWith("!")) {
					tests.add(line);
				}
			}
		}
		return tests;
	}
	
	private Word<String> readTest(Collection<String> alphabet, List<String> testInputStrings) {
		Map<String, String> inputs = new LinkedHashMap<>();
		alphabet.stream().forEach(i -> inputs.put(i.toString(), i));
		Word<String> inputWord = Word.epsilon();
		for (String inputString : testInputStrings) {
			inputString = inputString.trim();
			if (!inputs.containsKey(inputString)) {
				throw new RuntimeException("Input \"" + inputString + "\" is missing from the supplied alphabet, which is: " + alphabet);
			}
			inputWord = inputWord.append(inputs.get(inputString));
		}

		return inputWord;
	}
}
