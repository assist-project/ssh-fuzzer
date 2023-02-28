package learner;

import de.ls5.jlearn.abstractclasses.LearningException;
import de.ls5.jlearn.interfaces.Oracle;
import de.ls5.jlearn.interfaces.Symbol;
import de.ls5.jlearn.interfaces.Word;
import de.ls5.jlearn.shared.SymbolImpl;
import de.ls5.jlearn.shared.WordImpl;

public class WordProcessor implements Oracle {
	private static final long serialVersionUID = -4173623825087666011L;
	private Sut sut;
	private Logger sqllog;
	private boolean retrieveFromCache;
	private Counter queryCounter;
	
	protected WordProcessor(Sut sut, Logger sqllog, boolean retrieveFromCache, Counter queryCounter) {
		// Create a new sut wrapper and give access to the socket
		this.sut = sut;
		
		// Set the logger
		this.sqllog = sqllog;
		
		this.retrieveFromCache = true;
		
		this.queryCounter = queryCounter;
	}
	
	public Word processQuery(Word query) throws LearningException {
		// Perform a learnlib query
		String input, output;
		Word result = new WordImpl();
		
		// First run against DB for extra performance.
		String fullQuery = "";
		for (Symbol currentSymbol : query.getSymbolList()) {
			fullQuery += currentSymbol.toString() + " ";
		}
		
		// Some textual logging
		System.out.println("QUERY #" + queryCounter.increment() + " (" + fullQuery + ")");
		
		// Did fullquery already produce a result in another run?
		String cacheOutput = this.sqllog.query(fullQuery);
		
		// Some more textual logging
		System.out.println("... cache-result: " + cacheOutput);
		
		//System.out.println("... retrieveFromCache: " + this.retrieveFromCache);
		// FIXME The if should also check for this.retrieveFromCache...
		
		if (cacheOutput == "notfound" || !retrieveFromCache) {
			// Query not found in cache, retrieve result from SUT...
			
			System.out.println("... querying SUT");
			
			// Reset the SUT to default state
			System.out.println("... sending reset");
			sut.reset();
			
			String inputString = "";
			String resultString = "";
			boolean safeToCache = true;
			long startTime = System.currentTimeMillis();
			for (Symbol currentSymbol : query.getSymbolList()) {
				// INTERESTING
				// Tell something about the CHAOS state which we didnt implement in the end but might have been needed 
				// For Tectia 
				input = currentSymbol.toString();
				output = sut.sendInput(input);
				long duration = System.currentTimeMillis() - startTime;
				
				System.out.println("... sending \"" + input + "\", got \"" + output + "\" in " + duration + "ms");
				
				result.addSymbol(new SymbolImpl(output));
				
				// Log the results
				inputString += input + " ";
				resultString += output + " ";
				
				if(output == null || output.toString().equals("null")) {
					safeToCache = false;
				}
				
				// Check for duplicate with another result
				String partCache = this.sqllog.query(inputString);
				
				if (partCache.equals("notfound") && safeToCache) {
					// Part was not found in cache, add for superb nondeterminism detection
					this.sqllog.log(inputString, resultString, duration);
					System.out.println("... adding \"" + inputString + "\":\"" + resultString + "\" to cache");
				}
				else {
					// Found in cache, is it nondeterminism?
					System.out.println("... part \"" + inputString + "\" has already been cached");
					if (!resultString.equals(partCache)) {
						System.out.println("... NONDETERMINISM FOUND in cache (\"" + inputString + "\" exists with result other than \"" + resultString + "\")");
						System.out.println("... 	query: \"" + inputString + "\"");
						System.out.println("... 	cached result: \"" + partCache + "\"");
						System.out.println("... 	new result: \"" + resultString + "\"");
						throw new NonDeterminismException(inputString, partCache, resultString);
					}
				}
			}
			
		}
		else {
			// This result already exists from a previous run, use that one
			System.out.println("... using cache");
 			for (String cacheWord : cacheOutput.split("\\s+")) {
 				result.addSymbol(new SymbolImpl(cacheWord));
 			}
		}
		
		//System.out.println("... output vocabulary size is " + this.sqllog.outputVocabulary());		
		System.out.println("... return final result learnlib: " + result);
		return result;
	}
	
	public Counter getQueryCounter() {
		return queryCounter;
	}
}
