package com.db4o.replication.hibernate;

import com.db4o.inside.replication.ReadonlyReplicationProviderSignature;
import com.db4o.replication.hibernate.metadata.Db4oColumns;
import com.db4o.replication.hibernate.metadata.MySignature;
import com.db4o.replication.hibernate.metadata.PeerSignature;
import com.db4o.replication.hibernate.metadata.ReplicationComponentField;
import com.db4o.replication.hibernate.metadata.ReplicationComponentIdentity;
import com.db4o.replication.hibernate.metadata.ReplicationProviderSignature;
import com.db4o.replication.hibernate.metadata.ReplicationRecord;
import com.db4o.replication.hibernate.metadata.UuidLongPartSequence;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Table;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class Util {
	static boolean skip(Object obj) {
		return obj instanceof ReplicationRecord
				|| obj instanceof ReadonlyReplicationProviderSignature
				|| obj instanceof ReplicationComponentField
				|| obj instanceof ReplicationComponentIdentity
				|| obj instanceof UuidLongPartSequence;
	}

	static boolean skip(Class claxx) {
		return claxx == ReplicationRecord.class
				|| claxx == ReplicationProviderSignature.class
				|| claxx == PeerSignature.class
				|| claxx == MySignature.class
				|| claxx == ReplicationComponentField.class
				|| claxx == ReplicationComponentIdentity.class
				|| claxx == UuidLongPartSequence.class;
	}

	static boolean skip(Table table) {
		return table.getName().equals(ReplicationProviderSignature.TABLE_NAME)
				|| table.getName().equals(ReplicationRecord.TABLE_NAME)
				|| table.getName().equals(ReplicationComponentField.TABLE_NAME)
				|| table.getName().equals(ReplicationComponentIdentity.TABLE_NAME)
				|| table.getName().equals(UuidLongPartSequence.TABLE_NAME);
	}


	static long getMaxVersion(Connection con) {
		String sql = "SELECT max(" + ReplicationRecord.VERSION + ") from " + ReplicationRecord.TABLE_NAME;
		Statement st = null;
		ResultSet rs = null;
		try {
			st = con.createStatement();
			rs = st.executeQuery(sql);

			if (!rs.next())
				throw new RuntimeException("failed to get the max version, the sql was = " + sql);
			return Math.max(rs.getLong(1), Constants.MIN_VERSION_NO);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
	}

	public static long getVersion(Configuration cfg, Session session, Object obj) {
		Connection connection = session.connection();
		MetadataProviderReplicationConfiguration rc = MetadataProviderReplicationConfiguration.produce(cfg);
		String tableName = rc.getTableName(obj.getClass());
		//Util.dumpTable(connection, tableName);
		String pkColumn = rc.getPrimaryKeyColumnName(obj);
		Serializable identifier = session.getIdentifier(obj);

		String sql = "SELECT "
				+ Db4oColumns.VERSION.name
				+ " FROM " + tableName
				+ " where " + pkColumn + "=" + identifier;

		ResultSet rs = null;

		try {
			rs = connection.createStatement().executeQuery(sql);

			if (!rs.next())
				throw new RuntimeException("Cannot find the version of " + obj);

			return Math.max(rs.getLong(1), Constants.MIN_VERSION_NO);
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
			String sql = "UPDATE " + tableName + " SET " + Db4oColumns.VERSION.name + "=?"
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

	public static void dumpTable(Session sess, String tableName) {
		dumpTable(sess.connection(), tableName);
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

	public static void closeStatement(Statement st) {
		if (st != null) {
			try {
				st.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static Statement getStatement(Connection connection) {
		try {
			return connection.createStatement();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean oracleTypeMatches(int expected, int actual) {
		if (expected == actual)
			return true;

		if (expected != Types.BIGINT)
			throw new UnsupportedOperationException("Only support Types.BIGINT");

		return actual == Types.DECIMAL;
	}
}
