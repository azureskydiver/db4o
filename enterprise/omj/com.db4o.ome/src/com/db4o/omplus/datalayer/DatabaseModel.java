/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */
package com.db4o.omplus.datalayer;

import com.db4o.*;
import com.db4o.omplus.datalayer.propertyViewer.*;

public class DatabaseModel {
	private IDbInterface dbi;
	private PropertiesManager props;
	
	public IDbInterface db() {
		return dbi;
	}
	
	public PropertiesManager props() {
		return props;
	}
	
	public void connect(ObjectContainer db, String path) {
		disconnect();
		dbi = new DbInterfaceImpl(db, path);
		props = new PropertiesManager(dbi);
	}
	
	public boolean connected() {
		return dbi != null;
	}
	
	public void disconnect() {
		if(dbi == null) {
			return;
		}
		try {
			dbi.close();
		}
		finally {
			dbi = null;
			props = null;
		}
	}
}
