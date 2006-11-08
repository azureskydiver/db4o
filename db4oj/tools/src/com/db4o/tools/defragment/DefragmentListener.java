/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.tools.defragment;

public interface DefragmentListener {
	/**
	 * This method will be called when the defragment process encounters an invalid
	 * ID. This probably just indicates a 'dangling reference'
	 */
	void notifyDefragmentInfo(DefragmentInfo info);
}
