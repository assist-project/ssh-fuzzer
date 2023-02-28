package learner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import de.ls5.jlearn.interfaces.Alphabet;
import de.ls5.jlearn.shared.AlphabetImpl;
import de.ls5.jlearn.shared.SymbolImpl;

public class AlphabetFactory {
	public static List<String> fullAlphabet = Arrays.asList(
			new String [] { 
					"DISCONNECT"
					,"IGNORE"
					,"UNIMPL"
					,"DEBUG"
					,"KEXINIT"
					,"KEXINIT_PROCEED"
					,"KEX30"
					,"NEWKEYS"
					,"SR_AUTH"
					,"SR_CONN"
					,"UA_PK_NOK"
					,"UA_PK_OK"
					,"UA_PW_NOK"
					,"UA_PW_OK"
					,"UA_NONE"
					,"CH_OPEN"
					,"CH_CLOSE"
					,"CH_EOF"
					,"CH_DATA"
					,"CH_EXTENDED_DATA"
					,"CH_WINDOW_ADJUST"
					,"CH_REQUEST_PTY" } );
	
	public static Alphabet generateInputAlphabet(List<String> suppliedInputStrings) {
		Optional<String> hasInvInput = suppliedInputStrings.stream().filter(str -> !fullAlphabet.contains(str)).findAny();
		if (hasInvInput.isPresent()) {
			System.out.println("Input " + hasInvInput.get() + " not in full alphabet");
			System.exit(0);
		}
		Alphabet result = new AlphabetImpl();
		suppliedInputStrings.forEach(action -> 
		result.addSymbol(new SymbolImpl(action)));
		return result;
	}
}
