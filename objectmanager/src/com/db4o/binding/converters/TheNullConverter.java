/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.converters;

import com.db4o.binding.converter.IConverter;

/**
 * TheIdentityConverter.  Returns the source value (the identity function).
 *
 * @author djo
 */
public class TheNullConverter implements IConverter {

	public static final IConverter NULL = new TheNullConverter();

	/* (non-Javadoc)
	 * @see org.eclipse.jface.binding.converter.IConverter#convert(java.lang.Object)
	 */
	public Object convert(Object source) {
        return "";
	}

}
