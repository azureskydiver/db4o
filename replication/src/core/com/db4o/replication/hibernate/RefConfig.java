package com.db4o.replication.hibernate;

import com.db4o.foundation.Visitor4;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.PersistentClass;

public interface RefConfig {
	Configuration getConfiguration();

	String getPrimaryKeyColumnName(Object entity);

	String getPrimaryKeyColumnName(PersistentClass pClass);

	String getTableName(Class pClass);

	Dialect getDialect();

	void visitMappedTables(Visitor4 visitor);

	String getType(int sqlType);
}
