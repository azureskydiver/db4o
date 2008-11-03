package decaf.tests.enums;

import decaf.tests.DecafTestCaseBase;

public class EnumTestCase extends DecafTestCaseBase {
	public void testSimpleEnum() throws Exception {
		runResourceTestCase("SimpleEnum");
	}
	
	public void testEnumsWithConstructors() throws Exception {
		runResourceTestCase("EnumsWithConstructors");
	}
	
	public void testComplexEnum() throws Exception {
		runResourceTestCase("ComplexEnum");
	}	
	
	public void _testEnumUsage() throws Exception {
		runResourceTestCase("EnumUsage");
	}
	
	protected String packagePath() {
		return "enums";
	}
}
