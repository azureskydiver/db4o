/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

import java.io.*;

import com.db4o.drs.versant.*;
import com.versant.trans.*;

import db4ounit.*;

public class VodJviTestCase extends VodDatabaseTestCaseBase implements TestLifeCycle{
	
	
	private VodJvi _jvi;
	
	
	public void testVersantRootPath() throws IOException, InterruptedException{
		String path = _jvi.versantRootPath();
		File file = new File(path);
		Assert.isTrue(file.exists());
		Assert.isTrue(file.isDirectory());
	}
	
	public void testJviSession(){
		TransSession session = _jvi.createTransSession();
		Assert.isNotNull(session);
	}
	
	public void testNewDbId(){
		// using the same name twice will fail
		String name = VodJvi.safeDatabaseName("VodJviTestCase.test" + System.currentTimeMillis());
		short id = _jvi.newDbId(name);
		Assert.isGreater(0, id);
		Assert.areEqual(id, _jvi.dbIdFor(name));
	}

	public void testDeleteDbId() {
		final String name = VodJvi.safeDatabaseName("VodJviTestCase.test" + System.currentTimeMillis());
		_jvi.newDbId(name);
		_jvi.deleteDbId(name);
		Assert.expect(IllegalArgumentException.class, new CodeBlock() {
			public void run() throws Throwable {
				_jvi.dbIdFor(name);
			}
		});
	}
	
	public void setUp() throws Exception {
		_jvi = new VodJvi(_vod);
	}

	public void tearDown() throws Exception {
		_jvi.close();
	}


}
