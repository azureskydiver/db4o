/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.types.*;

/**
 * base class for database aware collections
 */
abstract class P1Collection extends P1Object implements Db4oCollection, Db4oTypeImpl{
    
    transient int i_activationDepth = -1;
    transient boolean i_deleteRemoved;
    
    public void activationDepth(int a_depth){
        i_activationDepth = a_depth;
    }
    
    public void deleteRemoved(boolean a_flag){
        i_deleteRemoved = a_flag;
    }
}
