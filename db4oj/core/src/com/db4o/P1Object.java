/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.internal.*;

/**
 * Kept for 5.7 migration support (PBootRecord depends on it).
 * 
 * @exclude
 * @persistent
 * @deprecated
 */
public class P1Object implements Db4oTypeImpl{
    
    public P1Object(){
    }
    
    public Object createDefault(Transaction a_trans) {
        throw Exceptions4.virtualException();
    }
    
    public boolean hasClassIndex() {
        return false;
    }	

    public void setTrans(Transaction a_trans){
    }

    public void setObjectReference(ObjectReference objectReference) {
    }
    
}
