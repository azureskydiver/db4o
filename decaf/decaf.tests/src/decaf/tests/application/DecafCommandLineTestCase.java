package decaf.tests.application;

import decaf.application.*;
import junit.framework.*;

public class DecafCommandLineTestCase extends TestCase {
	
	public void testProjectReference() {
		DecafCommandLine cmdLine = DecafCommandLineParser.parse("db4o.tests", "-projectReference", "db4o");
		assertEquals("db4o.tests", cmdLine.project);
		assertEquals(1, cmdLine.projectReferences.size());
		assertEquals("db4o", cmdLine.projectReferences.get(0));
	}

}
