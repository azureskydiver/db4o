package com.db4o.replication.hibernate.metadata;

import com.db4o.replication.hibernate.impl.Constants;

public class UuidLongPartSequence {
// ------------------------------ FIELDS ------------------------------

	public static final String TABLE_NAME = "UuidLongPartSequence";

	private long current;

	public long getCurrent() {
		return current;
	}

	public void setCurrent(long current) {
		this.current = current;
	}

// --------------------------- CONSTRUCTORS ---------------------------

	public UuidLongPartSequence() {
		current = Constants.MIN_SEQ_NO;
	}

// ------------------------ CANONICAL METHODS ------------------------

	public String toString() {
		return "UuidLongPartSequence{" +
				"current=" + current +
				'}';
	}

	public void increment() {
		current++;
	}
}
