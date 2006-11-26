/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.foundation;


/**
 * @exclude
 */
public class IntIdGenerator {
	
	private int _current;
	
	public int next(){
		_current ++;
    	if(_current < 0){
    		_current = 1;
    	}
    	return _current;
	}

}
