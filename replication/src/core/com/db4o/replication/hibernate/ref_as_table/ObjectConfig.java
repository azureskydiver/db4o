package com.db4o.replication.hibernate.ref_as_table;

import org.hibernate.cfg.Configuration;

public class ObjectConfig {
	Configuration cfg;

	public ObjectConfig(Configuration cfg) {
		this.cfg = cfg;
	}

	public Configuration getCfg() {
		return cfg;
	}
}
