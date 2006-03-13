package com.db4o.replication.hibernate.metadata;

import com.db4o.replication.hibernate.impl.Constants;

public class UuidLongPartSequence {
	public static final String TABLE_NAME = "uuid_long_part_sequence";

	private long current;

	public UuidLongPartSequence() {
		current = Constants.MIN_SEQ_NO;
	}

	public long getCurrent() {
		return current;
	}

	public void setCurrent(long current) {
		this.current = current;
	}

	public void increment() {
		current++;
	}
}
