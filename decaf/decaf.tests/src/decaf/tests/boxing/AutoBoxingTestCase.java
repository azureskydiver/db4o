package decaf.tests.boxing;

import decaf.tests.*;

public class AutoBoxingTestCase extends DecafTestCaseBase {
	
	public void testAutoUnboxingInIf() throws Exception {
		runResourceTestCase("AutoUnboxingInIf");
	}
	
	public void testAutoUnboxing() throws Exception {
		runResourceTestCase("AutoUnboxing");
	}
	
	@Override
	protected void runResourceTestCase(String resourceName) throws Exception {
		super.runResourceTestCase("boxing/" + resourceName);
	}

}
