/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */
package com.db4o.drs.vod.example;

import java.util.*;
import javax.jdo.*;

public class JdoConnector {
	
	public static PersistenceManager getPersistenceManager() {
		Properties properties = new Properties();
		properties.setProperty("javax.jdo.option.ConnectionURL", "versant:dRsVodExample@localhost");
		properties.setProperty("javax.jdo.PersistenceManagerFactoryClass","com.versant.core.jdo.BootstrapPMF");
		properties.setProperty("javax.jdo.PersistenceManagerFactoryClass","com.versant.core.jdo.BootstrapPMF");
		properties.setProperty("versant.metadata.0", "com/db4o/drs/vod/example/data/package.jdo");
		PersistenceManagerFactory pmf = 
			JDOHelper.getPersistenceManagerFactory(properties);
		return pmf.getPersistenceManager();
	}

}
