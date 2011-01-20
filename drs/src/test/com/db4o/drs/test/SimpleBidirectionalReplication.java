/* Copyright (C) 2004 - 2011  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test;

import com.db4o.*;
import com.db4o.drs.*;
import com.db4o.drs.inside.*;
import com.db4o.drs.test.data.*;
import com.db4o.foundation.*;

import db4ounit.*;

public class SimpleBidirectionalReplication extends DrsTestCase {
	
	public void testObjectsAreOnlyReplicatedOnce(){
		TestableReplicationProviderInside providerA = a().provider();
		TestableReplicationProviderInside providerB = b().provider();
		
		storeNewPilotIn(providerA);
		
		int replicatedObjects = replicateBidirectional(providerA, providerB);
		Assert.areEqual(1, replicatedObjects);
		
		modifyPilotIn(providerA, "modifiedInA");
		replicatedObjects = replicateBidirectional(providerA, providerB);
		Assert.areEqual(1, replicatedObjects);
		
		modifyPilotIn(providerB, "modifiedInB");
		replicatedObjects = replicateBidirectional(providerA, providerB);
		Assert.areEqual(1, replicatedObjects);
		
		storeNewPilotIn(providerA);
		storeNewPilotIn(providerB);
		
		replicatedObjects = replicateBidirectional(providerA, providerB);
		Assert.areEqual(2, replicatedObjects);
		
	}

	private void modifyPilotIn(TestableReplicationProviderInside provider, String newName) {
		Pilot pilot;
		pilot = (Pilot) provider.getStoredObjects(Pilot.class).next();
		pilot.setName(newName);
		provider.update(pilot);
		provider.commit();
		provider.waitForPreviousCommits();
	}

	private int replicateBidirectional(
			TestableReplicationProviderInside providerA,
			TestableReplicationProviderInside providerB) {
		
		final IntByRef replicatedObjects = new IntByRef();
		ReplicationEventListener listener = new ReplicationEventListener() {
			public void onReplicate(ReplicationEvent e) {
				replicatedObjects.value ++;
			}
		};
		
		ReplicationSession replicationSession = Replication.begin(providerA, providerB, listener);
		ObjectSet changedInA = providerA.objectsChangedSinceLastReplication();
		for(Object obj: changedInA){
			replicationSession.replicate(obj);
		}
		ObjectSet changedInB = providerB.objectsChangedSinceLastReplication();
		for(Object obj: changedInB){
			replicationSession.replicate(obj);
		}
		replicationSession.commit();
		return replicatedObjects.value;
	}

	private void storeNewPilotIn(TestableReplicationProviderInside provider) {
		Pilot pilot = new Pilot();
		provider.storeNew(pilot);
		provider.commit();
		provider.waitForPreviousCommits();
	}

}
