package com.db4o.replication.hibernate.ref_as_columns;

import com.db4o.replication.hibernate.common.Common;
import com.db4o.replication.hibernate.ref_as_table.ObjectConfig;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Shared {

	public static long getVersion(Configuration cfg, Session session, Object obj) {
		Connection connection = session.connection();
		ObjectConfig objectConfig = new ObjectConfig(cfg);
		String tableName = objectConfig.getTableName(obj.getClass());
		String pkColumn = objectConfig.getPrimaryKeyColumnName(obj);
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

			return Math.max(rs.getLong(1), Common.MIN_VERSION_NO);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			Common.closeResultSet(rs);
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
			Common.closePreparedStatement(ps);
		}
	}
}
