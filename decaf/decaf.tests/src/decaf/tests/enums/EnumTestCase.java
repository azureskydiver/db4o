package decaf.tests.enums;

import decaf.tests.DecafTestCaseBase;

public class EnumTestCase extends DecafTestCaseBase {
	public void testSimpleEnum() throws Exception {
		//runResourceTestCase("SimpleEnum");
	}
	
	public void _testComplexEnum() throws Exception {
		runResourceTestCase("ComplexEnum");
	}
	
	protected String packagePath() {
		return "enums";
	}
}
