/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.converters;

import com.db4o.binding.converter.IConverter;

/**
 * ConvertString2Character.
 *
 * @author djo
 */
public class ConvertString2Character implements IConverter {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.binding.converter.IConverter#convert(java.lang.Object)
	 */
	public Object convert(Object source) {
        String s = (String) source;
        Character result;
        
        if (s.length() > 1)
            throw new IllegalArgumentException("String2Character: string too long: " + s);
        
        try {
            result = new Character(s.charAt(0));
        } catch (Exception e) {
            throw new IllegalArgumentException("String2Character: " + e.getMessage() + ": " + s);
        }
        
		return result;
	}

}
