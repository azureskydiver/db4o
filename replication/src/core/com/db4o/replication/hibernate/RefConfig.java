package com.db4o.replication.hibernate;

import com.db4o.replication.hibernate.common.ReplicationComponentField;
import com.db4o.replication.hibernate.common.ReplicationComponentIdentity;
import com.db4o.replication.hibernate.common.ReplicationProviderSignature;
import com.db4o.replication.hibernate.common.ReplicationRecord;
import com.db4o.replication.hibernate.common.UuidLongPartSequence;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;

public class RefConfig {
	protected Configuration configuration;
	protected Dialect dialect;

	protected void init() {
		configuration.setProperty("hibernate.format_sql", "true");
		configuration.setProperty("hibernate.use_sql_comments", "true");
		configuration.setProperty("hibernate.cache.provider_class", "org.hibernate.cache.NoCacheProvider");
		configuration.setProperty("hibernate.cache.use_query_cache", "false");
		configuration.setProperty("hibernate.cache.use_second_level_cache", "false");
		configuration.setProperty("hibernate.cglib.use_reflection_optimizer", "true");
		configuration.setProperty("hibernate.connection.release_mode", "after_transaction");

		addClasses();
		dialect = Dialect.getDialect(configuration.getProperties());
	}

	protected void addClasses() {
		addClass(ReplicationProviderSignature.class);
		addClass(ReplicationRecord.class);
		addClass(ReplicationComponentIdentity.class);
		addClass(ReplicationComponentField.class);
		addClass(UuidLongPartSequence.class);
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	protected void addClass(Class aClass) {
		if (configuration.getClassMapping(aClass.getName()) == null)
			configuration.addClass(aClass);
	}

	public Dialect getDialect() {
		return dialect;
	}

	public String getType(int sqlType) {
		return dialect.getTypeName(sqlType);
	}
}
