package decaf.tests.collections;

import decaf.tests.*;

public class CollectionsTestCase extends DecafTestCaseBase {
	
	public void testExtendsList() throws Exception {
		runPlatformTestCase("ExtendsList");
	}

	public void testSimpleMapUsage() throws Exception {
		runPlatformTestCase("SimpleMapUsage");
	}

	public void testSimpleListUsage() throws Exception {
		runPlatformTestCase("SimpleListUsage");
	}

	public void testForEachList() throws Exception {
		runResourceTestCase("ForEachList", TargetPlatform.JDK12);
	}

	@Override
	protected String packagePath() {
		return "collections";
	}

}
