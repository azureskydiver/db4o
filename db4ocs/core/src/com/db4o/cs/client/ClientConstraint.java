package com.db4o.cs.client;

import com.db4o.query.Constraint;

import java.io.Serializable;

/**
 * User: treeder
 * Date: Nov 25, 2006
 * Time: 4:19:11 PM
 */
public class ClientConstraint implements Constraint, Serializable {
	private Object objectToConstrain;

	public ClientConstraint(Object constraint) {
		this.objectToConstrain = constraint;
	}

	public Constraint and(Constraint with) {
		return null;
	}

	public Constraint or(Constraint with) {
		return null;
	}

	public Constraint equal() {
		return null;
	}

	public Constraint greater() {
		return null;
	}

	public Constraint smaller() {
		return null;
	}

	public Constraint identity() {
		return null;
	}

	public Constraint like() {
		return null;
	}

	public Constraint contains() {
		return null;
	}

	public Constraint startsWith(boolean caseSensitive) {
		return null;
	}

	public Constraint endsWith(boolean caseSensitive) {
		return null;
	}

	public Constraint not() {
		return null;
	}

	public Object getObject() {
		return objectToConstrain;
	}
}
