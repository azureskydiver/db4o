package com.db4o.replication.hibernate;

public class DefaultObjectProvider implements ObjectProvider {
	ObjectProviderConfiguration cfg;

	public DefaultObjectProvider(ObjectProviderConfiguration cfg) {
		this.cfg = cfg;
	}
}
