package com.db4o.replication.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Environment;
import org.hibernate.classic.Session;
import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.Table;
import org.hibernate.tool.hbm2ddl.ColumnMetadata;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.hibernate.tool.hbm2ddl.TableMetadata;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Set;

public class MetaDataTablesCreator {
	protected static final String ALTER_TABLE = "ALTER TABLE ";

	ReplicationConfiguration cfg;

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
	 * and {@link ReplicationRecord}.
	 */
	protected Set mappedTables;

	protected Session session;

	protected Connection connection;

	protected SchemaValidator validator;

	public MetaDataTablesCreator(ReplicationConfiguration aCfg) {
		cfg = aCfg;
		dialect = cfg.getDialect();
		validator = new SchemaValidator(cfg);
	}

	public void execute() {
		if (cfg.getConfiguration().getProperties().get(Environment.HBM2DDL_AUTO).equals("validate"))
			validator.validate();

		SessionFactory sessionFactory = cfg.getConfiguration(). buildSessionFactory();
		session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		connection = session.connection();

		try {
			metadata = new DatabaseMetadata(connection, dialect);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		mappedTables = cfg.getMappedTables();
		checkMappedTables();
		session.flush();
		tx.commit();
		session.close();
		sessionFactory.close();

		validator.validate();
	}


	protected void checkMappedTables() {
		for (Iterator iterator = mappedTables.iterator(); iterator.hasNext();) {
			Table table = (Table) iterator.next();
			if (Util.skip(table))
				continue;

			if (!isVersionColumnExist(table))
				createDb4oColumns(table.getName());
		}
	}

	protected boolean isVersionColumnExist(Table table) {
		TableMetadata tableMetadata = metadata.getTableMetadata(table.getName(), null, null);
		ColumnMetadata versionCol = tableMetadata.getColumnMetadata(Db4oColumns.VERSION.name);
		return versionCol != null;
	}


	protected void createDb4oColumns(String tableName) {
		Connection connection = this.connection;
		String addcolStr = " " + dialect.getAddColumnString() + " ";

		Statement st;

		try {
			st = connection.createStatement();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		executeQuery(st, ALTER_TABLE + tableName + addcolStr
				+ Db4oColumns.UUID_LONG_PART.name + " " + cfg.getType(Db4oColumns.UUID_LONG_PART.sqlType));

		executeQuery(st, ALTER_TABLE + tableName + addcolStr
				+ Db4oColumns.VERSION.name + " " + cfg.getType(Db4oColumns.VERSION.sqlType));

		executeQuery(st, ALTER_TABLE + tableName + addcolStr
				+ ReplicationProviderSignature.SIGNATURE_ID_COLUMN_NAME + " "
				+ cfg.getType(ReplicationProviderSignature.SIGNATURE_ID_COLUMN_TYPE));

		executeQuery(st, getDb4oSigIdFKConstraintString(tableName));

		Util.closeStatement(st);
	}

	private void executeQuery(Statement st, String sql) {
		try {
			st.execute(sql);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	protected String getDb4oSigIdFKConstraintString(String tableName) {
		//ADD foreign key constraint
		String constriantName = "DB4O_" + tableName;
		final String[] foreignKeys = {ReplicationProviderSignature.SIGNATURE_ID_COLUMN_NAME};
		final String addForeignKeyConstraintString;
		addForeignKeyConstraintString = ALTER_TABLE + tableName
				+ dialect.getAddForeignKeyConstraintString(
				constriantName, foreignKeys, ReplicationProviderSignature.TABLE_NAME, foreignKeys, true);

		return addForeignKeyConstraintString;
	}
}
