package com.db4o.replication.hibernate.ref_as_table;

import com.db4o.replication.hibernate.common.Common;
import org.hibernate.Session;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Shared {
	public static void ensureLong(Serializable id) {
		if (!(id instanceof Long))
			throw new IllegalStateException("You must use 'long' as the type of the hibernate id");
	}

	public static long castAsLong(Serializable id) {
		ensureLong(id);
		return ((Long) id).longValue();
	}

	static void incrementObjectVersion(Session sess, Object entity, long id) {
		incrementObjectVersion(sess.connection(), entity.getClass().getName(), castAsLong(sess.getIdentifier(entity)));
	}

	static void incrementObjectVersion(Connection con, String className, long id) {
		long newVer = Common.getMaxVersion(con) + 1;
		String sql = "UPDATE " + ReplicationReference.TABLE_NAME
				+ " SET " + ReplicationReference.VERSION + " = " + newVer
				+ " WHERE " + ReplicationReference.CLASS_NAME + " = '" + className + "'"
				+ " AND " + ReplicationReference.OBJECT_ID + " = " + id;

		final Statement st = Common.getStatement(con);

		try {
			final int affected = st.executeUpdate(sql);

			if (affected != 1)
				throw new RuntimeException("unable to update the version of an object");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			Common.closeStatement(st);
		}
	}

	public static long getVersion(Connection con, String className, long id) {
		final Statement st = Common.getStatement(con);

		String sql = "SELECT " + ReplicationReference.VERSION + " FROM " + ReplicationReference.TABLE_NAME
				+ " WHERE " + ReplicationReference.CLASS_NAME + " = ?"
				+ " AND " + ReplicationReference.OBJECT_ID + " = ?";
		final PreparedStatement ps = Common.getPreparedStatement(con, sql);


		ResultSet rs = null;
		try {
			ps.setString(1, className);
			ps.setLong(2, id);

			rs = ps.executeQuery();

			if (!rs.next())
				throw new RuntimeException("failed to get the version, the sql was = " + sql);

			return rs.getLong(1);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			Common.closeStatement(st);
			Common.closeResultSet(rs);
		}
	}
}
