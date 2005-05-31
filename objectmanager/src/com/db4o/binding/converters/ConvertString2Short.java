/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.converters;

import com.db4o.binding.converter.IConverter;

/**
 * ConvertString2Short.
 *
 * @author djo
 */
public class ConvertString2Short implements IConverter {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.binding.converter.IConverter#convert(java.lang.Object)
	 */
	public Object convert(Object source) {
        try {
            return new Short(Short.parseShort((String) source));
        } catch (Exception e) {
            throw new IllegalArgumentException("String2Short: " + e.getMessage() + ": " + source);
        }
	}

}
