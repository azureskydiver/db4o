package com.db4o.replication;

public interface ObjectState {

	Object getObject();

	boolean isNew();
	boolean wasModified();
	boolean wasDeleted();

}
