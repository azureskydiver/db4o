/* Copyright (C) 2008   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.jre5.collections.typehandler;

public class FirstClassElement {

	public int _id;
	
	public FirstClassElement(int id) {
		_id = id;
	}
	
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if(obj == null || getClass() != obj.getClass()) {
			return false;
		}
		FirstClassElement other = (FirstClassElement) obj;
		return _id == other._id;
	}
	
	public int hashCode() {
		return _id;
	}
	
	public String toString() {
		return "FCE#" + _id;
	}

}
