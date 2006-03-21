package com.db4o.replication.hibernate.impl.ref_as_columns;

import com.db4o.foundation.Visitor4;
import com.db4o.replication.hibernate.cfg.ObjectConfig;
import com.db4o.replication.hibernate.cfg.RefConfig;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.Oracle9Dialect;
import org.hibernate.mapping.Table;
import org.hibernate.tool.hbm2ddl.ColumnMetadata;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.hibernate.tool.hbm2ddl.SchemaValidator;
import org.hibernate.tool.hbm2ddl.TableMetadata;

import java.sql.Connection;
import java.sql.SQLException;

public final class RefAsColumnsSchemaValidator {
// ------------------------------ FIELDS ------------------------------

	private final SchemaValidator delegate;
	private SessionFactory sessionFactory;
	private final Dialect dialect;
	private final RefConfig cfg;

// --------------------------- CONSTRUCTORS ---------------------------

	public RefAsColumnsSchemaValidator(RefConfig aCfg) {
		cfg = aCfg;

		delegate = new SchemaValidator(cfg.getConfiguration());
		dialect = cfg.getDialect();
	}

	public final void destroy() {
		sessionFactory.close();
	}

	public final void validate() {
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
		ObjectConfig objectConfig = new ObjectConfig(cfg.getConfiguration());
		objectConfig.visitMappedTables(visitor);

		tx.commit();
		session.close();
	}

// -------------------------- INNER CLASSES --------------------------

	final class ValidatingTableVisitor implements Visitor4 {
		final DatabaseMetadata metadata;

		ValidatingTableVisitor(DatabaseMetadata metadata) {
			this.metadata = metadata;
		}

		public final void visit(Object obj) {
			final Table table = (Table) obj;
			visitCol(table, Db4oColumns.VERSION);
			visitCol(table, Db4oColumns.UUID_LONG_PART);
			visitCol(table, Db4oColumns.PROVIDER_ID);
		}

		final void visitCol(Table table, Db4oColumns db4oCol) {
			TableMetadata tableMetadata = metadata.getTableMetadata(table.getName(), table.getSchema(), table.getCatalog());
			ColumnMetadata col = tableMetadata.getColumnMetadata(db4oCol.name);

			if (col == null)
				throw new RuntimeException("Missing column: column name = " + db4oCol.name + ", table = " + table);
			else {
				final int actualType = col.getTypeCode();
				final int expected = db4oCol.type;

				if (actualType != expected) {
					if (dialect instanceof Oracle9Dialect) {
						if (!com.db4o.replication.hibernate.impl.Util.oracleTypeMatches(expected, actualType))
							throw new RuntimeException("Wrong column type: " + db4oCol.name + ", expected: " + cfg.getType(expected) + ", table = " + table);
					} else {
						throw new RuntimeException("Wrong column type: " + db4oCol.name + ", expected: " + cfg.getType(expected) + ", table = " + table);
					}
				}
			}
		}
	}
}
