/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.ext.*;

/**
 * tracks the version of the last replication between
 * two Objectcontainers.
 * 
 *  @exclude
 */
public class ReplicationRecord implements Internal{
   
    public Db4oDatabase i_source;
    public Db4oDatabase i_target;
    
    public long i_version;
    
    public ReplicationRecord(){
        
    }
    
    
}
