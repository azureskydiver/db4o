/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.converters;

import com.db4o.binding.converter.IConverter;

/**
 * ConvertBoolean2String.
 *
 * @author djo
 */
public class ConvertBoolean2String implements IConverter {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.binding.converter.IConverter#convert(java.lang.Object)
	 */
	public Object convert(Object source) {
        Boolean bool = (Boolean) source;
        if (bool.booleanValue()) {
            return "Yes";
        } else {
            return "No";
        }
	}

}
