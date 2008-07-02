package decaf.tests.boxing;

import decaf.tests.*;

public class AutoBoxingTestCase extends DecafTestCaseBase {
	
	public void testAutoUnboxingInIf() throws Exception {
		runResourceTestCase("AutoUnboxingInIf");
	}
	
	public void testAutoUnboxing() throws Exception {
		runResourceTestCase("AutoUnboxing");
	}
	
	public void testAutoBoxing() throws Exception {
		runResourceTestCase("AutoBoxing");
	}

	public void testMethodArgBoxing() throws Exception {
		runResourceTestCase("MethodArgBoxing");
	}

	public void testTernaryOpBoxing() throws Exception {
		runResourceTestCase("TernaryOpBoxing");
	}

	@Override
	protected String packagePath() {
		return "boxing";
	}

}
