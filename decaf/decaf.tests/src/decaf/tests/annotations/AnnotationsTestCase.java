package decaf.tests.annotations;

import decaf.tests.*;

public class AnnotationsTestCase extends DecafTestCaseBase {
	
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
	protected void runResourceTestCase(String resourceName) throws Exception {
		super.runResourceTestCase("annotations/"  + resourceName);
	}
}
