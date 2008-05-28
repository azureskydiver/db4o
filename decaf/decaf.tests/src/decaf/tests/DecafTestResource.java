package decaf.tests;

import sharpen.ui.tests.*;

public class DecafTestResource extends TestCaseResource {

	public DecafTestResource(String originalPath) {
		super(originalPath);
	}
	
	@Override
	protected String expectedPathSuffix() {
		return ".decaf.txt";
	}

}
