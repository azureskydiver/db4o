package com.db4o.replication.hibernate;

import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.PostDeleteEventListener;

public interface ObjectDeletedListener extends PostDeleteEventListener {
	void configure(Configuration cfg);

	void install(Session session, Configuration cfg);
}
