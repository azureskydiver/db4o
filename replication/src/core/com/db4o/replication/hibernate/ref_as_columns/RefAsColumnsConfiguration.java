package com.db4o.replication.hibernate.ref_as_columns;

import com.db4o.foundation.Visitor4;
import com.db4o.replication.hibernate.RefConfig;
import com.db4o.replication.hibernate.common.Common;
import com.db4o.replication.hibernate.common.ReplicationComponentField;
import com.db4o.replication.hibernate.common.ReplicationComponentIdentity;
import com.db4o.replication.hibernate.common.ReplicationProviderSignature;
import com.db4o.replication.hibernate.common.ReplicationRecord;
import com.db4o.replication.hibernate.common.UuidLongPartSequence;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class RefAsColumnsConfiguration implements RefConfig {
	private static HashMap<Configuration, RefAsColumnsConfiguration> cache = new HashMap<Configuration, RefAsColumnsConfiguration>();

	private Configuration configuration;

	private Set tables;

	private final Dialect dialect;

	public static RefAsColumnsConfiguration produce(Configuration cfg) {
		Object exist = cache.get(cfg);
		if (exist != null)
			return (RefAsColumnsConfiguration) exist;

		RefAsColumnsConfiguration rc = new RefAsColumnsConfiguration(cfg);
		cache.put(cfg, rc);
		return rc;
	}

	private RefAsColumnsConfiguration(Configuration aCfg) {
		configuration = aCfg;
		configuration.setProperty("hibernate.format_sql", "true");
		configuration.setProperty("hibernate.use_sql_comments", "true");
		configuration.setProperty("hibernate.cache.provider_class", "org.hibernate.cache.NoCacheProvider");
		configuration.setProperty("hibernate.cache.use_query_cache", "false");
		configuration.setProperty("hibernate.cache.use_second_level_cache", "false");
		configuration.setProperty("hibernate.cglib.use_reflection_optimizer", "true");
		configuration.setProperty("hibernate.connection.release_mode", "after_transaction");

		addClass(ReplicationProviderSignature.class);
		addClass(ReplicationRecord.class);
		addClass(ReplicationComponentIdentity.class);
		addClass(ReplicationComponentField.class);
		addClass(UuidLongPartSequence.class);
		dialect = Dialect.getDialect(configuration.getProperties());
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	private void addClass(Class aClass) {
		if (configuration.getClassMapping(aClass.getName()) == null)
			configuration.addClass(aClass);
	}

	public String getPrimaryKeyColumnName(Object entity) {
		final String className = entity.getClass().getName();
		final PersistentClass pClass = configuration.getClassMapping(className);

		return getPrimaryKeyColumnName(pClass);
	}

	public String getPrimaryKeyColumnName(PersistentClass pClass) {
		PrimaryKey primaryKey = pClass.getTable().getPrimaryKey();
		Iterator columnIterator = primaryKey.getColumnIterator();

		String pkColName;

		pkColName = ((Column) columnIterator.next()).getName();
		if (columnIterator.hasNext()) {
			throw new RuntimeException("we don't support composite primary keys");
		}

		return pkColName;
	}

	public String getTableName(Class pClass) {
		PersistentClass mapped = configuration.getClassMapping(pClass.getName());
		if (mapped == null)
			throw new RuntimeException(pClass + " is not mapped using a hbm.xml file.");
		return mapped.getTable().getName();
	}

	/**
	 * Return entity tables without meta data tables.
	 *
	 * @return entity tables
	 */
	Set getMappedTables() {
		if (tables == null) {
			tables = new HashSet();
			Iterator tableMappings = configuration.getTableMappings();

			while (tableMappings.hasNext()) {
				Table table = (Table) tableMappings.next();

				if (Common.skip(table))
					continue;
				tables.add(table);
			}
		}

		return tables;
	}

	public Dialect getDialect() {
		return dialect;
	}

	public void visitMappedTables(Visitor4 visitor) {
		final Set mappedTables = getMappedTables();
		for (Iterator iterator = mappedTables.iterator(); iterator.hasNext();)
			visitor.visit(iterator.next());
	}

	public String getType(int sqlType) {
		return dialect.getTypeName(sqlType);
	}
}
