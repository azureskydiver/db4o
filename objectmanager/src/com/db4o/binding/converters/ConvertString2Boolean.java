/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.converters;

import com.db4o.binding.converter.IConverter;

/**
 * ConvertString2Boolean.
 *
 * @author djo
 */
public class ConvertString2Boolean implements IConverter {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.binding.converter.IConverter#convert(java.lang.Object)
	 */
	public Object convert(Object source) {
        String s = (String) source;
        if (s.equals("Yes") || s.equals("yes") || s.equals("true"))
            return Boolean.TRUE;
        if (s.equals("No") || s.equals("no") || s.equals("false"))
            return Boolean.FALSE;
        
		throw new IllegalArgumentException(s + " is not a legal boolean value");
	}

}
