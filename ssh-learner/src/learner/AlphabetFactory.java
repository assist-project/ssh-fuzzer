package learner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import net.automatalib.words.Alphabet;
import net.automatalib.words.impl.ListAlphabet;

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
                                        ,"KEX31"
					,"NEWKEYS"
					,"SR_AUTH"
                                        ,"SR_ACCEPT"
					,"SR_CONN"
					,"UA_PK_NOK"
					,"UA_PK_OK"
					,"UA_PW_NOK"
					,"UA_PW_OK"
					,"UA_NONE"
                                        ,"UA_SUCCESS"
										,"UA_FAILURE"
					,"CH_OPEN"
					,"CH_CLOSE"
					,"CH_EOF"
					,"CH_DATA"
					,"CH_EXTENDED_DATA"
					,"CH_WINDOW_ADJUST"
					,"CH_REQUEST_PTY" } );
	
	public static Alphabet<String> generateInputAlphabet(List<String> suppliedInputStrings) {
		Optional<String> hasInvInput = suppliedInputStrings.stream().filter(str -> !fullAlphabet.contains(str)).findAny();
		if (hasInvInput.isPresent()) {
			System.out.println("Input " + hasInvInput.get() + " not in full alphabet");
			System.exit(0);
		}
		ListAlphabet<String> result = new ListAlphabet<String>(suppliedInputStrings);
		return result;
	}
}
