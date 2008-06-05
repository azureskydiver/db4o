package decaf.tests.integration;

import decaf.tests.*;

public class IntegrationTestCase extends DecafTestCaseBase {

	public void testErasureInVarArgs() throws Exception {
		runResourceTestCase("integration/ErasureInVarArgs");
	}
	
	public void testErasureInForEach() throws Exception {
		runResourceTestCase("integration/ErasureInForEach");
	}
}
