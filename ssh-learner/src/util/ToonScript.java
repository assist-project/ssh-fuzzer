package util;

import java.util.ArrayList;
import java.util.List;

import de.ls5.jlearn.interfaces.Automaton;
import de.ls5.jlearn.interfaces.State;
import de.ls5.jlearn.interfaces.Symbol;
import de.ls5.jlearn.shared.SymbolImpl;

public class ToonScript {
	
	//private static List<Symbol> rekeySingle = Arrays.asList(new SymbolImpl("REKEY"));
	//private static List<Symbol> rekeyComposite = Arrays.asList(new SymbolImpl("KEXINIT"), new SymbolImpl("KEX30"), new SymbolImpl("NEWKEYS"));
	
	private static List<Symbol> rekeySingle = new ArrayList<Symbol>();
	private static List<Symbol> rekeyComposite = new ArrayList<Symbol>();
	
	public static State getEndState(Automaton automaton, State startingState, List<Symbol> inputSeq){
		State currentState = startingState;
		for (Symbol input : inputSeq) {
			currentState = currentState.getTransitionState(input);
		}
		return currentState;
	}
	
	/**
	 * Gives a minimal length path from the start state to the given state. We build this path based 
	 * on the random value generator. Note, there can be several minimal paths to the given state. We only 
	 * return one. The length is the first order. Traces of equal length are order by the position of the last input
	 * in the alphabet.
	 */
	public static List<Symbol> traceToState(Automaton automaton, State state) {
		List<Symbol> alphabet = automaton.getAlphabet().getSymbolList();
		List<Symbol> selectedMiddlePart = new ArrayList<Symbol>();
		List<List<Symbol>> middleParts = new ArrayList<List<Symbol>>();
		middleParts.add(selectedMiddlePart);
		List<State> reachedStates = new ArrayList<State>();
		while(true) {
			List<Symbol> traceToState = new ArrayList<Symbol>();
			traceToState.addAll(selectedMiddlePart);
			State reachedState = getEndState(automaton, automaton.getStart(), traceToState);
			if (reachedState.getId() == state.getId()) {
				break;
			}
			reachedStates.add(reachedState);
			middleParts.remove(0);
			for (Symbol input : alphabet) {
				// we only add middle parts which are relevant (those that lead to new states)
				if (!reachedStates.contains(reachedState.getTransitionState(input))) {
					List<Symbol> newMiddlePart = new ArrayList<Symbol>(selectedMiddlePart);
					newMiddlePart.add(input);
					middleParts.add(newMiddlePart);
				}
			}
			
			selectedMiddlePart = middleParts.get(0);
		}
		
		return selectedMiddlePart;
	}
	
	public static Automaton loadAutomaton(String location) {
		Automaton loadedHyp = Dot.readDotFile(location);
		return loadedHyp;
	}
	
	
	public static void main(String args[]) {
		rekeySingle.add(new SymbolImpl("REKEY"));
		rekeyComposite.add(new SymbolImpl("KEXINIT"));
		rekeyComposite.add(new SymbolImpl("KEX30"));
		rekeyComposite.add(new SymbolImpl("NEWKEYS"));
		
		Automaton hyp = loadAutomaton("hyp.dot");
		for (State state : hyp.getAllStates()) {
			State rekeySingleEndState = getEndState(hyp, state, rekeySingle);
			State rekeyCompositeEndState = getEndState(hyp, state, rekeyComposite);
			if (rekeySingleEndState.getId() != rekeyCompositeEndState.getId()) {
				System.out.println("States are different " + rekeySingleEndState + " "+ rekeyCompositeEndState);
				List<Symbol> potCe = new ArrayList<>();
				List<Symbol> accessSequence = traceToState(hyp, state);
				potCe.addAll(accessSequence);
				potCe.addAll(rekeyComposite);
				System.out.println("Potential counterexample: " + potCe);
				
			}
		}
		System.out.println("Done~!");
	}

}
