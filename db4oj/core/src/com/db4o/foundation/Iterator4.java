/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.foundation;

/**
 * @sharpen.ignore
 */
public interface Iterator4 {

	public boolean moveNext();

	/**
	 * @sharpen.property
	 */
	public Object current();

	public void reset();
}
