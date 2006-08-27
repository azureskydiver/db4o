package com.db4o;

public class MappingNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -1771324770287654802L;
	
	private int id;
	
	public MappingNotFoundException(int id_) {
		id = id_;
	}

	public int id() {
		return id;
	}
}
