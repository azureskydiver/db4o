package db4ounit.fixtures;

import com.db4o.foundation.*;

import db4ounit.*;

public final class FixtureDecoration implements TestDecoration {
	private final Test _test;
	private final ContextVariable _variable;
	private final Object _value;

	public FixtureDecoration(Test test, ContextVariable variable, Object value) {
		_test = test;
		_variable = variable;
		_value = value;
	}

	public void run() {
		runDecorated(new Runnable() {
			public void run() {
				_test.run();
			}
		});
	}
	
	public Test test() {
		return _test;
	}

	private void runDecorated(final Runnable block) {
		_variable.with(value(), block);
	}

	private Object value() {
		return _value instanceof Deferred4
			? ((Deferred4)_value).value()
			: _value;
	}

	public String getLabel() {
		final ObjectByRef label = new ObjectByRef(); 
		runDecorated(new Runnable() {
			public void run() {
				label.value = "(" + _value + ") " + _test.getLabel();
			}
		});
		return (String)label.value;
	}
}