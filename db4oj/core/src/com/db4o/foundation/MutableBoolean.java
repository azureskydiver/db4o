/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.foundation;


/**
 * @exclude
 */
public class MutableBoolean {
	
	private boolean _value;
	
	public boolean isTrue(){
		return _value;
	}
	
	public void setTrue(){
		_value = true;
	}

}
