package decaf.tests.boxing;

import decaf.tests.*;

public class AutoBoxingTestCase extends DecafTestCaseBase {
	
	public void testBooleanAutoUnboxing() throws Exception {
		runResourceTestCase("BooleanAutoUnboxing");
	}
	
	public void testIntAutoUnboxing() throws Exception {
		runResourceTestCase("IntAutoUnboxing");
	}
	
	@Override
	protected void runResourceTestCase(String resourceName) throws Exception {
		super.runResourceTestCase("boxing/" + resourceName);
	}

}
