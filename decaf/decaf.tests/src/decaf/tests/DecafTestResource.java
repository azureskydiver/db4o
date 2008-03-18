package decaf.tests;

import javatocsharp.ui.tests.TestCaseResource;

public class DecafTestResource extends TestCaseResource {

	public DecafTestResource(String originalPath) {
		super(originalPath);
	}
	
	@Override
	protected String expectedPathSuffix() {
		return ".decaf.txt";
	}

}
