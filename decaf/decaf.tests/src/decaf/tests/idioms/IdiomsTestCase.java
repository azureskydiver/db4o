package decaf.tests.idioms;

import decaf.tests.*;

public class IdiomsTestCase extends DecafTestCaseBase {

	public void testStringIdioms() throws Exception {
		runResourceTestCase("StringIdioms");
	}

	@Override
	protected String packagePath() {
		return "idioms";
	}

}
