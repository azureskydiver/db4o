package db4ounit;

import java.lang.reflect.*;

import com.db4o.foundation.*;

/**
 * @sharpen.ignore
 */
public class UnitTestMain {
	
	public static void main(String[] args) throws Exception {
		new UnitTestMain().runTests(args);
	}

	public final void runTests(String[] args) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		new ConsoleTestRunner(build(args), false).run();
	}
	
	protected TestSuiteBuilder builder(Class clazz) {
		return new ReflectionTestSuiteBuilder(clazz);
	}

	private Iterable4 build(String[] args)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		
		final Iterable4 tests = Iterators.map(Iterators.iterable(args), new Function4() {
			public Object apply(Object arg) {
				String testIdentifier = (String)arg;
				try {
					int methodSeparatorIndex = testIdentifier.indexOf('#');
					if (methodSeparatorIndex>0) {
						String className=testIdentifier.substring(0,methodSeparatorIndex);
						String methodName=testIdentifier.substring(methodSeparatorIndex+1);
						return testMethod(className, methodName);
					}
					
					return builder(Class.forName(testIdentifier));
					
				} catch (Exception x) {
					return new FailingTest(testIdentifier, x);
				}
			}
		});
		return Iterators.flatten(tests);
	}
	
	protected Test testMethod(String className, String methodName)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		Class clazz = Class.forName(className);
		return new TestMethod(clazz.newInstance(), findMethod(clazz, methodName));
	}

	private Method findMethod(final Class clazz, String methodName) {
		Method[] methods = clazz.getMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if (method.getName().equals(methodName)) {
				return method;
			}
		}
		throw new IllegalArgumentException("Method '" + methodName + "' not found in class '" + clazz + "'.");
	}
}
