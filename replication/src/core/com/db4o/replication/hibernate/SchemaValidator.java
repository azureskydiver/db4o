package com.db4o.replication.hibernate;

import com.db4o.foundation.Visitor4;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.hibernate.mapping.Table;
import org.hibernate.tool.hbm2ddl.ColumnMetadata;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.hibernate.tool.hbm2ddl.TableMetadata;

import java.sql.Connection;
import java.sql.SQLException;

public class SchemaValidator {
	private ReplicationConfiguration cfg;

	protected org.hibernate.tool.hbm2ddl.SchemaValidator delegate;
	protected SessionFactory sessionFactory;

	public SchemaValidator(ReplicationConfiguration aCfg) {
		cfg = aCfg;

		delegate = new org.hibernate.tool.hbm2ddl.SchemaValidator(cfg.getConfiguration());

		sessionFactory = cfg.getConfiguration(). buildSessionFactory();

	}

	public void validate() {
		delegate.validate();

		final Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		final Connection connection = session.connection();
		DatabaseMetadata metadata;
		try {
			metadata = new DatabaseMetadata(connection, cfg.getDialect());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		cfg.visitMappedTables(new TableVisitor(metadata));

		tx.commit();
		session.close();
	}

	public void destroy() {
		sessionFactory.close();
	}

//	public void validateColumns(Dialect dialect, Mapping mapping, TableMetadata tableInfo) {
//		Iterator iter = getColumnIterator();
//		while (iter.hasNext()) {
//			Column col = (Column) iter.next();
//
//			ColumnMetadata columnInfo = tableInfo.getColumnMetadata(col.getName());
//
//			if (columnInfo == null) {
//				throw new HibernateException("Missing column: " + col.getName());
//			} else {
//				final boolean typesMatch = col.getSqlType(dialect, mapping)
//						.startsWith(columnInfo.getTypeName().toLowerCase())
//						|| columnInfo.getTypeCode() == col.getSqlTypeCode(mapping);
//				if (!typesMatch) {
//					throw new HibernateException(
//							"Wrong column type: " + col.getName() +
//									", expected: " + col.getSqlType(dialect, mapping)
//					);
//				}
//			}
//		}
//
//	}

	class TableVisitor implements Visitor4 {
		DatabaseMetadata metadata;

		public TableVisitor(DatabaseMetadata metadata) {
			this.metadata = metadata;
		}

		public void visit(Object obj) {
			isVersionColumnExistOk(metadata, (Table) obj);
		}

		protected boolean isVersionColumnExistOk(DatabaseMetadata metadata, Table table) {
			TableMetadata tableMetadata = metadata.getTableMetadata(table.getName(), table.getSchema(), table.getCatalog());
			ColumnMetadata versionCol = tableMetadata.getColumnMetadata(Db4oColumns.VERSION.name);
			final boolean exist = versionCol != null;

			if (exist) {
				final String typeName = versionCol.getTypeName();
				//System.out.println("typeName = " + typeName);
				final int typeCode = versionCol.getTypeCode();
				//System.out.println("typeCode = " + typeCode);
			}

			return exist;
		}
	}
}
