/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.marshall;

import com.db4o.*;

/**
 * common functionality for {@link ReadContext} and 
 * {@link WriteContext}
 */
public interface Context {
	
    ObjectContainer objectContainer();

}
