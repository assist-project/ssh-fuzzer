package learner;

public class MembershipOracle extends WordProcessor {
	private static final long serialVersionUID = 9079445565706621341L;
	
	public MembershipOracle(Sut sut, Logger sqllog) {
		super(sut, sqllog, false, new Counter("Membership Query Counter"));  // This oracle should not retrieve from cache, so set false.
	}
}
