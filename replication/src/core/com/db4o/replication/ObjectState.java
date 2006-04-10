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
	 * @return The entity
	 */
	Object getObject();

	/**
	 * Is the object newly created since last replication?
	 *
	 * @return true when the object is newly created since last replication
	 */
	boolean isNew();

	/**
	 * Was the object deleted since last replication?
	 *
	 * @return true when the object was deleted since last replication
	 */
	boolean wasDeleted();

	/**
	 * Was the object modified since last replication?
	 *
	 * @return true when the object was modified since last replication
	 */
	boolean wasModified();
}
