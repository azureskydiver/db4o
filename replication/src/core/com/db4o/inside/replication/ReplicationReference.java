package com.db4o.inside.replication;

import com.db4o.ext.*;

public interface ReplicationReference {

	Db4oUUID uuid();

	/**
	 * 
	 *  IMPORTANT
	 * 
	 * Must return the latests version of the object AND OF ALL COLLECTIONS IT REFERENCES IN ITS
	 * FIELDS because collections are treated as 2nd class objects (just like arrays) for Hibernate replication
	 * compatibility purposes. 
	 */
	long version();

	Object object();
	Object counterpart();
	void setCounterpart(Object obj);

	void markForReplicating();
	boolean isMarkedForReplicating();

}
