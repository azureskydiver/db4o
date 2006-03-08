package com.db4o.replication.hibernate.ref_as_columns;

import com.db4o.foundation.Visitor4;
import com.db4o.replication.hibernate.RefConfig;
import com.db4o.replication.hibernate.common.ReplicationProviderSignature;
import com.db4o.replication.hibernate.ref_as_table.ObjectConfig;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Environment;
import org.hibernate.classic.Session;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.Oracle9Dialect;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.hibernate.tool.hbm2ddl.ColumnMetadata;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.hibernate.tool.hbm2ddl.TableMetadata;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

public class RefAsColumnsTablesCreator {
	protected static final String ALTER_TABLE = "ALTER TABLE ";

	RefConfig cfg;

	/**
	 * Represents a dialect of SQL implemented by a particular RDBMS.
	 */
	protected Dialect dialect;

	/**
	 * Comprehensive information about the database as a whole.
	 */
	protected DatabaseMetadata metadata;

	/**
	 * Hibernate mapped tables, excluding  {@link com.db4o.inside.replication.ReadonlyReplicationProviderSignature}
	 * and {@link com.db4o.replication.hibernate.common.ReplicationRecord}.
	 */
	protected Set mappedTables;

	protected Session session;

	protected Connection connection;

	protected RefAsColumnsSchemaValidator validator;

	public RefAsColumnsTablesCreator(RefConfig aCfg) {
		cfg = aCfg;
		dialect = cfg.getDialect();
		validator = new RefAsColumnsSchemaValidator(cfg);
	}

	/**
	 * @throws RuntimeException when tables/columns not found
	 */
	public void execute() {
		if (cfg.getConfiguration().getProperties().get(Environment.HBM2DDL_AUTO).equals("validate"))
			validator.validate();
		else {
			visitTables();
			validator.validate();
		}
		validator.destroy();
	}

	private void visitTables() {
		SessionFactory sessionFactory = cfg.getConfiguration(). buildSessionFactory();
		session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		connection = session.connection();

		try {
			metadata = new DatabaseMetadata(connection, dialect);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		final Statement st = com.db4o.replication.hibernate.common.Common.getStatement(connection);

		final ModifyingTableVisitor visitor = new ModifyingTableVisitor(metadata, st);
		ObjectConfig objectConfig = new ObjectConfig(cfg.getConfiguration());
		objectConfig.visitMappedTables(visitor);

		com.db4o.replication.hibernate.common.Common.closeStatement(st);

		session.flush();
		tx.commit();
		session.close();
		sessionFactory.close();
	}

	class ModifyingTableVisitor implements Visitor4 {
		protected final DatabaseMetadata metadata;
		final Statement st;

		public ModifyingTableVisitor(DatabaseMetadata metadata, Statement st) {
			this.metadata = metadata;
			this.st = st;
		}

		public void visit(Object obj) {
			final Table table = (Table) obj;

			if (colNotExist(table, Db4oColumns.VERSION))
				createDb4oColumns(table, Db4oColumns.VERSION);

			if (colNotExist(table, Db4oColumns.UUID_LONG_PART))
				createDb4oColumns(table, Db4oColumns.UUID_LONG_PART);

			if (colNotExist(table, Db4oColumns.PROVIDER_ID)) {
				createDb4oColumns(table, Db4oColumns.PROVIDER_ID);
				executeQuery(st, getDb4oSigIdFKConstraintString(table));
			}
		}

		protected boolean colNotExist(Table table, Db4oColumns db4oCol) {
			TableMetadata tableMetadata = metadata.getTableMetadata(table.getName(), table.getSchema(), table.getCatalog());
			ColumnMetadata col = tableMetadata.getColumnMetadata(db4oCol.name);

			if (col == null)
				return true;
			else {
				final int actual = col.getTypeCode();
				final int expected = db4oCol.type;
				if (actual != expected) {
					if (dialect instanceof Oracle9Dialect) {
						if (!com.db4o.replication.hibernate.common.Common.oracleTypeMatches(expected, actual))
							throw new RuntimeException("Wrong column type: " + db4oCol.name + ", expected: " + expected + ", table = " + table
									+ "Please delete this column and restart dRS.");
					} else {
						throw new RuntimeException("Wrong column type: " + db4oCol.name + ", expected: " + expected + ", table = " + table
								+ "Please delete this column and restart dRS.");
					}
				}

				return false;
			}
		}

		protected void createDb4oColumns(Table table, Db4oColumns db4oCol) {
			final String tableName = table.getQualifiedName(cfg.getDialect(), null, null);
			String addcolStr = " " + cfg.getDialect().getAddColumnString() + " ";
			final String sql = ALTER_TABLE + tableName + addcolStr + db4oCol.name + " " + cfg.getType(db4oCol.type);
			executeQuery(st, sql);
		}

		private void executeQuery(Statement st, String sql) {
			//System.out.println(dialect + " ||| " + sql);
			try {
				st.execute(sql);
			} catch (SQLException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}

		protected String getDb4oSigIdFKConstraintString(Table table) {
			final String tableName = table.getQualifiedName(cfg.getDialect(), null, null);

			final PersistentClass classMapping = cfg.getConfiguration().getClassMapping(ReplicationProviderSignature.class.getName());
			final Table rpsTable = classMapping.getTable();

			final String rpsqualifiedName = rpsTable.getQualifiedName(cfg.getDialect(), null, null);

			//ADD foreign key constraint
			String constriantName = "DB4O_" + tableName;
			final String[] foreignKeys = {Db4oColumns.PROVIDER_ID.name};
			final String addForeignKeyConstraintString;
			addForeignKeyConstraintString = ALTER_TABLE + tableName
					+ cfg.getDialect().getAddForeignKeyConstraintString(
					constriantName, foreignKeys, rpsqualifiedName, foreignKeys, true);

			return addForeignKeyConstraintString;
		}
	}
}
