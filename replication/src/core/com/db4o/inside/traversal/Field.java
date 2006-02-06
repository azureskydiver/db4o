package com.db4o.inside.traversal;

import java.util.Collection;

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

	public boolean isCollection() {
		return field instanceof Collection;
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
}
