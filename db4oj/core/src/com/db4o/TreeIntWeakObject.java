/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */
package com.db4o;

import com.db4o.foundation.*;

/**
 * @exclude
 */
public class TreeIntWeakObject extends TreeIntObject{

    public TreeIntWeakObject(int key) {
        super(key);
    }
    
    public TreeIntWeakObject(int key, Object obj) {
        super(key, Platform4.createWeakReference(obj));
    }
    
    public Object getObject(){
        return Platform4.weakReferenceTarget(i_object);
    }
    
    public void setObject(Object obj){
        i_object = Platform4.createWeakReference(obj);
    }
    
    public final TreeIntWeakObject traverseRemoveEmpty(final Visitor4 visitor){
        if(i_preceding != null){
            i_preceding = ((TreeIntWeakObject)i_preceding).traverseRemoveEmpty(visitor);
        }
        if(i_subsequent != null){
            i_subsequent = ((TreeIntWeakObject)i_subsequent).traverseRemoveEmpty(visitor);
        }
        Object referent = Platform4.weakReferenceTarget(i_object);
        if(referent == null){
            return (TreeIntWeakObject)remove();
        }
        visitor.visit(referent);
        calculateSize();
        return this;
    }


    
    

}
