package com.db4o.db4ounit.jre12.assorted;
import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {
	protected Class[] testCases() {
		return new Class[] {
				//FinalFieldTestCase.class,
				GenericArrayFieldTypeTestCase.class,
				TranslatorStoredClassesTestCase.class,
				UpdatingDb4oVersionsTestCase.class,
		};
	}

}
