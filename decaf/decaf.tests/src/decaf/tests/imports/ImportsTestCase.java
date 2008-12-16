package decaf.tests.imports;

import decaf.tests.*;

public class ImportsTestCase extends DecafTestCaseBase {
	
	public void testStaticImport() throws Exception {
		runResourceTestCase("StaticImport");
	}

	@Override
	protected String packagePath() {
		return "imports";
	}
}
