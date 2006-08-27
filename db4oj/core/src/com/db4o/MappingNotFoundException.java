/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o;

/**
 * @exclude
 */
public class MappingNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -1771324770287654802L;
	
	private int _id;
	
	public MappingNotFoundException(int id) {
		super("Mapping not found for "+id);
		this._id = id;
	}

	public int id() {
		return _id;
	}
}
