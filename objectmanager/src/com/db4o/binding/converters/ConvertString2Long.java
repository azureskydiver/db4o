/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.converters;

import com.db4o.binding.converter.IConverter;

/**
 * ConvertString2Long.
 *
 * @author djo
 */
public class ConvertString2Long implements IConverter {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.binding.converter.IConverter#convert(java.lang.Object)
	 */
	public Object convert(Object source) {
        try {
            return new Long(Long.parseLong((String) source));
        } catch (Exception e) {
            throw new IllegalArgumentException("String2Long: " + e.getMessage() + ": " + source);
        }
	}

}
