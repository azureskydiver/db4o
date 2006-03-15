package com.db4o.replication.hibernate.metadata;

import org.apache.commons.lang.builder.ToStringBuilder;

public class DeletedObject {
	public static final String TABLE_NAME = "DeletedObject";
	public static final String UUID = "uuid";

	Uuid uuid;

	public DeletedObject() {
	}

	public Uuid getUuid() {
		return uuid;
	}

	public void setUuid(Uuid uuid) {
		this.uuid = uuid;
	}

	public String toString() {
		return new ToStringBuilder(this).
				append(UUID, uuid).
				toString();
	}
}
