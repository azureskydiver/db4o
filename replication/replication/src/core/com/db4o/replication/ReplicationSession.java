/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.replication;

/**
 * Execute a round of replication between two ReplicationProviders.
 * <p/>
 * Sample code of using ReplicationSession:
 * <pre>
 * ReplicationSession session = Replication.begin(objectContainer1, objectContainer2);
 * session.replicate(object);
 * session.commit();
 * session.close();
 * </pre>
 *
 * @author Albert Kwan
 * @author Klaus Wuestefeld
 * @version 1.2
 * @see Replication
 * @since dRS 1.0
 */
public interface ReplicationSession {
	/**
	 * checks if an object has been modified in both replication providers
	 * since the last time the two replication providers were replicated.
	 *
	 * @param obj - the object to check for a conflict.
	 */
	public void checkConflict(Object obj);


	/**
	 * Closes this session and frees used resources. Sessions that were opened
	 * during the creation of ReplicationProviders will be closed as well.
	 * <p/>
	 * Hibernate Sessions created from Hibernate Configurations will be closed.
	 * db4o ObjectContainers will remain open.
	 *
	 * @throws IllegalStateException if the session is still active (neither commitReplicationTransaction() nor rollback is called).
	 */
	public void close();

	/**
	 * Commits replication changes to both ReplicationProviders and marks the
	 * involved ReplicationProviders to be "clean" against each other - to contain
	 * no modified objects.
	 */
	public void commit();

	/**
	 * Returns the ReplicationProvider representing the <b>first</b> persistence
	 * system passed as a parameter when the replication session was instantiated.
	 *
	 * @return the first persistence system
	 */
	public ReplicationProvider providerA();

	/**
	 * Returns the ReplicationProvider representing the <b>second</b> persistence
	 * system passed as a parameter when the replication session was instantiated.
	 *
	 * @return the second persistence system
	 */
	public ReplicationProvider providerB();

	/**
	 * Replicates an individual object and traverses to member objects as long as
	 * members are new or modified since the last time the two ReplicationProviders
	 * were replicated.
	 *
	 * @param obj the object to be replicated.
	 * @see ReplicationEventListener
	 */
	public void replicate(Object obj);

	/**
	 * Replicates all deletions between the two providers.
	 * Cascade delete is disabled, regardless the user's settings.
	 * <p/>
	 * If the deletion violates referential integrity, exception will be thrown.
	 * You can use the dRS replication callback to check whether the object to
	 * be deleted violates referential integrity. If so, you can stop traversal.
	 *
	 * @param extent the Class that you want to delete
	 */
	public void replicateDeletions(Class extent);

	/**
	 * Rollbacks all changes done during the replication session. This call
	 * guarantees the changes will not be applied to the underlying databases. The
	 * state of the objects involved in the replication is undefined.
	 * Both ReplicationProviders may still contain cached references of touched objects.
	 * <p/>
	 * To restart replication, it is recommended to reopen both involved
	 * ReplicationProviders and to start a new ReplicationSession.
	 */
	void rollback();

	/**
	 * Sets the direction of replication. By default, if this method is not called, replication is bidirectional,
	 * which means the newer copy of the object is copied to the other provider..
	 * <p/> If you want to force unidirectional replication, call this method before calling {@link #replicate}.
	 *
	 * @param from objects in this provider will not be changed.
	 * @param to   objects will be copied to this provider if copies in "from" is newer
	 */
	public void setDirection(ReplicationProvider from, ReplicationProvider to);
}
