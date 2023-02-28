package learner;

public class Counter {
	private String name;

	private int value;

	public Counter(String name) {
		this.name = name;
		this.value = 0;
	}
	
	public int getValue() {
		return value;
	}
	
	/**
	 * Increments and returns the incremented value.
	 */
	public int increment() {
		value ++;
		return value;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Counter [name=" + name + ", value=" + value + "]";
	}
}
