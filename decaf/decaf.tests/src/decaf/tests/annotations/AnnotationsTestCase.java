package decaf.tests.annotations;

import decaf.tests.*;

public class AnnotationsTestCase extends DecafTestCaseBase {
	
	public void testRemoveAt() throws Exception {
		runResourceTestCase("RemoveAt");
	}
	
	public void testInsertFirst() throws Exception {
		runResourceTestCase("InsertFirst");
	}

	public void testMixin() throws Exception {
		runResourceTestCase("Mixin");
	}
	
	public void testJdk5AnnotationsAreAutomaticallyIgnored() throws Exception {
		runResourceTestCase("Jdk5AnnotationsAreAutomaticallyIgnored");
	}
	
	public void testIgnoreImplements() throws Exception {
		runResourceTestCase("IgnoreImplements");
	}
	
	public void testIgnoreExtends() throws Exception {
		runResourceTestCase("IgnoreExtends");
	}
	
	public void testIgnoreMainType() throws Exception {
		runResourceTestCase("IgnoreMainType");
	}

	public void testIgnoreClass() throws Exception {
		runResourceTestCase("IgnoreClass");
	}

	public void testIgnoreMethod() throws Exception {
		runResourceTestCase("IgnoreMethod");
	}
	
	@Override
	protected String packagePath() {
		return "annotations";
	}
}
