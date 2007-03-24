/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.selpersist;

import com.db4o.types.TransientClass;


public class NotStorable implements TransientClass {

	public String toString(){
		return "NotStorable class";
	} 
}
