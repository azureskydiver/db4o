/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.ext.*;


/**
 * @exclude
 */
public class VirtualAttributes implements Cloneable{
    
    public Db4oDatabase i_database;
    public long i_version;
    public long i_uuid;
    
    public VirtualAttributes shallowClone() {
        try {
            return (VirtualAttributes)this.clone();
        }catch(Exception e) {
        }
        return null;
    }

}
