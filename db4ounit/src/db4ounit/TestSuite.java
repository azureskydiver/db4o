package db4ounit;

import com.db4o.foundation.*;

/**
 * A group of tests.
 * 
 * A TestSuite can be ran only once because it disposes of test instances
 * as it runs them.
 *
 */
public class TestSuite implements Test {
	private static final class TestArrayFactory implements ArrayFactory {
		public Object[] newArray(int size) {
			return new Test[size];
		}
	}

	private Test[] _tests;
	private String _label;
	
	public TestSuite(String label, Test[] tests) {
		this._label = label;
		this._tests = tests;
	}
	
	public TestSuite(Iterator4 tests) {
		this((Test[]) Iterators.toArray(tests, new TestArrayFactory()));
	}
	
	public TestSuite(Test[] tests) {
		this(null, tests);
	}
	
	public TestSuite(Test singleTest) {
		this(null, new Test[] { singleTest });
	}
	
	public String getLabel() {
		return _label == null ? labelFromTests(_tests) : _label;
	}
	
	public Test[] getTests() {
		return _tests;
	}

	public void run(TestResult result) {
		try {
			final Test[] tests = getTests();
			for (int i = 0; i < tests.length; i++) {
				tests[i].run(result);
				tests[i] = null;
			}
		} finally {
			_tests = null;
		}
	}
	
	private static String labelFromTests(Test[] tests) {
		if (tests.length == 0) return "[]";
		
		String firstLabel = tests[0].getLabel();
		if (tests.length == 1) return "[" + firstLabel + "]";
		
		return "[" + firstLabel + ", ...]";
	}
}
