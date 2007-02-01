/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.inside.*;

class JavaOnly {
    
	static final int collectionUpdateDepth(Class a_class) 
	{
		return 0;
	}
    
	static final boolean isCollection(Class a_class) 
	{
		return false;
	}
    
	static final boolean isCollectionTranslator(Config4Class a_config) 
	{
		return false;
	}
    
	public static JDK jdk() 
	{
		return new JDK();
	}
    
	public static void link()
	{
	}
    
	public static void runFinalizersOnExit()
	{
	}
    
	static final Class[] SIMPLE_CLASSES = null;
}
