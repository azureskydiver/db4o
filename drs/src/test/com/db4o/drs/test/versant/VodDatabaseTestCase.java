/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

import java.io.*;

import javax.jdo.*;

import com.db4o.drs.versant.*;
import com.versant.odbms.*;
import com.versant.odbms.model.*;
import com.versant.trans.*;

import db4ounit.*;

public class VodDatabaseTestCase implements TestCase, ClassLevelFixtureTest {
	
	private static final String DATABASE_NAME = "VodDatabaseTestCase";
	
	public static void classSetUp() throws Exception {
		_vod = new VodDatabase(DATABASE_NAME);
		_vod.createDb();
		_vod.enhance();
	}

	public static void classTearDown() {
		_vod.removeDb();
	}
	
	private static VodDatabase _vod;
	
	public void testVersantRootPath() throws IOException, InterruptedException{
		String path = _vod.versantRootPath();
		File file = new File(path);
		Assert.isTrue(file.exists());
		Assert.isTrue(file.isDirectory());
	}
	
	public void testPersistenceManagerFactory(){
		PersistenceManager pmf = _vod.createPersistenceManager();
		Assert.isFalse(pmf.isClosed());
		pmf.close();
	}
	
	public void testJviSession(){
		TransSession session = _vod.createTransSession();
		Assert.isNotNull(session);
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
