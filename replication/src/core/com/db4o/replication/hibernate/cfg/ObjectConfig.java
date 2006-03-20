package com.db4o.replication.hibernate.cfg;

import com.db4o.foundation.Visitor4;
import com.db4o.replication.hibernate.impl.Util;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ObjectConfig {
// ------------------------------ FIELDS ------------------------------

	Configuration configuration;

	public Configuration getConfiguration() {
		return configuration;
	}

	private Set tables;

// --------------------------- CONSTRUCTORS ---------------------------

	public ObjectConfig(Configuration cfg) {
		this.configuration = cfg;
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

				if (Util.skip(table))
					continue;
				tables.add(table);
			}
		}

		return tables;
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

	public String getPrimaryKeyColumnName(Class claxx) {
		return getPrimaryKeyColumnName(configuration.getClassMapping(claxx.getName()));
	}

	public String getPrimaryKeyColumnName(Object entity) {
		final String className = entity.getClass().getName();
		final PersistentClass pClass = configuration.getClassMapping(className);

		return getPrimaryKeyColumnName(pClass);
	}

	public String getTableName(Class pClass) {
		PersistentClass mapped = configuration.getClassMapping(pClass.getName());
		if (mapped == null)
			throw new RuntimeException(pClass + " is not mapped using a hbm.xml file.");
		return mapped.getTable().getName();
	}

	public void visitMappedTables(Visitor4 visitor) {
		final Set mappedTables = getMappedTables();
		for (Iterator iterator = mappedTables.iterator(); iterator.hasNext();)
			visitor.visit(iterator.next());
	}
}
