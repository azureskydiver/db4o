package db4ounit;

import java.lang.reflect.*;
import java.util.*;

/**
 * @sharpen.ignore
 */
public class UnitTestMain {
	public static void main(String[] args) throws Exception {
		new UnitTestMain().runTests(args);
	}

	public final void runTests(String[] args) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		TestSuite suite = build(args);
		TestRunner runner=new TestRunner(suite);
		runner.run();
	}
	
	protected TestSuiteBuilder builder(Class[] clazzes) {
		return new ReflectionTestSuiteBuilder(clazzes);
	}

	private TestSuite build(String[] args)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		Vector plainTestMethods=new Vector();
		Vector plainTestClasses=new Vector();
		for(int idx=0;idx<args.length;idx++) {
			String testIdentifier = args[idx];
			int methodSeparatorIndex = testIdentifier.indexOf('#');
			if(methodSeparatorIndex>0) {
				String className=testIdentifier.substring(0,methodSeparatorIndex);
				String methodName=testIdentifier.substring(methodSeparatorIndex+1);
				TestMethod testMethod = testMethod(className, methodName);
				plainTestMethods.addElement(testMethod);
				continue;
			}
			Class curClazz=Class.forName(testIdentifier);
			if(TestCase.class.isAssignableFrom(curClazz)) {
				plainTestClasses.addElement(curClazz);
			}
		}
		Class[] plainTestClassesArray = new Class[plainTestClasses.size()];
		vectorToArray(plainTestClasses, plainTestClassesArray);
		TestSuiteBuilder classBuilder=builder(plainTestClassesArray);
		Test[] plainTestMethodArray=new Test[plainTestMethods.size()];		vectorToArray(plainTestMethods, plainTestMethodArray);		TestSuite methodSuite=new TestSuite(plainTestMethodArray);
		return new TestSuite(new Test[]{classBuilder.build(),methodSuite});
	}
	
	private void vectorToArray(Vector vector, Object[] array){
		int i = 0;		Enumeration enumer = vector.elements();
		while(enumer.hasMoreElements()){
			array[i++] = enumer.nextElement();
		}
	}
	private TestMethod testMethod(String className, String methodName)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		TestCase test = createTestInstance(className);
		Method method=null;
		Method[] methods = test.getClass().getMethods();
		for (int methodIdx = 0; methodIdx < methods.length; methodIdx++) {
			Method curMethod = methods[methodIdx];
			if(curMethod.getName().equals(methodName)) {
				method=curMethod;
			}
		}
		if(method==null) {
			return null;
		}
		return new TestMethod(test,method);
	}

	protected TestCase createTestInstance(String className) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		Class clazz=Class.forName(className);
		TestCase test=(TestCase)clazz.newInstance();
		return test;
	}
}
