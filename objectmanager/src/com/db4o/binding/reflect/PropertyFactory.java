/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.reflect;


/**
 * PropertyFactory.  A factory object for creating IProperty objects.  This
 * singleton actually delegates to an IPropertyFactory object that can be
 * overridden/set by clients.
 *
 * @author djo
 */
public class PropertyFactory {
	private static IPropertyFactory factory = null;
    
    /* (non-Javadoc)
	 * @see org.eclipse.jface.binding.reflect.IPropertyFactory#construct(java.lang.Object, java.lang.String)
	 */
	public static IProperty construct(Object object, String propertyName) {
        if (factory == null)
            throw new IllegalArgumentException("PropertyFactory.setPropertyFactory(IPropertyFactory) has not been called.");
        
		return factory.construct(object, propertyName);
	}
    
    /**
     * Method setPropertyFactory. Sets the IPropertyFactory to use.
     * 
     * @param factory The IPropertyFactory to use.
     */
    public static void setPropertyFactory(IPropertyFactory factory) {
        PropertyFactory.factory = factory;
    }
}
