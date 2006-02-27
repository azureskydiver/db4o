package com.db4o.replication.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
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
import java.sql.Types;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SchemaValidator {
	protected static final String ALTER_TABLE = "ALTER TABLE ";

	Configuration cfg;

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
	 * and {@link com.db4o.replication.hibernate.ReplicationRecord}.
	 */
	protected Set mappedTables;

	protected Session session;

	protected Connection connection;

	public SchemaValidator(Configuration aCfg) {
		cfg = aCfg;
		dialect = Dialect.getDialect(cfg.getProperties());
	}

	public void execute() {
		mappedTables = getMappedTables();

		SessionFactory sessionFactory = cfg. buildSessionFactory();
		session = sessionFactory.openSession();
		connection = session.connection();

		try {
			metadata = new DatabaseMetadata(connection, dialect);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		if (cfg.getProperties().get(Environment.HBM2DDL_AUTO).equals("validate")) {
			validate();
		} else {
			createColumns();
			validate();
		}

		session.close();
		sessionFactory.close();
	}

	public void validate() {
		boolean ok = true;
		try {
			new org.hibernate.tool.hbm2ddl.SchemaValidator(cfg).validate();
		} catch (HibernateException e) {
			throw new RuntimeException(e);
		}

		//TODO check columns
	}

	private void createColumns() {
		Transaction tx = session.beginTransaction();

		checkColumns(true);
		session.flush();
		tx.commit();

	}


	protected Set getMappedTables() {
		Set tables = new HashSet();
		Iterator tableMappings = cfg.getTableMappings();

		while (tableMappings.hasNext()) {
			Table table = (Table) tableMappings.next();

			if (Util.skip(table))
				continue;
			tables.add(table);
		}
		return tables;
	}

	protected void checkColumns(boolean createCol) {
		for (Iterator iterator = mappedTables.iterator(); iterator.hasNext();) {
			Table table = (Table) iterator.next();
			if (Util.skip(table))
				continue;

			if (!isVersionColumnExist(table))
				if (createCol)
					createDb4oColumns(table.getName());
		}
	}

	protected boolean isVersionColumnExist(Table table) {
		TableMetadata tableMetadata = metadata.getTableMetadata(table.getName(), null, null);
		ColumnMetadata versionCol = tableMetadata.getColumnMetadata(Db4oColumns.DB4O_VERSION);
		return versionCol != null;
	}

	protected String getLongVarBinaryType() {
		return dialect.getTypeName(Types.LONGVARBINARY);
	}

	protected String getBigIntType() {
		return dialect.getTypeName(Types.BIGINT);
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

		executeQuery(st, SchemaValidator.ALTER_TABLE + tableName + addcolStr
				+ Db4oColumns.DB4O_UUID_LONG_PART + " " + getBigIntType());

		executeQuery(st, SchemaValidator.ALTER_TABLE + tableName + addcolStr
				+ Db4oColumns.DB4O_VERSION + " " + getBigIntType());

		executeQuery(st, SchemaValidator.ALTER_TABLE + tableName + addcolStr
				+ ReplicationProviderSignature.SIGNATURE_ID_COLUMN_NAME + " " + getBigIntType());

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
		addForeignKeyConstraintString = SchemaValidator.ALTER_TABLE + tableName
				+ dialect.getAddForeignKeyConstraintString(
				constriantName, foreignKeys, ReplicationProviderSignature.TABLE_NAME, foreignKeys, true);

		return addForeignKeyConstraintString;
	}
}
