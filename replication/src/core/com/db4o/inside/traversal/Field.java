package com.db4o.inside.traversal;

public class Field {
	private Object owner;

	private String name;
	private Object field;

	public Field(Object owner, String name, Object field) {
		if (owner == null) throw new IllegalArgumentException("owner cannot be null");
		if (name == null) throw new IllegalArgumentException("name cannot be null");
		if (field == null) throw new IllegalArgumentException("field be null");

		this.owner = owner;
		this.name = name;
		this.field = field;
	}

	public Object getOwner() {
		return owner;
	}

	public String getName() {
		return name;
	}

	public Object getField() {
		return field;
	}

	public String toString() {
		return "owner =" + owner + ", name = " + name + ", field = " + field;
	}
}
