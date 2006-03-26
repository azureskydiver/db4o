package com.db4o.replication.hibernate.cfg;

import com.db4o.replication.hibernate.impl.Util;
import org.hibernate.cfg.Configuration;

public final class ReplicationConfiguration {
// -------------------------- STATIC METHODS --------------------------

	public static Configuration decorate(Configuration c) {
		for (Class cl : Util.metadataClasses)
			Util.addClass(c, cl);
		return c;
	}
}
