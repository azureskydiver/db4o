package com.db4o.ext;

public class LeanStoredClass {
	private int id;
	private String name;
	private int parentID;
	private LeanStoredField[] fields;
	
	public LeanStoredClass(int id,String name, int parentID, LeanStoredField[] fields) {
		this.id=id;
		this.name = name;
		this.parentID = parentID;
		this.fields = fields;
	}
	
	public int getId() {
		return id;
	}

	public String name() {
		return name;
	}

	public int parentID() {
		return parentID;
	}

	public LeanStoredField[] storedFields() {
		return fields;
	}

	public LeanStoredField storedField(String name, Object type) {
		for (int idx = 0; idx < fields.length; idx++) {
			if(name.equals(fields[idx].name())) {
				return fields[idx];
			}
		}
		return null;
	}
	
	public boolean equals(Object obj) {
		if(this==obj) {
			return true;
		}
		if(obj==null||getClass()!=obj.getClass()) {
			return false;
		}
		LeanStoredClass other=(LeanStoredClass)obj;
		return id==other.id;
	}
	
	public int hashCode() {
		return id;
	}
	
	public String toString() {
		StringBuffer str=new StringBuffer()
			.append(id)
			.append(": ")
			.append(name)
			.append('(')
			.append(parentID)
			.append(')')
			.append('\n');
		for (int idx = 0; idx < fields.length; idx++) {
			str.append("- ")
				.append(fields[idx])
				.append('\n');
		}
		return str.toString();
	}
}