package db4ounit;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.db4o.foundation.Collection4;

public class ReflectionTestSuiteBuilder {
	
	public TestSuite fromClasses(Class[] classes) {		
		TestSuite[] suites = new TestSuite[classes.length];
		for (int i = 0; i < classes.length; i++) {
			suites[i] = fromClass(classes[i]);
		}
		return new TestSuite(null, suites);
	}
	
	public TestSuite fromClass(Class clazz) {
		
		Object instance = newInstance(clazz);
		if (instance instanceof TestSuiteBuilder) {
			return ((TestSuiteBuilder)instance).build();
		}
		if (instance instanceof Test) {
			return new TestSuite(clazz.getName(), new Test[] { (Test)instance });
		}
		
		Collection4 tests = new Collection4();
		Method[] methods = clazz.getMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if (!isTestMethod(method)) continue;			
			tests.add(createTest(instance, method));
		}		
		return new TestSuite(clazz.getName(), (Test[])tests.toArray(new Test[tests.size()]));
	}

	protected Object newInstance(Class clazz) {		
		try {
			return clazz.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected boolean isTestMethod(Method method) {
		return method.getName().startsWith("test")			
			&& Modifier.isPublic(method.getModifiers())
			&& !Modifier.isStatic(method.getModifiers())
			&& method.getParameterTypes().length == 0;
	}
	
	protected Test createTest(Object instance, Method method) {
		return new TestMethod(instance, method);
	}
}
