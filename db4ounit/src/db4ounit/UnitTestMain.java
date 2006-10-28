package db4ounit;

import java.util.*;

/**
 * @sharpen.ignore
 */
public class UnitTestMain {
	public static void main(String[] args) throws Exception {
		List plainTestClasses=new ArrayList();
		for(int idx=0;idx<args.length;idx++) {
			Class curClazz=Class.forName(args[idx]);
			if(TestCase.class.isAssignableFrom(curClazz)) {
				plainTestClasses.add(curClazz);
			}
		}
		Class[] plainTestClassesArray = (Class[])plainTestClasses.toArray(new Class[plainTestClasses.size()]);
		TestSuiteBuilder plainBuilder=new ReflectionTestSuiteBuilder(plainTestClassesArray);
		TestRunner runner=new TestRunner(plainBuilder);
		runner.run();
	}
}
