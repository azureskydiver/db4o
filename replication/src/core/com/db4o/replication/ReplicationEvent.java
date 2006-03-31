package com.db4o.replication;

public interface ReplicationEvent {

	ObjectState stateInProviderA();
	ObjectState stateInProviderB();
	
	boolean isConflict();
	
	void overrideWith(ObjectState chosen);
	void stopTraversal();
	
}
