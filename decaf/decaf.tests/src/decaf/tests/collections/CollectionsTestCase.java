package decaf.tests.collections;

import decaf.tests.*;

public class CollectionsTestCase extends DecafTestCaseBase {

	public void testSimpleMapUsage() throws Exception {
		runPlatformTestCase("SimpleMapUsage");
	}

	public void testSimpleListUsage() throws Exception {
		runPlatformTestCase("SimpleListUsage");
	}
	
	@Override
	protected String packagePath() {
		return "collections";
	}

}
