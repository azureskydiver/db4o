/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.ext;

import com.db4o.*;


/**
 * will be passed to the {@link Db4oCallback} registered
 * in a {@link Db4oReplication} with #setConflictHandler()
 * in case an object that is replicated was changed in
 * both {@link ObjectContainer}s 
 * 
 */
public interface Db4oReplicationConflict {
    
    
    /**
     * returns the destination {@link ObjectContainer}. 
     * @return the destination {@link ObjectContainer}
     */
    public ObjectContainer destination();

    /**
     * gets the object that caused the conflict from the destination
     * {@link ObjectContainer}.
     * @return the object as it exists in the destination {@link ObjectContainer}. 
     */
    public Object destinationObject();
    
    /**
     * returns the source {@link ObjectContainer}. 
     * @return the source {@link ObjectContainer}
     */
    public ObjectContainer source();
    
    /**
     * gets the object that caused the conflict from the source
     * {@link ObjectContainer}.
     * @return the object as it exists in the source {@link ObjectContainer}. 
     */
    public Object sourceObject();
    
    /**
     * instructs the replication process to store the object from
     * the source {@link ObjectContainer} to both ObjectContainers.
     * <br><br>If neither #useSource() nor #useDestination() is called in the
     * {@link Db4oCallback}, replication will ignore the object that
     * caused the conflict.
     */
    public void useSource(); 
    
    /**
     * instructs the replication process to store the object from
     * the destination {@link ObjectContainer} to both ObjectContainers.
     * <br><br>If neither #useSource() nor #useDestination() is called in the
     * {@link Db4oCallback}, replication will ignore the object that
     * caused the conflict.
     */
    public void useDestination();
    

}
