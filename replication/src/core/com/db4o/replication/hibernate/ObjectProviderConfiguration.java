package com.db4o.replication.hibernate;

import org.hibernate.cfg.Configuration;

public class ObjectProviderConfiguration {
	Configuration cfg;

	public ObjectProviderConfiguration(Configuration cfg) {
		this.cfg = cfg;
	}
}
