package db4ounit;

import java.lang.reflect.*;

import com.db4o.foundation.*;

import db4ounit.fixtures.*;

public class ReflectionTestSuiteBuilder implements TestSuiteBuilder {
	
	private Class[] _classes;
	
	public ReflectionTestSuiteBuilder(Class clazz) {
		this(new Class[] { clazz });
	}
	
	public ReflectionTestSuiteBuilder(Class[] classes) {
		if (null == classes) throw new IllegalArgumentException("classes");
		_classes = classes;
	}
	
	public Iterator4 iterator() {
		return Iterators.flatten(
					Iterators.map(
						_classes,
						new Function4() {
							public Object apply(Object arg) {
								return fromClass((Class) arg);
							}
						})
					);
	}	
	
	/**
	 * Can be overriden in inherited classes to inject new fixtures into
	 * the context.
	 * 
	 * @param closure
	 * @return
	 */
	protected Object withContext(Closure4 closure) {
		return closure.run();
	}
	
	protected Iterator4 fromClass(final Class clazz) {
		return (Iterator4)withContext(new Closure4() {
			public Object run() {
				try {
					return new ContextfulIterator(suiteFor(clazz));
				} catch (Exception e) {
					return Iterators.singletonIterable(new FailingTest(clazz.getName(), e)).iterator();
				}
			}
		});
	}

	private Iterator4 suiteFor(Class clazz) {
		if(!isApplicable(clazz)) {
			TestPlatform.emitWarning("DISABLED: " + clazz.getName());
			return Iterators.EMPTY_ITERATOR;
		}
		if (TestSuiteBuilder.class.isAssignableFrom(clazz)) {
			return ((TestSuiteBuilder)newInstance(clazz)).iterator();
		}
		if (Test.class.isAssignableFrom(clazz)) {
			return Iterators.singletonIterator(newInstance(clazz));
		}
		validateTestClass(clazz);
		return fromMethods(clazz);
	}

	private void validateTestClass(Class clazz) {
		if (!(TestCase.class.isAssignableFrom(clazz))) {
			throw new IllegalArgumentException("" + clazz + " is not marked as " + TestCase.class);
		}
	}

	protected boolean isApplicable(Class clazz) {
		return clazz != null; // just removing the 'parameter not used' warning
	}
	
	private Iterator4 fromMethods(final Class clazz) {
		return Iterators.map(clazz.getMethods(), new Function4() {
			public Object apply(Object arg) {
				final Method method = (Method)arg;
				if (!isTestMethod(method)) {
					emitWarningOnIgnoredTestMethod(clazz, method);
					return Iterators.SKIP;			
				}
				return fromMethod(clazz, method);
			}
		});
	}
	
	private void emitWarningOnIgnoredTestMethod(Class clazz, Method method) {
		if (!startsWithIgnoreCase(method.getName(), "_test")) {
			return;
		}
		TestPlatform.emitWarning("IGNORED: " + createTest(newInstance(clazz), method).label());
	}

	protected boolean isTestMethod(Method method) {
		return hasTestPrefix(method)
			&& TestPlatform.isPublic(method)
			&& !TestPlatform.isStatic(method)
			&& !TestPlatform.hasParameters(method);
	}

	private boolean hasTestPrefix(Method method) {
		return startsWithIgnoreCase(method.getName(), "test");
	}

	protected boolean startsWithIgnoreCase(final String s, final String prefix) {
		return s.toUpperCase().startsWith(prefix.toUpperCase());
	}

	protected Object newInstance(Class clazz) {		
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			throw new TestException(e);
		}
	}
	
	protected Test createTest(Object instance, Method method) {
		return new TestMethod(instance, method);
	}
	
	protected final Test fromMethod(final Class clazz, final Method method) {
		return new ContextfulTest(new TestFactory() {
			public Test newInstance() {
				return createTest(ReflectionTestSuiteBuilder.this.newInstance(clazz), method);
			}
		});
	}
}
