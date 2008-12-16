package decaf.tests.idioms;

import decaf.tests.*;

public class IdiomsTestCase extends DecafTestCaseBase {

	public void testStringIdioms() throws Exception {
		runResourceTestCase("StringIdioms");
	}
	
	public void testClassCast() throws Exception {
		runResourceTestCase("ClassCast");
	}

	@Override
	protected String packagePath() {
		return "idioms";
	}

}
