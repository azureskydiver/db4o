package com.db4o.ibs;

/**
 * Any party interested in {@link ChangeSet} notifications.
 */
public interface ChangeSetListener {
	
	public void onChange(ChangeSet changes);

}
