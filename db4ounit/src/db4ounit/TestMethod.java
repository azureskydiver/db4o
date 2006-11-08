package db4ounit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Reflection based db4ounit.Test implementation.
 */
public class TestMethod implements Test {
	
	public interface LabelProvider {
		String getLabel(TestMethod method);
	}
	
	public static LabelProvider DEFAULT_LABEL_PROVIDER = new LabelProvider() {
		public String getLabel(TestMethod method) {
			return method.getSubject().getClass().getName() + "." + method.getMethod().getName();
		}
	};
	
	private final Object _subject;
	private final Method _method;
	private final LabelProvider _labelProvider;
	
	public TestMethod(Object instance, Method method) {
		this(instance, method, DEFAULT_LABEL_PROVIDER);
	}

	public TestMethod(Object instance, Method method, LabelProvider labelProvider) {
		if (null == instance) throw new IllegalArgumentException("instance");
		if (null == method) throw new IllegalArgumentException("method");	
		if (null == labelProvider) throw new IllegalArgumentException("labelProvider");
		_subject = instance;
		_method = method;
		_labelProvider = labelProvider;
	}
	
	public Object getSubject() {
		return _subject;
	}
	
	public Method getMethod() {
		return _method;
	}

	public String getLabel() {
		return _labelProvider.getLabel(this);
	}

	public void run(TestResult result) {
		try {
			result.testStarted(this);
			setUp();
			invoke();
		} catch (InvocationTargetException e) {
			result.testFailed(this, e.getTargetException());
		} catch (Exception e) {
			result.testFailed(this, e);
		} finally {
			try {
				tearDown();
			} catch (TestException e) {
				result.testFailed(this, e);
			}
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
