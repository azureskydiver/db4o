/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;


import javax.jdo.*;

import com.versant.odbms.*;
import com.versant.odbms.model.*;

import db4ounit.*;

public class VodDatabaseTestCase extends VodDatabaseTestCaseBase {
	
	public void testPersistenceManagerFactory(){
		PersistenceManager pmf = _vod.createPersistenceManager();
		Assert.isFalse(pmf.isClosed());
		pmf.close();
	}
	
	public void testSchema(){
		DatastoreManager dm = _vod.createDatastoreManager();
		DatastoreInfo info = dm.getPrimaryDatastoreInfo();
		SchemaEditor editor = dm.getSchemaEditor();
		long[] classLoids = dm.locateAllClasses(info, false);
		for (int i = 0; i < classLoids.length; i++) {
			DatastoreSchemaClass dc = editor.findClass(classLoids[i], info);
			System.out.println(dc.getName());
		}
		dm.close();
	}

}
