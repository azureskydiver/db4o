/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.foundation;

/**
 * @exclude
 */
public final class ArgumentNullException extends IllegalArgumentException {
    
    public ArgumentNullException(){
        super();
    }

	public ArgumentNullException(final String name) {
		super(name);
	}
}
