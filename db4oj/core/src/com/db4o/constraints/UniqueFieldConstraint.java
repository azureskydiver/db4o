/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.constraints;

import com.db4o.config.*;
import com.db4o.internal.*;


/**
 * configures a field of a class to allow unique values only.
 */
public class UniqueFieldConstraint implements ConfigurationItem {
	
	private final Object _clazz;
	
	private final String _fieldName;
	
	/**
	 * constructor to create a UniqueFieldConstraint. 
	 * @param clazz can be a class (Java) / Type (.NET) / instance of the class / fully qualified class name
	 * @param fieldName the name of the field that is to be unique. 
	 */
	public UniqueFieldConstraint(Object clazz, String fieldName){
		_clazz = clazz;
		_fieldName = fieldName;
	}
	
	/**
	 * internal method, public for implementation reasons.
	 */
	public void apply(ObjectContainerBase objectContainer) {
		
		
	}
	
	
}
