package decaf.tests.application;

import junit.framework.*;
import decaf.application.*;
import decaf.builder.*;

public class DecafCommandLineTestCase extends TestCase {
	
	public void testProjectReference() {
		DecafCommandLine cmdLine = DecafCommandLineParser.parse("db4o.tests", "-projectReference", "db4o");
		assertEquals("db4o.tests", cmdLine.project);
		assertEquals(1, cmdLine.projectReferences.size());
		assertEquals("db4o", cmdLine.projectReferences.get(0));
		assertTrue(cmdLine.targetPlatforms.isEmpty());
	}
	
	public void testTargetPlatform() {
		DecafCommandLine cmdLine = DecafCommandLineParser.parse("db4o.tests", "-targetPlatform", "jdk12", "-targetPlatform", "jdk11");
		
		assertEquals(2, cmdLine.targetPlatforms.size());
		assertSame(TargetPlatform.JDK12, cmdLine.targetPlatforms.get(0));
		assertSame(TargetPlatform.JDK11, cmdLine.targetPlatforms.get(1));
		
	}

}
