/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;
import com.db4o.inside.*;


/**
 * @exclude
 */
public abstract class ObjectHeaderAttributes {
    
    public abstract void addBaseLength(int length);
    
    public abstract void addPayLoadLength(int length);
    
    public abstract void prepareIndexedPayLoadEntry(Transaction trans);
    

}
