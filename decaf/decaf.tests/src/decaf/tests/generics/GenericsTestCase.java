package decaf.tests.generics;

import decaf.tests.*;

public class GenericsTestCase extends DecafTestCaseBase {
	
	public void testCovarianceErasure() throws Exception {
		runResourceTestCase("CovarianceErasure");
	}
	
	public void testBoundedType() throws Exception {
		runResourceTestCase("BoundedType");
	}

	// FIXME
	public void _testBoundedInheritance() throws Exception {
		runResourceTestCase("BoundedInheritance");
	}

	public void testMultipleGenericParameters() throws Exception {
		runResourceTestCase("MultipleGenericParameters");
	}
	
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
	protected String packagePath() {
		return "generics";
	}

}
