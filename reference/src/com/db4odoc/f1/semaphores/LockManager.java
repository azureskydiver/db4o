/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.f1.semaphores;

import com.db4o.*;
import com.db4o.ext.*;

/**
 * This class demonstrates a very rudimentary implementation
 * of virtual "locks" on objects with db4o. All code that is
 * intended to obey these locks will have to call lock() and
 * unlock().  
 */
public class LockManager {
    
    private final String SEMAPHORE_NAME = "locked: ";
    private final int WAIT_FOR_AVAILABILITY = 300; // 300 milliseconds
    
    private final ExtObjectContainer _objectContainer;
    
    public LockManager(ObjectContainer objectContainer){
        _objectContainer = objectContainer.ext();
    }
    
    public boolean lock(Object obj){
        long id = _objectContainer.getID(obj);
        return _objectContainer.setSemaphore(SEMAPHORE_NAME + id, WAIT_FOR_AVAILABILITY);
    }
    
    public void unlock(Object obj){
        long id = _objectContainer.getID(obj);
        _objectContainer.releaseSemaphore(SEMAPHORE_NAME + id);
    }
}
