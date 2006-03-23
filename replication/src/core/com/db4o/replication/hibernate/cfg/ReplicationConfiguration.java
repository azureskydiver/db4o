package com.db4o.replication.hibernate.cfg;

import com.db4o.foundation.Visitor4;
import com.db4o.replication.hibernate.impl.Util;
import com.db4o.replication.hibernate.metadata.DeletedObject;
import com.db4o.replication.hibernate.metadata.ObjectReference;
import com.db4o.replication.hibernate.metadata.ReplicationComponentField;
import com.db4o.replication.hibernate.metadata.ReplicationComponentIdentity;
import com.db4o.replication.hibernate.metadata.ReplicationProviderSignature;
import com.db4o.replication.hibernate.metadata.ReplicationRecord;
import com.db4o.replication.hibernate.metadata.UuidLongPartSequence;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class ReplicationConfiguration {
// ------------------------------ FIELDS ------------------------------

	private final Configuration configuration;

	public final Configuration getConfiguration() {
		return configuration;
	}

	private Set tables;

	private final Dialect dialect;

	public final Dialect getDialect() {
		return dialect;
	}

// --------------------------- CONSTRUCTORS ---------------------------

	public ReplicationConfiguration(Configuration configuration) {
		this.configuration = configuration;

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

	private void addClasses() {
		Util.addClass(configuration, ReplicationProviderSignature.class);
		Util.addClass(configuration, ReplicationRecord.class);
		Util.addClass(configuration, ReplicationComponentIdentity.class);
		Util.addClass(configuration, ReplicationComponentField.class);
		Util.addClass(configuration, UuidLongPartSequence.class);
		Util.addClass(configuration, DeletedObject.class);
		Util.addClass(configuration, ObjectReference.class);
	}

	private Set getMappedTables() {
		if (tables == null) {
			tables = new HashSet();
			Iterator tableMappings = configuration.getTableMappings();

			while (tableMappings.hasNext()) {
				Table table = (Table) tableMappings.next();

				if (Util.skip(table))
					continue;
				tables.add(table);
			}
		}

		return tables;
	}

	public final String getPrimaryKeyColumnName(Object entity) {
		final String className = entity.getClass().getName();
		final PersistentClass pClass = configuration.getClassMapping(className);

		return getPrimaryKeyColumnName(pClass);
	}

	public final String getPrimaryKeyColumnName(Class claxx) {
		return getPrimaryKeyColumnName(configuration.getClassMapping(claxx.getName()));
	}

	public final String getPrimaryKeyColumnName(PersistentClass pClass) {
		PrimaryKey primaryKey = pClass.getTable().getPrimaryKey();
		Iterator columnIterator = primaryKey.getColumnIterator();

		String pkColName;

		pkColName = ((Column) columnIterator.next()).getName();
		if (columnIterator.hasNext()) {
			throw new RuntimeException("we don't support composite primary keys");
		}

		return pkColName;
	}

	public final String getTableName(Class pClass) {
		PersistentClass mapped = configuration.getClassMapping(pClass.getName());
		if (mapped == null)
			throw new RuntimeException(pClass + " is not mapped using a hbm.xml file.");
		return mapped.getTable().getName();
	}

	public final String getType(int sqlType) {
		return dialect.getTypeName(sqlType);
	}

	public final void visitMappedTables(Visitor4 visitor) {
		final Set mappedTables = getMappedTables();
		for (Iterator iterator = mappedTables.iterator(); iterator.hasNext();)
			visitor.visit(iterator.next());
	}
}
