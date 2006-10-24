package db4ounit;

import java.lang.reflect.Method;
import java.util.Vector;

public class ReflectionTestSuiteBuilder implements TestSuiteBuilder {
	
	private Class[] _classes;
	
	public ReflectionTestSuiteBuilder(Class clazz) {
		if (null == clazz) throw new IllegalArgumentException("clazz");
		_classes = new Class[] { clazz };
	}
	
	public ReflectionTestSuiteBuilder(Class[] classes) {
		if (null == classes) throw new IllegalArgumentException("classes");
		_classes = classes;
	}
	
	public TestSuite build() {
		return (1 == _classes.length)
			? fromClass(_classes[0])
			: fromClasses(_classes);
	}
	
	protected TestSuite fromClasses(Class[] classes) {		
		Vector suites = new Vector(classes.length);
		for (int i = 0; i < classes.length; i++) {
			TestSuite suite = fromClass(classes[i]);
			if (suite.getTests().length>0) {
				suites.add(suite);
			}
		}
		return new TestSuite(toTestArray(suites));
	}
	
	protected TestSuite fromClass(Class clazz) {
		if(!isApplicable(clazz)) {
			TestPlatform.emitWarning("IGNORED: " + clazz.getName());
			return new TestSuite(new Test[0]);
		}
		if(TestSuiteBuilder.class.isAssignableFrom(clazz)) {
			return ((TestSuiteBuilder)newInstance(clazz)).build();
		}
		if (Test.class.isAssignableFrom(clazz)) {
			return new TestSuite(clazz.getName(), new Test[] { (Test)newInstance(clazz) });
		}
		if (!(TestCase.class.isAssignableFrom(clazz))) {
			throw new IllegalArgumentException("" + clazz + " is not marked as " + TestCase.class);
		}
		return fromMethods(clazz);
	}

	protected boolean isApplicable(Class clazz) {
		return clazz != null; // just removing the 'parameter not used' warning
	}
	
	private TestSuite fromMethods(Class clazz) {
		Vector tests = new Vector();
		Method[] methods = clazz.getMethods();
		for (int i = 0; i < methods.length; i++) {
			Object instance=newInstance(clazz);
			Method method = methods[i];
			if (!isTestMethod(method)) {
				emitWarningOnIgnoredTestMethod(instance, method);
				continue;			
			}
			tests.addElement(createTest(instance, method));
		}		
		return new TestSuite(clazz.getName(), toTestArray(tests));
	}
	
	private void emitWarningOnIgnoredTestMethod(Object subject, Method method) {
		if (!startsWithIgnoreCase(method.getName(), "_test")) {
			return;
		}
		TestPlatform.emitWarning("IGNORED: " + TestMethod.createLabel(subject, method));
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
	
	private static Test[] toTestArray(Vector tests) {
		Test[] array = new Test[tests.size()];
		tests.copyInto(array);
		return array;
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
}
