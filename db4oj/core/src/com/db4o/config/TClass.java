/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.config;

import com.db4o.ObjectContainer;

public class TClass implements ObjectConstructor
{
	public Object onStore(ObjectContainer con, Object object){
		return ((Class)object).getName();
	}
	
	public void onActivate(ObjectContainer con, Object object, Object members){
		// do nothing
	}

	public Object onInstantiate(ObjectContainer con, Object storedObject){
		try{
			// return Db4o.classForName((String)storedObject);
		    return Class.forName((String)storedObject);
		}catch(Exception e){}
		return null;
	}

	public Class storedClass(){
		return String.class;
	}
}
