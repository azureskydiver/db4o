/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */
package com.db4o.ext;

import com.db4o.*;
import com.db4o.inside.*;

/**
 * 
 * intended for future virtual fields on classes. Currently only
 * the constant for the virtual version field is found here.  
 *
 * @exclude
 */
public class VirtualField {
	
	/**
	 * the field name of the virtual version field, to be used
	 * for querying.
	 */
	public static final String VERSION = Const4.VIRTUAL_FIELD_PREFIX + "version"; 

}
