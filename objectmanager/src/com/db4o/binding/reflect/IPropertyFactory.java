/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.reflect;

/**
 * IPropertyFactory.  An interface for objects that can create instances of
 * type IProperty.
 *
 * @author djo
 */
public interface IPropertyFactory {
	/**
     * Constructs an IProperty for the specified property name on the 
     * specified object.
     * 
	 * @param object The object on which the property exists.
	 * @param propertyName The name of the property.
	 * @return IProperty an IProperty object that can manipulate this property
	 */
	public IProperty construct(Object object, String propertyName);
}
