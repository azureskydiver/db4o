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
		Assert.isGreater(0, _jvi.newDbId("VodJviTestCase.test" + System.currentTimeMillis() ));
	}

	public void setUp() throws Exception {
		_jvi = new VodJvi(_vod);
	}

	public void tearDown() throws Exception {
		_jvi.close();
	}


}
