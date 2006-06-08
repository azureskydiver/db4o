package db4ounit;

public class TestSuite implements Test {
	private Test[] _tests;
	private String _label;
	
	public TestSuite(String label, Test[] tests) {
		this._label = label;
		this._tests = tests;
	}
	
	public TestSuite(Test[] tests) {
		this(null, tests);
	}
	
	public String getLabel() {
		return _label == null ? labelFromTests(_tests) : _label;
	}
	
	public Test[] getTests() {
		return _tests;
	}

	public void run(TestResult result) {
		Test[] tests = getTests();
		for (int i = 0; i < tests.length; i++) {
			tests[i].run(result);
		}
	}
	
	private static String labelFromTests(Test[] tests) {
		if (tests.length == 0) return "[]";
		
		String firstLabel = tests[0].getLabel();
		if (tests.length == 1) return "[" + firstLabel + "]";
		
		return "[" + firstLabel + ", ...]";
	}
}
