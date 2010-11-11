/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */
package com.db4o.drs.vod.replication;

import java.util.*;

import javax.jdo.*;
import com.db4o.drs.vod.example.*;
import com.db4o.drs.vod.example.data.*;

public class Program {

	public static void main(String[] args) {
		new Program().run();
	}

	private PersistenceManager _pm;

	private void run() {
		_pm = JdoConnector.getPersistenceManager();
		_pm.currentTransaction().begin();
		printVodDatabaseContent();
		
		_pm.currentTransaction().commit();
		_pm.close();
	}
		

	private void printVodDatabaseContent(){
		Collection<Contract> contracts = (Collection<Contract>) _pm.newQuery(Contract.class).execute();
		System.out.println(contracts.toString());
		
		
	}
}
