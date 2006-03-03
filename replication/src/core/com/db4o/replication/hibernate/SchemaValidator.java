package com.db4o.replication.hibernate;

import com.db4o.foundation.Visitor4;
import com.db4o.replication.hibernate.metadata.Db4oColumns;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.Oracle9Dialect;
import org.hibernate.mapping.Table;
import org.hibernate.tool.hbm2ddl.ColumnMetadata;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.hibernate.tool.hbm2ddl.TableMetadata;

import java.sql.Connection;
import java.sql.SQLException;

public class SchemaValidator {
	private MetadataProviderReplicationConfiguration cfg;

	protected org.hibernate.tool.hbm2ddl.SchemaValidator delegate;
	protected SessionFactory sessionFactory;
	protected final Dialect dialect;

	public SchemaValidator(MetadataProviderReplicationConfiguration aCfg) {
		cfg = aCfg;

		delegate = new org.hibernate.tool.hbm2ddl.SchemaValidator(cfg.getConfiguration());
		dialect = cfg.getDialect();
	}

	public void validate() {
		if (sessionFactory == null)
			sessionFactory = cfg.getConfiguration(). buildSessionFactory();

		delegate.validate();

		final Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		final Connection connection = session.connection();
		DatabaseMetadata metadata;
		try {
			metadata = new DatabaseMetadata(connection, dialect);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		final ValidatingTableVisitor visitor = new ValidatingTableVisitor(metadata);
		cfg.visitMappedTables(visitor);

		tx.commit();
		session.close();
	}

	public void destroy() {
		sessionFactory.close();
	}

	class ValidatingTableVisitor implements Visitor4 {
		final DatabaseMetadata metadata;

		ValidatingTableVisitor(DatabaseMetadata metadata) {
			this.metadata = metadata;
		}

		public void visit(Object obj) {
			final Table table = (Table) obj;
			visitCol(table, Db4oColumns.VERSION);
			visitCol(table, Db4oColumns.UUID_LONG_PART);
			visitCol(table, Db4oColumns.PROVIDER_ID);
		}

		protected void visitCol(Table table, Db4oColumns db4oCol) {
			TableMetadata tableMetadata = metadata.getTableMetadata(table.getName(), table.getSchema(), table.getCatalog());
			ColumnMetadata col = tableMetadata.getColumnMetadata(db4oCol.name);

			if (col == null)
				throw new RuntimeException("Missing column: column name = " + db4oCol.name + ", table = " + table);
			else {
				final int actualType = col.getTypeCode();
				final int expected = db4oCol.type;

				if (actualType != expected) {
					if (dialect instanceof Oracle9Dialect) {
						if (!Util.oracleTypeMatches(expected, actualType))
							throw new RuntimeException("Wrong column type: " + db4oCol.name + ", expected: " + cfg.getType(expected) + ", table = " + table);
					} else {
						throw new RuntimeException("Wrong column type: " + db4oCol.name + ", expected: " + cfg.getType(expected) + ", table = " + table);
					}
				}
			}
		}
	}
}
