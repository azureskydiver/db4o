/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;


/**
 * @exclude
 */
public class ObjectID {
    
    public final int _id;
    
    public static final ObjectID IS_NULL = new ObjectID(-1);
    
    public static final ObjectID NOT_POSSIBLE = new ObjectID(-2);

    public static final ObjectID IGNORE = new ObjectID(-3);
    
    public ObjectID(int id){
        _id = id;
    }
    
    public boolean isValid(){
        return _id > 0;
    }

}
