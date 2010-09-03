/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant;

import java.util.*;

public class JviDatabaseIdFactory implements VodDatabaseIdFactory {

	private final VodJvi _jvi;
	private final Set<String> _databaseNames = new HashSet<String>();

	public JviDatabaseIdFactory(VodDatabase vod) {
		_jvi = new VodJvi(vod);
	}
	
	public int createDatabaseIdFor(String databaseName) {
		short id = _jvi.newDbId(databaseName);
		_databaseNames.add(databaseName);
		return id;
	}

	public void deleteGeneratedIds() {
		for (String databaseName : _databaseNames) {
			_jvi.deleteDbId(databaseName);
		}
	}

}
