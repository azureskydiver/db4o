/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.tools.defragment;

import com.db4o.*;

/**
 * Filter for YapClass instances.
 */
public interface YapClassFilter {
	/**
	 * @param yapClass YapClass instance to be checked
	 * @return true, if the given YapClass instance should be accepted, false otherwise.
	 */
	boolean accept(YapClass yapClass);
}
