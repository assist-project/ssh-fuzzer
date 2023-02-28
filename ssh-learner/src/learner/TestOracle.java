package learner;

public class TestOracle extends WordProcessor {
	
	public TestOracle(Sut sut, Logger sqllog) {
		super(sut, sqllog, true, new Counter("Test Query Counter"));  // This test oracle should retrieve from cache, so set true.
	}
}
