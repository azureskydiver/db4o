package db4ounit.extensions;

import java.util.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;

/**
 * @sharpen.ignore
 */
public class Db4oUnitTestMain {
	public static void main(String[] args) throws Exception {
		List plainTestClasses=new ArrayList();
		List db4oTestClasses=new ArrayList();
		for(int idx=0;idx<args.length;idx++) {
			Class curClazz=Class.forName(args[idx]);
			if(Db4oTestCase.class.isAssignableFrom(curClazz)) {
				db4oTestClasses.add(curClazz);
				continue;
			}
			if(TestCase.class.isAssignableFrom(curClazz)) {
				plainTestClasses.add(curClazz);
			}
		}
		Class[] plainTestClassesArray = (Class[])plainTestClasses.toArray(new Class[plainTestClasses.size()]);
		Class[] db4oTestClassesArray = (Class[])db4oTestClasses.toArray(new Class[db4oTestClasses.size()]);
		TestSuiteBuilder plainBuilder=new ReflectionTestSuiteBuilder(plainTestClassesArray);
		Db4oTestSuiteBuilder db4oBuilder=new Db4oTestSuiteBuilder(new Db4oSolo(),db4oTestClassesArray);
		TestSuite suite=new TestSuite(new Test[]{plainBuilder.build(),db4oBuilder.build()});
		TestRunner runner=new TestRunner(suite);
		runner.run();
	}
}
