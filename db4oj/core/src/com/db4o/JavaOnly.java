/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.config.*;

/**
 * @sharpen.ignore
 */
class JavaOnly {
    
    public static void link(){
        new TClass();
        new TVector();
        new THashtable();
        new TNull();
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
