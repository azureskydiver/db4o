package com.db4o.replication.hibernate.metadata;

import org.apache.commons.lang.builder.ToStringBuilder;

public class DeletedObject {
// ------------------------------ FIELDS ------------------------------

	public static final String TABLE_NAME = "DeletedObject";
	public static final String UUID = "uuid";

	Uuid uuid;

	public Uuid getUuid() {
		return uuid;
	}

	public void setUuid(Uuid uuid) {
		this.uuid = uuid;
	}

// --------------------------- CONSTRUCTORS ---------------------------

	public DeletedObject() {
	}

// ------------------------ CANONICAL METHODS ------------------------

	public String toString() {
		return new ToStringBuilder(this).
				append(UUID, uuid).
				toString();
	}
}
