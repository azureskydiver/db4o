package decaf.tests.generics;

import decaf.tests.*;

public class GenericsTestCase extends DecafTestCaseBase {
	public void testDeclarationErasureNoBounds() throws Exception {
		runResourceTestCase("DeclarationErasureNoBounds");
	}
	
	public void testIntroduceCastsForFields() throws Exception {
		runResourceTestCase("IntroduceCastsForFields");
	}
	
	@Override
	protected void runResourceTestCase(String resourceName) throws Exception {
		super.runResourceTestCase("generics/"  + resourceName);
	}
}
