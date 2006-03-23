package com.db4o.replication.hibernate.cfg;

import com.db4o.replication.hibernate.impl.Util;
import com.db4o.replication.hibernate.metadata.ObjectReference;
import com.db4o.replication.hibernate.metadata.ReplicationComponentField;
import com.db4o.replication.hibernate.metadata.ReplicationComponentIdentity;
import com.db4o.replication.hibernate.metadata.ReplicationProviderSignature;
import com.db4o.replication.hibernate.metadata.ReplicationRecord;
import com.db4o.replication.hibernate.metadata.UuidLongPartSequence;
import org.hibernate.cfg.Configuration;

public final class ReplicationConfiguration {
// -------------------------- STATIC METHODS --------------------------

	private static void addClasses(Configuration configuration) {
		Util.addClass(configuration, ReplicationProviderSignature.class);
		Util.addClass(configuration, ReplicationRecord.class);
		Util.addClass(configuration, ReplicationComponentIdentity.class);
		Util.addClass(configuration, ReplicationComponentField.class);
		Util.addClass(configuration, UuidLongPartSequence.class);
		Util.addClass(configuration, ObjectReference.class);
	}

	public static Configuration decorate(Configuration configuration) {
		configuration.setProperty("hibernate.format_sql", "false");
		configuration.setProperty("hibernate.use_sql_comments", "false");
		configuration.setProperty("hibernate.cache.provider_class", "org.hibernate.cache.NoCacheProvider");
		configuration.setProperty("hibernate.cache.use_query_cache", "false");
		configuration.setProperty("hibernate.cache.use_second_level_cache", "false");
		configuration.setProperty("hibernate.cglib.use_reflection_optimizer", "true");
		configuration.setProperty("hibernate.connection.release_mode", "after_transaction");

		addClasses(configuration);

		return configuration;
	}
}
