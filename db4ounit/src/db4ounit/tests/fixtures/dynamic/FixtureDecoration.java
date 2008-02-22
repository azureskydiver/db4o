package db4ounit.tests.fixtures.dynamic;

import com.db4o.foundation.*;

import db4ounit.*;

public final class FixtureDecoration implements Test {
	private final Test _test;
	private final ContextVariable _variable;
	private final Object _value;

	FixtureDecoration(Test test, ContextVariable variable, Object value) {
		_test = test;
		_variable = variable;
		_value = value;
	}

	public void run(final TestResult result) {
		_variable.with(value(), new Runnable() {
			public void run() {
				_test.run(result);
			}
		});
	}

	private Object value() {
		return _value instanceof Deferred4
			? ((Deferred4)_value).value()
			: _value;
	}

	public String getLabel() {
		return "(" + _value + ") " + _test.getLabel();
	}
}