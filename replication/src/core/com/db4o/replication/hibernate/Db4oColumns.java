package com.db4o.replication.hibernate;

import java.sql.Types;

class Db4oColumns {
	/**
	 * Column of the version number of an object.
	 */
	static final Db4oColumns VERSION = new Db4oColumns("drs_version", Types.BIGINT);

	/**
	 * Column of the db4o uuid long part.
	 */
	static final Db4oColumns UUID_LONG_PART = new Db4oColumns("drs_uuid_long_part", Types.BIGINT);

	final String name;

	final int sqlType;

	Db4oColumns(String name, int sqlType) {
		this.name = name;
		this.sqlType = sqlType;
	}

	public String toString() {
		System.err.println("you shouldn't call this method. a bug? ");
		return name;
	}
}
