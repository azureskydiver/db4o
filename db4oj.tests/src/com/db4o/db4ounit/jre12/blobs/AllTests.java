package com.db4o.db4ounit.jre12.blobs;
import db4ounit.extensions.*;

/**
 * @decaf.ignore.jdk11
 */
public class AllTests extends Db4oTestSuite {
	protected Class[] testCases() {
		return new Class[] {
				BlobThreadCloseTestCase.class,
				ExternalBlobsTestCase.class,
		};
	}

}
