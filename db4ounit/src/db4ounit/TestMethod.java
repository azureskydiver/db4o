package db4ounit;

import java.lang.reflect.*;

/**
 * Reflection based db4ounit.Test implementation.
 */
public class TestMethod implements Test {
	
	private final Object _subject;
	private final Method _method;
	
	public TestMethod(Object instance, Method method) {
		if (null == instance) throw new IllegalArgumentException("instance");
		if (null == method) throw new IllegalArgumentException("method");	
		_subject = instance;
		_method = method;
	}
	
	public Object getSubject() {
		return _subject;
	}
	
	public Method getMethod() {
		return _method;
	}

	public String label() {
		return _subject.getClass().getName() + "." + _method.getName();
	}
	
	public String toString() {
		return "TestMethod(" + _method + ")";
	}

	public void run() {
		try {
			setUp();
			try {
				invoke();
			} catch (InvocationTargetException x) {
				throw new TestException(x.getTargetException());
			} catch (Exception x) {
				throw new TestException(x);
			}
		} finally {
			tearDown();
		}
	}

	protected void invoke() throws Exception {
		_method.invoke(_subject, new Object[0]);
	}

	protected void tearDown() {
		if (_subject instanceof TestLifeCycle) {
			try {
				((TestLifeCycle)_subject).tearDown();
			} catch (Exception e) {
				throw new TearDownFailureException(e);
			}
		}
	}

	protected void setUp() {
		if (_subject instanceof TestLifeCycle) {
			try {
				((TestLifeCycle)_subject).setUp();
			} catch (Exception e) {
				throw new SetupFailureException(e);
			}
		}
	}
}
