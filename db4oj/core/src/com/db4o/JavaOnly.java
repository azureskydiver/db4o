/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.util.*;

import com.db4o.config.*;

class JavaOnly {
    
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
