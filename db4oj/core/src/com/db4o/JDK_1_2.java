/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.config.*;
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

    int collectionUpdateDepth(Class a_class) {
        return java.util.Map.class.isAssignableFrom(a_class) ? 3 : 2;
    }
    
    Db4oCollections collections(YapStream a_stream){
        return new P2Collections(a_stream);
    }

    Object createReferenceQueue() {
        return new YapReferenceQueue();
    }

    YapRef createYapRef(Object a_queue, YapObject a_yapObject, Object a_object) {
        return new YapRef(a_queue, a_yapObject, a_object);
    }

    ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    void flattenCollection2(final YapStream a_stream, Object a_object, final com.db4o.Collection4 col) {
        if (isCollection(a_object.getClass())) {
            forEachCollectionElement(a_object, new Visitor4() {
                public void visit(Object obj) {
                    Platform.flattenCollection1(a_stream, obj, col);
                }
            });
        } else {
            col.add(a_object);
        }
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
        if (a_object instanceof YapRef) {
            return ((YapRef) a_object).get();
        }
        return a_object;
    }
    
    public int ver(){
        return 2;
    }

    boolean isCollection(Class a_class) {
        return java.util.Collection.class.isAssignableFrom(a_class)
            || java.util.Map.class.isAssignableFrom(a_class);
    }
    
	void killYapRef(Object obj){
		if(obj instanceof YapRef){
			((YapRef)obj).i_yapObject = null;
		}
	}

    void setAccessible(Object a_accessible) {
        ((java.lang.reflect.AccessibleObject) a_accessible).setAccessible(true);
    }

    void pollReferenceQueue(YapStream a_stream, Object a_referenceQueue) {
        if (a_referenceQueue != null) {
            YapReferenceQueue yrq = (YapReferenceQueue) a_referenceQueue;
            YapRef ref;
            synchronized(a_stream.lock()){
	            while ((ref = yrq.yapPoll()) != null) {
	                a_stream.purge1(ref.i_yapObject);
	            }
            }
        }
    }

}
