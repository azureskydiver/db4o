package com.db4o.replication;

/**
 * The state of the entity in a provider.
 *
 * @author Albert Kwan
 * @author Klaus Wuestefeld
 * @version 1.2
 * @since dRS 1.2
 */
public interface ObjectState {
	/**
	 * The entity.
	 *
	 * @return null if the object has been deleted or if it was not replicated in previous replications.
	 */
	Object getObject();

	/**
	 * Is the object newly created since last replication?
	 *
	 * @return true when the object is newly created since last replication
	 */
	boolean isNew();

	/**
	 * Was the object modified since last replication?
	 *
	 * @return true when the object was modified since last replication
	 */
	boolean wasModified();

	/**
	 * The time when the object is modified in a provider.
	 *
	 * @return time when the object is modified in a provider.
	 */
	long modificationDate();
}
