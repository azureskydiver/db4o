/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.ext.*;


/**
 * 
 */
class VirtualAttributes implements Cloneable{
    
    Db4oDatabase i_database;
    long i_version;
    long i_uuid;
    
    VirtualAttributes shallowClone() {
        try {
            return (VirtualAttributes)this.clone();
        }catch(Exception e) {
        }
        return null;
    }

}
