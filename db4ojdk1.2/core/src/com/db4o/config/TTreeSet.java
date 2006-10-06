/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.config;

import java.util.*;

import com.db4o.*;

/**
 * @exclude
 * @sharpen.ignore
 */
public class TTreeSet implements ObjectConstructor {
	
	public Object onStore(ObjectContainer con, Object object){
		return ((TreeSet)object).comparator();
	}

	public void onActivate(ObjectContainer con, Object object, Object members){
		// do nothing
	}

	public Object onInstantiate(ObjectContainer container, Object storedObject){
		try{
			Comparator comp = (Comparator)storedObject;
			if(comp != null){
				return new TreeSet(comp);
			}
		}catch(Exception e){}
		return new TreeSet();
	}

	public Class storedClass(){
		return Object.class;
	}
}
