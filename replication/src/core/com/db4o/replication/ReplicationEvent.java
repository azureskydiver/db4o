package com.db4o.replication;

/**
 * Defines an event class for the replication of an entity.
 *
 * @author Albert Kwan
 * @author Klaus Wuestefeld
 * @version 1.2
 * @since dRS 1.2
 */
public interface ReplicationEvent {
	/**
	 * Does a conflict occur?
	 *
	 * @return true when a conflict occur
	 */
	boolean isConflict();

	/**
	 * The user can choose to override the prevailing object.
	 *
	 * @param chosen the ObjectState of the prevailing object
	 */
	void overrideWith(ObjectState chosen);

	/**
	 * The ObjectState in provider A.
	 *
	 * @return ObjectState in provider A
	 */
	ObjectState stateInProviderA();

	/**
	 * The ObjectState in provider B.
	 *
	 * @return ObjectState in provider B
	 */
	ObjectState stateInProviderB();

	/**
	 * Stop the replication of this object and the traversal to its child objects.
	 */
	void stopTraversal();
}
