package com.db4o.replication.hibernate;

import com.db4o.foundation.Visitor4;
import com.db4o.replication.hibernate.common.Common;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ObjectConfig {
	Configuration configuration;

	private Set tables;

	public ObjectConfig(Configuration cfg) {
		this.configuration = cfg;
	}

	public Configuration getConfiguration() {
		return configuration;
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

	public void visitMappedTables(Visitor4 visitor) {
		final Set mappedTables = getMappedTables();
		for (Iterator iterator = mappedTables.iterator(); iterator.hasNext();)
			visitor.visit(iterator.next());
	}
}
