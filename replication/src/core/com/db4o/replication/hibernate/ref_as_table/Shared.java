package com.db4o.replication.hibernate.ref_as_table;

import java.io.Serializable;

public class Shared {
	public static void ensureLong(Serializable id) {
		if (!(id instanceof Long))
			throw new IllegalStateException("You must use 'long' as the type of the hibernate id");
	}

	public static long castAsLong(Serializable id) {
		ensureLong(id);
		return ((Long) id).longValue();
	}
}
