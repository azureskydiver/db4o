package com.db4o.ext;

public class LeanStoredField {
	private String name;
	private int typeID;
	private boolean primitive;
	
	public LeanStoredField(String name, int typeID,boolean primitive) {
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
		LeanStoredField other=(LeanStoredField)obj;
		return name.equals(other.name)&&typeID==other.typeID;
	}

	public int hashCode() {
		return name.hashCode()*29+typeID;
	}
	
	public String toString() {
		return name+"("+typeID+")";
	}
}
