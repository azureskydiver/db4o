/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.replication;

import com.db4o.ext.*;


/**
 * ReplicationHandler for the new external Replication code
 * @exclude
 */
public class ReplicationHandler {
    
    public static final int OLD = -1;
    public static final int NONE = 0;
    public static final int NEW = 1;
    
    // short term memory, can change to Map, if needed 
    private Object _currentObject;
    private long _currentUuidLong;
    private Db4oDatabase _currentProvider;
    private long _currentVersion;
    
    public void associateObjectWith(Object obj, Db4oDatabase provider, long uuidLong, long version){
        _currentObject = obj;
        _currentUuidLong = uuidLong;
        _currentProvider = provider;
        _currentVersion = version;
    }
    
    public long uuidLongFor(Object obj){
        if(_currentObject == obj){
            return _currentUuidLong;
        }
        return 0;
    }
    
    public Db4oDatabase providerFor(Object obj){
        if(_currentObject == obj){
            return _currentProvider;
        }
        return null;
    }
    
    public long versionFor(Object obj){
        if(_currentObject == obj){
            return _currentVersion;
        }
        return 0;
    }
    
    
}
