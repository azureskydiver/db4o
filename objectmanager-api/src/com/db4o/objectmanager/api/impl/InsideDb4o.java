/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.objectmanager.api.impl;

import com.db4o.*;
import com.db4o.inside.btree.*;
import com.db4o.inside.classindex.*;
import com.db4o.reflect.*;

/**
 * This class uses inside-methods of db4o to provide ObjectManager
 * functionality. All functionality used here should go into the
 * public db4o API.
 */
public class InsideDb4o {
	
	private final YapStream _stream;
	
	public InsideDb4o(ObjectContainer oc){
		_stream = (YapStream)oc;
	}

	/**
	 * This should go into StoredClass.
	 */
	public int getNumberOfObjectsForClass(String name) {
		try{
			return index(yapClass(name)).size(trans());
		}catch (Exception e){
			e.printStackTrace();
		}
		return 0;
	}
    
    private YapStream stream(){
    	return _stream;
    }
    
    private YapClass yapClass(String name){
    	return stream().getYapClass(reflectClass(name), false);
    }
    
    private ReflectClass reflectClass(String name){
    	return stream().reflector().forName(name);
    }
    
    private BTree index(YapClass yapClass) throws ClassCastException, NullPointerException{
    	return ((BTreeClassIndexStrategy) yapClass.index()).btree();
    }
    
    private Transaction trans(){
    	return stream().getTransaction();
    }

}
