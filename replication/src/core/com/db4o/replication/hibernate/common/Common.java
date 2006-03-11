package com.db4o.replication.hibernate.common;

import com.db4o.inside.replication.ReadonlyReplicationProviderSignature;
import com.db4o.replication.hibernate.ref_as_table.ReplicationReference;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.mapping.Table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class Common {
	public static final long MIN_VERSION_NO = 100;

	public static void dumpTable(String providerName, Session sess, String tableName) {
		dumpTable(providerName, sess.connection(), tableName);
	}

	public static void dumpTable(String providerName, Connection con, String tableName) {
		ResultSet rs = null;

		try {
			System.out.println("providerName = " + providerName + ", table = " + tableName);
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

	public static void closePreparedStatement(PreparedStatement ps) {
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static void closeResultSet(ResultSet rs) {
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

	public static PreparedStatement getPreparedStatement(Connection connection, String sql) {
		try {
			return connection.prepareStatement(sql);
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

	public static boolean skip(Object obj) {
		return obj instanceof ReplicationRecord
				|| obj instanceof ReadonlyReplicationProviderSignature
				|| obj instanceof ReplicationComponentField
				|| obj instanceof ReplicationComponentIdentity
				|| obj instanceof UuidLongPartSequence
				|| obj instanceof ReplicationReference;
	}

	public static boolean skip(Class claxx) {
		return claxx == ReplicationRecord.class
				|| claxx == ReplicationProviderSignature.class
				|| claxx == PeerSignature.class
				|| claxx == MySignature.class
				|| claxx == ReplicationComponentField.class
				|| claxx == ReplicationComponentIdentity.class
				|| claxx == UuidLongPartSequence.class
				|| claxx == ReplicationReference.class;
	}

	public static boolean skip(Table table) {
		return table.getName().equals(ReplicationProviderSignature.TABLE_NAME)
				|| table.getName().equals(ReplicationRecord.TABLE_NAME)
				|| table.getName().equals(ReplicationComponentField.TABLE_NAME)
				|| table.getName().equals(ReplicationComponentIdentity.TABLE_NAME)
				|| table.getName().equals(UuidLongPartSequence.TABLE_NAME)
				|| table.getName().equals(ReplicationReference.TABLE_NAME);
	}

	public static long getMaxVersion(Connection con) {
		String sql = "SELECT max(" + ReplicationRecord.VERSION + ") from " + ReplicationRecord.TABLE_NAME;
		Statement st = null;
		ResultSet rs = null;
		try {
			st = con.createStatement();
			rs = st.executeQuery(sql);

			if (!rs.next())
				throw new RuntimeException("failed to get the max version, the sql was = " + sql);
			return Math.max(rs.getLong(1), MIN_VERSION_NO);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
	}

	public static void setCurrentSessionContext(Configuration cfg) {
		String key = Environment.CURRENT_SESSION_CONTEXT_CLASS;
		if (cfg.getProperty(key) == null)
			cfg.setProperty(key, "thread");
	}
}
