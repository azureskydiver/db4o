/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.tools.defragment;

/**
 * A message from the defragmentation process. This is a stub only
 * and will be refined.
 * 
 * Currently instances of these class will only be created and sent
 * to registered listeners when invalid IDs are encountered during
 * the defragmentation process. These probably are harmless and the
 * result of a user-initiated delete operation.
 */
public class DefragmentInfo {
	private String _msg;

	public DefragmentInfo(String msg) {
		_msg = msg;
	}
	
	public String toString() {
		return _msg;
	}
}
