package decaf.tests.collections;

import decaf.tests.*;

public class CollectionsTestCase extends DecafTestCaseBase {

	public void testSimpleMapUsage() throws Exception {
		runResourceTestCase("SimpleMapUsage");
	}

	public void testSimpleListUsage() throws Exception {
		runResourceTestCase("SimpleListUsage");
	}
	
	@Override
	protected void runResourceTestCase(String resourceName) throws Exception {
		super.runResourceTestCase("collections/" + resourceName);
	}

}
