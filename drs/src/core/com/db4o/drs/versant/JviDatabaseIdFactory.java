/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant;

public class JviDatabaseIdFactory implements VodDatabaseIdFactory {

	private final VodJvi _jvi;

	public JviDatabaseIdFactory(VodDatabase vod) {
		_jvi = new VodJvi(vod);
	}
	
	public int createDatabaseIdFor(String databaseName) {
		return _jvi.newDbId(databaseName);
	}

}
