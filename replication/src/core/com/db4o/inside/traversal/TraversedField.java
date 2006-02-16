package com.db4o.inside.traversal;

public class TraversedField {
	private Object referencingObject;

	private String name;
	private Object value;

	public TraversedField(Object referencingObject, String name, Object value) {
		if (referencingObject == null)
			throw new IllegalArgumentException("referencingObject cannot be null");
		if (name == null)
			throw new IllegalArgumentException("name cannot be null");
		if (value == null)
			throw new IllegalArgumentException("value be null");

		this.referencingObject = referencingObject;
		this.name = name;
		this.value = value;
	}

	public Object getReferencingObject() {
		return referencingObject;
	}

	public String getName() {
		return name;
	}

	public Object getValue() {
		return value;
	}

	public String toString() {
		return "referencingObject =" + referencingObject + ", name = " + name + ", value = " + value;
	}
}
