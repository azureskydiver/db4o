package com.db4o.db4ounit.common.references;

import com.db4o.internal.*;

public class HashcodeReferenceSystemTestCase extends ReferenceSystemTestCaseBase {
	
	protected ReferenceSystem createReferenceSystem() {
		return new HashcodeReferenceSystem();
	}

}
