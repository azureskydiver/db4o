/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.ix;

import com.db4o.internal.*;
import com.db4o.internal.handlers.*;


/**
 * @exclude
 */
class IxDeprecationHelper {
    
    static Object comparableObject(Indexable4 handler, Transaction trans,  Object indexEntry ){
        if(handler instanceof StringHandler){
            return ((StringHandler)handler).val(indexEntry,trans.container());
        }
        return indexEntry; 
    }
}
