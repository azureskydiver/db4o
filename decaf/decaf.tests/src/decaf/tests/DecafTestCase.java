package decaf.tests;

public class DecafTestCase extends DecafTestCaseBase {

	public void testForEachArray() throws Exception {
		runResourceTestCase("ForEachArray");
	}

	public void testForEachArrayMethod() throws Exception {
		runResourceTestCase("ForEachArrayMethod");
	}
	
	public void testNestedForEach() throws Exception {
		runResourceTestCase("NestedForEach");
	}
	
	public void testDeepVarArgs() throws Exception {
		runResourceTestCase("DeepVarArgs");
	}

	public void testVarArgsMethod() throws Exception {
		runResourceTestCase("VarArgsMethod");
	}

	public void testVarArgsGenericMethod() throws Exception {
		runResourceTestCase("VarArgsGenericMethod");
	}
	
	public void testPackageDeclaration() throws Exception {
		runResourceTestCase("PackageDeclaration");
	}

	public void testSameSignatureConstructor() throws Exception {
		runResourceTestCase("SameSignatureConstructor");
	}
	
	public void testGenericForEach() throws Exception {
		runResourceTestCase("GenericForEach");
	}

	public void testEnums() throws Exception {
		runResourceTestCase("Enums");
	}
}
