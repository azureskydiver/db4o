package decaf.tests.annotations;

import decaf.tests.*;

public class AnnotationsTestCase extends DecafTestCaseBase {
	
	public void testPlatformDependentIgnoreExtends() throws Exception {
		runPlatformTestCase("PlatformDependentIgnoreExtends");
	}
	
	public void testPlatformDependentIgnoreImplements() throws Exception {
		runPlatformTestCase("PlatformDependentIgnoreImplements");
	}
	
	public void testPlatformDependentIgnore() throws Exception {
		runPlatformTestCase("PlatformDependentIgnore");
	}
	
	public void testReplaceFirst() throws Exception {
		runResourceTestCase("ReplaceFirst");
	}
	
	public void testRemoveFirst() throws Exception {
		runResourceTestCase("RemoveFirst");
	}
	
	public void testInsertFirst() throws Exception {
		runResourceTestCase("InsertFirst");
	}
	
	public void testMixinAsNestedClass() throws Exception {
		runResourceTestCase("MixinAsNestedClass");
	}
	
	public void testJdk5AnnotationsAreAutomaticallyIgnored() throws Exception {
		runResourceTestCase("Jdk5AnnotationsAreAutomaticallyIgnored");
	}
	
	public void testIgnoreImplements() throws Exception {
		runResourceTestCase("IgnoreImplements");
	}
	
	public void testIgnoreExtends() throws Exception {
		runResourceTestCase("IgnoreExtends");
	}
	
	public void testIgnoreMainType() throws Exception {
		runResourceTestCase("IgnoreMainType");
	}

	public void testIgnoreClass() throws Exception {
		runResourceTestCase("IgnoreClass");
	}

	public void testIgnoreMethod() throws Exception {
		runResourceTestCase("IgnoreMethod");
	}
	
	@Override
	protected String packagePath() {
		return "annotations";
	}
}
