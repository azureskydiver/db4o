package com.db4o.replication.hibernate.impl.ref_as_columns;

import java.sql.Types;

public class Db4oColumns {
	/**
	 * Column of the version number of an object.
	 */
	public static final Db4oColumns VERSION = new Db4oColumns("drs_version", Types.BIGINT);

	/**
	 * Column of the db4o uuid long part.
	 */
	public static final Db4oColumns UUID_LONG_PART = new Db4oColumns("drs_uuid_long_part", Types.BIGINT);

	public static final Db4oColumns PROVIDER_ID = new Db4oColumns("drs_provider_id", Types.BIGINT);

	public final String name;

	public final int type;

	private Db4oColumns(String name, int sqlType) {
		this.name = name;
		this.type = sqlType;
	}

	public String toString() {
		System.err.println("you shouldn't call this method. a bug? ");
		return name;
	}
}
