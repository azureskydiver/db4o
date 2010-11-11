/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */
package com.db4o.drs.vod.company;

import java.util.*;

import javax.jdo.*;

import com.db4o.drs.vod.company.data.*;

public class Program {
	
	public static void main(String[] args) {
		new Program().run();
	}
	
	private PersistenceManager _pm;

	private void run() {
		PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory(properties());
		_pm = pmf.getPersistenceManager();
		_pm.currentTransaction().begin();
		
		storeInitialData();
		
//		Customer melissa = new Customer("Melissa");
//		_pm.makePersistent(melissa);
//		_pm.makePersistent(new Contract("Health Insurance", melissa, 100));
		
		_pm.currentTransaction().commit();
		_pm.close();
		pmf.close();
		
	}

	private void storeInitialData() {
		Customer rickie = new Customer("Rickie");
		_pm.makePersistent(rickie);
		_pm.makePersistent(new Contract("House Insurance", rickie, 100));
		System.out.println("storeInitialData " + rickie);
	}

	private Properties properties() {
		Properties properties = new Properties();
		properties.setProperty("javax.jdo.option.ConnectionURL", "versant:drsCompany@localhost");
		properties.setProperty("javax.jdo.PersistenceManagerFactoryClass","com.versant.core.jdo.BootstrapPMF");
		properties.setProperty("javax.jdo.PersistenceManagerFactoryClass","com.versant.core.jdo.BootstrapPMF");
		properties.setProperty("versant.metadata.0", "com/db4o/drs/vod/company/data/package.jdo");
		return properties;
	}

}
