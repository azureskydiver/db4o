package decaf.tests.annotations;

import decaf.core.*;
import decaf.tests.*;

public class AnnotationsTestCase extends DecafTestCaseBase {
	
	public void testPublic() throws Exception {
		runPlatformTestCase("Public");
	}
	
	public void testRemoveClass() throws Exception {
		runPlatformTestCase("RemoveClass");
	}
	
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
		runPlatformTestCase("RemoveFirst");
	}
	
	public void testInsertFirst() throws Exception {
		runResourceTestCase("InsertFirst");
	}
	
	public void testMixinAsNestedClass() throws Exception {
		runResourceTestCase("MixinAsNestedClass");
	}
	
	public void testJdk5AnnotationsAreAutomaticallyIgnored() throws Exception {
		runResourceTestCase("Jdk5AnnotationsAreAutomaticallyIgnored", TargetPlatform.NONE, TargetPlatform.ANDROID);
	}
	
	public void testIgnoreImplements() throws Exception {
		runPlatformTestCase("IgnoreImplements");
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

	public void testRemoveAllClassesInArray() throws Exception {
		runResourceTestCase("RemoveAllClassesInArray");
	}

	@Override
	protected String packagePath() {
		return "annotations";
	}
}
