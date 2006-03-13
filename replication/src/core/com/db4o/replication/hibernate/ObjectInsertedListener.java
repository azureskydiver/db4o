package com.db4o.replication.hibernate;

import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.PostInsertEventListener;

public interface ObjectInsertedListener extends PostInsertEventListener {
	void configure(Configuration cfg);

	void install(Session session, Configuration cfg);
}
