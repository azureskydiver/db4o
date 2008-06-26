package decaf.tests.integration;

import decaf.tests.*;

public class IntegrationTestCase extends DecafTestCaseBase {
	
	public void _testUnboxingInVarArgs() throws Exception {
		runResourceTestCase("UnboxingInVarArgs");
	}
	
	public void _testUnboxingInForEach() throws Exception {
		runResourceTestCase("UnboxingInForEach");
	}

	public void testUnboxingForGenerics() throws Exception {
		runResourceTestCase("UnboxingForGenerics");
	}

	public void _testErasureInVarArgs() throws Exception {
		runResourceTestCase("ErasureInVarArgs");
	}
	
	public void _testErasureInForEach() throws Exception {
		runResourceTestCase("ErasureInForEach");
	}
	
	@Override
	protected void runResourceTestCase(String resourceName) throws Exception {
		super.runResourceTestCase("integration/"  + resourceName);
	}
}
