/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.tools.defragment;

public class DefragmentInfo {
	private String _msg;

	public DefragmentInfo(String msg) {
		_msg = msg;
	}
	
	public String toString() {
		return _msg;
	}
}
