/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.ext;


/**
 * db4o replication interface.
 */
public interface Db4oReplication {
    
    /**
     * commits the replication task.
     * <br><br>Call this method after replication is completed to
     * write all changes back to the database files. This method
     * will store information about the replication task to the
     * database file to allow incremental replication for future
     * replication runs.
     */
    public void commit();
    
    /**
     * ends a replication task without committing any changes.
     */
    public void rollback();
    
    /**
     * registers a callback handler to be notified on replication
     * conflicts.
     * <br><br>Conflicts occur, if an object has been modified in 
     * both the origin and destination ObjectContainer since the
     * last time replication was run between the two ObjectContainers.<br><br>
     * Upon a conflict the {@link Db4oCallback#callback(Object)} method will
     * be called in the conflict handler and a {@link Db4oReplicationConflict}
     * object will be passed as a parameter.  
     * @param conflictHandler the object to be called upon conflicts.
     */
    public void setConflictHandler(Db4oCallback conflictHandler);
    
    
    
    

}
