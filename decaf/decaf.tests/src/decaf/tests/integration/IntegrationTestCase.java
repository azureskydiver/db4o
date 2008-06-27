package decaf.tests.integration;

import decaf.tests.*;

public class IntegrationTestCase extends DecafTestCaseBase {
	
	public void testUnboxingInVarArgs() throws Exception {
		runResourceTestCase("UnboxingInVarArgs");
	}
	
	public void testUnboxingInForEach() throws Exception {
		runResourceTestCase("UnboxingInForEach");
	}

	public void testUnboxingForGenerics() throws Exception {
		runResourceTestCase("UnboxingForGenerics");
	}

	public void testErasureInVarArgs() throws Exception {
		runResourceTestCase("ErasureInVarArgs");
	}
	
	public void testErasureInForEach() throws Exception {
		runResourceTestCase("ErasureInForEach");
	}
	
	@Override
	protected void runResourceTestCase(String resourceName) throws Exception {
		super.runResourceTestCase("integration/"  + resourceName);
	}
}
