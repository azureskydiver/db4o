package decaf.tests.generics;

import decaf.tests.*;

public class GenericsTestCase extends DecafTestCaseBase {
	
	public void testNestedGenerics() throws Exception {
		runResourceTestCase("NestedGenerics");
	}
	
	public void testGenericMethods() throws Exception {
		runResourceTestCase("GenericMethods");
	}
	
	public void testDeclarationErasureNoBounds() throws Exception {
		runResourceTestCase("DeclarationErasureNoBounds");
	}
	
	public void testIntroduceCastsForFields() throws Exception {
		runResourceTestCase("IntroduceCastsForFields");
	}
	
	public void testIntroduceCastsForMethods() throws Exception {
		runResourceTestCase("IntroduceCastsForMethods");
	}
	
	@Override
	protected void runResourceTestCase(String resourceName) throws Exception {
		super.runResourceTestCase("generics/"  + resourceName);
	}
}
