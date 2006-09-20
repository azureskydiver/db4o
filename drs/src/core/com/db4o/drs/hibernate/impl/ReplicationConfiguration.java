package com.db4o.drs.hibernate.impl;

import org.hibernate.cfg.Configuration;

public final class ReplicationConfiguration {
	public static Configuration decorate(Configuration c) {
		for (Class cl : Util._metadataClasses)
			Util.addClass(c, cl);
		return c;
	}
}
