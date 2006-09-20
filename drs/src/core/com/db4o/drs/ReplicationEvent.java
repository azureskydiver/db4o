package com.db4o.drs;

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
	 * Overrides default replication behaviour with some state chosen by the user.
	 *
	 * @param chosen the ObjectState of the prevailing object or null if replication should ignore this object and not traverse to its referenced objects.
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
	 * The time when the object is created in one provider.
	 *
	 * @return time when the object is created in one provider.
	 */
	long objectCreationDate();

	/**
	 * The replication process will not traverse to objects referenced by the current one.
	 */
	void stopTraversal();
}
