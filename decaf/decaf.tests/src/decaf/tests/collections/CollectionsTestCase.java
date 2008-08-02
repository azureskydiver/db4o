package decaf.tests.collections;

import decaf.core.*;
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
		runPlatformTestCase("ForEachList");
	}
	
	public void testIterableMapping() throws Exception {
//		runPlatformTestCase("IterableMapping");
		runResourceTestCase("IterableMapping", TargetPlatform.JDK11);
	}

	@Override
	protected String packagePath() {
		return "collections";
	}

}
