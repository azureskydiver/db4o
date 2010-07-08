/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

import com.db4o.drs.versant.*;

import db4ounit.*;

public abstract class VodDatabaseTestCaseBase implements TestCase {
	
	protected static void registerMetadataFile(VodDatabase vod) {
		vod.amendPropertyIfNotExists("versant.metadata.0", "drs-test.jdo");
		vod.amendPropertyIfNotExists("versant.metadata.1", "drs.jdo");
	}

}
