/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.io;

public class FieldReader {
	private String name;
	private int typeID;
	private boolean primitive;
	
	public FieldReader(String name, int typeID,boolean primitive) {
		this.name = name;
		this.typeID = typeID;
		this.primitive=primitive;
	}

	public String name() {
		return name;
	}

	public int typeID() {
		return typeID;
	}

	public boolean isPrimitive() {
		return primitive;
	}
	
	public boolean equals(Object obj) {
		if(this==obj) {
			return true;
		}
		if(obj==null||getClass()!=obj.getClass()) {
			return false;
		}
		FieldReader other=(FieldReader)obj;
		return name.equals(other.name)&&typeID==other.typeID;
	}

	public int hashCode() {
		return name.hashCode()*29+typeID;
	}
	
	public String toString() {
		return name+"("+typeID+")";
	}
}
