package com.db4o.replication.hibernate;

import com.db4o.inside.replication.ReadonlyReplicationProviderSignature;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;

import java.io.Serializable;
import java.sql.*;
import java.util.Iterator;

public class Util {
	static void addMetaDataClasses(Configuration cfg) {
		addClass(cfg, ReplicationProviderSignature.class);
		addClass(cfg, ReplicationRecord.class);
		addClass(cfg, ReplicationComponentIdentity.class);
		addClass(cfg, ReplicationComponentField.class);
	}

	static void addClass(Configuration cfg, Class aClass) {
		if (cfg.getClassMapping(aClass.getName()) == null)
			cfg.addClass(aClass);
	}

	static String getPrimaryKeyColumnName(Configuration cfg, Object entity) {
		final String className = entity.getClass().getName();
		final PersistentClass pClass = cfg.getClassMapping(className);

		PrimaryKey primaryKey = pClass.getTable().getPrimaryKey();
		Iterator columnIterator = primaryKey.getColumnIterator();

		String pkColName;

		pkColName = ((Column) columnIterator.next()).getName();
		if (columnIterator.hasNext()) {
			throw new RuntimeException("we don't support composite primary keys");
		}

		return pkColName;
	}

	static String getTableName(Configuration cfg, Class pClass) {
		PersistentClass mapped = cfg.getClassMapping(pClass.getName());
		if (mapped == null)
			throw new RuntimeException(pClass + " is not mapped using a hbm.xml file.");
		return mapped.getTable().getName();
	}

	static long getMaxVersion(Connection con) {
		String sql = "SELECT max(" + ReplicationRecord.VERSION + ") from " + ReplicationRecord.TABLE_NAME;
		Statement st = null;
		ResultSet rs = null;
		try {
			st = con.createStatement();
			rs = st.executeQuery(sql);

			if (rs.next())
				return rs.getLong(1);
			else
				return Constants.MIN_VERSION_NO;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
	}

	public static long getVersion(Configuration cfg, Session session, Object obj) {
		String tableName = Util.getTableName(cfg, obj.getClass());
		String pkColumn = Util.getPrimaryKeyColumnName(cfg, obj);
		Serializable identifier = session.getIdentifier(obj);

		String sql = "SELECT "
				+ Db4oColumns.DB4O_VERSION
				+ " FROM " + tableName
				+ " where " + pkColumn + "=" + identifier;

		ResultSet rs = null;

		try {
			rs = session.connection().createStatement().executeQuery(sql);

			if (!rs.next())
				throw new RuntimeException("Cannot find the version of " + obj);

			return rs.getLong(1);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			closeResultSet(rs);
		}
	}

	static void incrementObjectVersion(Connection connection, Serializable id, long newVersion,
	                                   String tableName, String primaryKeyColumnName) {
		PreparedStatement ps = null;

		try {
			String sql = "UPDATE " + tableName + " SET " + Db4oColumns.DB4O_VERSION + "=?"
					+ " WHERE " + primaryKeyColumnName + " =?";
			ps = connection.prepareStatement(sql);
			ps.setLong(1, newVersion);
			ps.setObject(2, id);

			int affected = ps.executeUpdate();
			if (affected != 1) {
				throw new RuntimeException("Unable to update the version column");
			}
			ps.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			closePreparedStatement(ps);
		}
	}

	static void dumpTable(Connection con, String tableName) {
		ResultSet rs = null;

		try {
			System.out.println("Printing table = " + tableName);
			String sql = "SELECT * FROM " + tableName;
			rs = con.createStatement().executeQuery(sql);
			ResultSetMetaData metaData = rs.getMetaData();
			int columnCount = metaData.getColumnCount();
			for (int i = 1; i <= columnCount; i++) {
				System.out.print(metaData.getColumnName(i) + "\t|");
			}
			System.out.println();
			while (rs.next()) {
				for (int i = 1; i <= columnCount; i++) {
					System.out.print(rs.getObject(i) + "\t|");
				}
				System.out.println();
			}
			System.out.println("Printing table = " + tableName + " - done");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			closeResultSet(rs);
		}
	}

	static void closePreparedStatement(PreparedStatement ps) {
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	static void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	static void closeStatement(Statement st) {
		if (st != null) {
			try {
				st.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	static boolean skip(Object obj) {
		return obj instanceof ReplicationRecord
				|| obj instanceof ReadonlyReplicationProviderSignature
				|| obj instanceof ReplicationComponentField
				|| obj instanceof ReplicationComponentIdentity;
	}

	static boolean skip(Class claxx) {
		return claxx == ReplicationRecord.class
				|| claxx == ReplicationProviderSignature.class
				|| claxx == PeerSignature.class
				|| claxx == MySignature.class
				|| claxx == ReplicationComponentField.class
				|| claxx == ReplicationComponentIdentity.class;
	}

	static boolean skip(Table table) {
		return table.getName().equals(ReplicationProviderSignature.TABLE_NAME)
				|| table.getName().equals(ReplicationRecord.TABLE_NAME)
				|| table.getName().equals(ReplicationComponentField.TABLE_NAME)
				|| table.getName().equals(ReplicationComponentIdentity.TABLE_NAME);
	}
}
