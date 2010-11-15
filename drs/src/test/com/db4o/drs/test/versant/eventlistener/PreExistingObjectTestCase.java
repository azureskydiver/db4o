/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant.eventlistener;

import javax.jdo.*;

import com.db4o.*;
import com.db4o.drs.*;
import com.db4o.drs.inside.*;
import com.db4o.drs.test.*;
import com.db4o.drs.test.versant.data.*;
import com.db4o.drs.versant.*;

import db4ounit.*;

public class PreExistingObjectTestCase implements TestCase {
	
	private static final String DATABASE_NAME = "PreExisting";
	
	public void test(){
		VodDatabase vod = new VodDatabase(DATABASE_NAME);
		
		try{
			vod.removeDb();
			vod.produceDb();
			vod.addJdoMetaDataFile(com.db4o.drs.test.versant.data.Item.class.getPackage());
			vod.createEventSchema();
			
			PersistenceManager pm = vod.persistenceManagerFactory().getPersistenceManager();
			pm.currentTransaction().begin();
			pm.makePersistent(new Item("one"));
			pm.currentTransaction().commit();
			pm.close();
			
			vod.startEventDriver();
			vod.startEventProcessor();
			
			try{
				VodReplicationProvider vodReplicationProvider = new VodReplicationProvider(vod);
				try {
					TransientReplicationProvider transientReplicationProvider = new TransientReplicationProvider(new byte[] {(byte)'T'}, "transient");
					Replication.begin(vodReplicationProvider, transientReplicationProvider);
					ObjectSet replicationSet = vodReplicationProvider.objectsChangedSinceLastReplication(Item.class);
					Assert.areEqual(1, replicationSet.size());
					Item item = (Item) replicationSet.next();
					ReplicationReference ref = vodReplicationProvider.produceReference(item);
					Assert.isNotNull(ref);
				}finally {
					vodReplicationProvider.destroy();
				}
			}finally {
				vod.stopEventProcessor();
				vod.stopEventDriver();
			}
		} finally {
			vod.removeDb();
		}
	}

}
