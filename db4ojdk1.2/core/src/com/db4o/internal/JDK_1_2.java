/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import java.lang.ref.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.reflect.generic.*;
import com.db4o.types.*;

class JDK_1_2 extends JDKReflect {
	
	JDK_1_2(){
	}
	
	public static void link(){
	    // link standard translators, so they won't get deleted
	    // by deployment
	    
	    Object obj = new TCollection();
	    obj = new TMap();
	    obj = new TSerializable();
	    obj = new TTreeMap();
	    obj = new TTreeSet();
	}

    Db4oCollections collections(ObjectContainerBase a_stream){
        return new P2Collections(a_stream);
    }

    Object createReferenceQueue() {
        return new ReferenceQueue4();
    }

    public Object createWeakReference(Object obj){
        return new WeakReference(obj);
    }
    
    Object createYapRef(Object a_queue, ObjectReference a_yapObject, Object a_object) {
        return new ActiveObjectReference(a_queue, a_yapObject, a_object);
    }

    Object getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    void forEachCollectionElement(Object a_object, Visitor4 a_visitor) {
        java.util.Iterator i = null;
        if (a_object instanceof java.util.Collection) {
            i = ((java.util.Collection) a_object).iterator();
        } else if (a_object instanceof java.util.Map) {
            i = ((java.util.Map) a_object).keySet().iterator();
        }
        if (i != null) {
            while (i.hasNext()) {
                a_visitor.visit(i.next());
            }
        }
    }

    Object getYapRefObject(Object a_object) {
        if (a_object instanceof ActiveObjectReference) {
            return ((ActiveObjectReference) a_object).get();
        }
        return a_object;
    }
    
    boolean isCollectionTranslator(Config4Class a_config) {
        if (a_config != null) {
            ObjectTranslator ot = a_config.getTranslator();
            if (ot != null) {
                return ot instanceof TCollection || ot instanceof TMap || ot instanceof THashtable;
            }
        }
        return false;
    }
    
    public int ver(){
        return 2;
    }
    
	void killYapRef(Object obj){
		if(obj instanceof ActiveObjectReference){
			((ActiveObjectReference)obj)._referent = null;
		}
	}

    void pollReferenceQueue(ObjectContainerBase a_stream, Object a_referenceQueue) {
        if (a_referenceQueue != null) {
            ReferenceQueue4 yrq = (ReferenceQueue4) a_referenceQueue;
            ActiveObjectReference ref;
            synchronized(a_stream.lock()){
	            while ((ref = yrq.yapPoll()) != null) {
	                a_stream.purge1(ref._referent);
	            }
            }
        }
    }
    
	public void registerCollections(GenericReflector reflector) {
		reflector.registerCollection(java.util.Collection.class);
		reflector.registerCollection(java.util.Map.class);
		reflector.registerCollectionUpdateDepth(java.util.Map.class, 3);
	}

    void setAccessible(Object a_accessible) {
        ((java.lang.reflect.AccessibleObject) a_accessible).setAccessible(true);
    }
    
    public Object weakReferenceTarget(Object weakRef){
        if(weakRef instanceof WeakReference){
            return ((WeakReference)weakRef).get();
        }
        return weakRef;
    }

}
