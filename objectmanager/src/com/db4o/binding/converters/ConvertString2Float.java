/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.converters;

import com.db4o.binding.converter.IConverter;

/**
 * ConvertString2Float.
 *
 * @author djo
 */
public class ConvertString2Float implements IConverter {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.binding.converter.IConverter#convert(java.lang.Object)
	 */
	public Object convert(Object source) {
        try {
            return new Float(Float.parseFloat((String) source));
        } catch (Exception e) {
            throw new IllegalArgumentException("String2Float: " + e.getMessage() + ": " + source);
        }
	}

}
