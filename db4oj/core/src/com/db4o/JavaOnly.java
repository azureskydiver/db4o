/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.util.*;

import com.db4o.config.*;

class JavaOnly {
    
    static final int collectionUpdateDepth(Class a_class) {
        if (Platform.hasCollections()) {
            return Platform.jdk().collectionUpdateDepth(a_class);
        }
        return Hashtable.class.isAssignableFrom(a_class) ? 3 : 2;
    }
    
    static final boolean isCollection(Class a_class) {
        if(P1Collection.class.isAssignableFrom(a_class)){
            return true;
        }
        if (Platform.hasCollections()) {
            return Platform.jdk().isCollection(a_class);
        }
        return java.util.Vector.class.isAssignableFrom(a_class)
            || java.util.Hashtable.class.isAssignableFrom(a_class);
        // TODO: Need to implement Vector in forEachCollectionElement first
    }
    
    static final boolean isCollectionTranslator(Config4Class a_config) {
        if (a_config != null) {
            ObjectTranslator ot = a_config.getTranslator();
            if (ot != null) {
                return ot instanceof TCollection || ot instanceof TMap || ot instanceof THashtable;
            }
        }
        return false;
    }
    
    public static void link(){
        Object obj = new TClass();
        obj = new TVector();
        obj = new THashtable();
        obj = new TNull();
    }
    
    public static void runFinalizersOnExit(){
    	try{
			System.class.getMethod("runFinalizersOnExit", new Class[] {boolean.class}).
			invoke(null, new Object[]{new Boolean(true)});
    	}catch(Throwable t){
    	}
    }
    
	static final Class[] SIMPLE_CLASSES ={
		Integer.class,
		Long.class,
		Float.class,
		Boolean.class,
		Double.class,
		Byte.class,
		Character.class,
		Short.class,
		String.class,
		java.util.Date.class
	};


}
