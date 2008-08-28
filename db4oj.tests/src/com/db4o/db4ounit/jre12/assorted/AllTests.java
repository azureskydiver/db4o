package com.db4o.db4ounit.jre12.assorted;
import db4ounit.extensions.*;

/**
 * @decaf.ignore.jdk11
 */
public class AllTests extends Db4oTestSuite {
	protected Class[] testCases() {
		return new Class[] {
				//FinalFieldTestCase.class,
				CascadeToHashMapTestCase.class,
				GenericArrayFieldTypeTestCase.class,
				GenericPrimitiveArrayTestCase.class,
				StoreComparableFieldTestCase.class,
				TranslatorStoredClassesTestCase.class,
				//Comment out this test temporarily
				//UpdatingDb4oVersionsTestCase.class,
		};
	}

}
