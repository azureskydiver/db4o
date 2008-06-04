package decaf.tests;

public class DecafTestCase extends DecafTestCaseBase {
	
	public void testBooleanAutoUnboxing() throws Exception {
		runResourceTestCase("BooleanAutoUnboxing");
	}

	public void testForEachArray() throws Exception {
		runResourceTestCase("ForEachArray");
	}

	public void testForEachArrayMethod() throws Exception {
		runResourceTestCase("ForEachArrayMethod");
	}

	public void testVarArgsMethod() throws Exception {
		runResourceTestCase("VarArgsMethod");
	}

	public void testVarArgsGenericMethod() throws Exception {
		runResourceTestCase("VarArgsGenericMethod");
	}

}
