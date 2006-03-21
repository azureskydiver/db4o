package com.db4o.replication.hibernate.impl.ref_as_columns;

import com.db4o.replication.hibernate.cfg.ObjectConfig;
import com.db4o.replication.hibernate.impl.Constants;
import com.db4o.replication.hibernate.impl.Util;
import com.db4o.replication.hibernate.metadata.ReplicationProviderSignature;
import com.db4o.replication.hibernate.metadata.Uuid;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class Shared {
// -------------------------- STATIC METHODS --------------------------

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

			return Math.max(rs.getLong(1), Constants.MIN_VERSION_NO);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			Util.closeResultSet(rs);
		}
	}

	public static Object[] getUuidAndVersion(String tableName, String pkColumn,
			Serializable identifier, Session session) {
		String sql = "SELECT "
				+ Db4oColumns.VERSION.name
				+ ", " + Db4oColumns.UUID_LONG_PART.name
				+ ", " + Db4oColumns.PROVIDER_ID.name
				+ " FROM " + tableName
				+ " where " + pkColumn + "=" + identifier;

		ResultSet rs = null;

		try {
			rs = session.connection().createStatement().executeQuery(sql);

			if (!rs.next()) throw new RuntimeException("record not found");

			long longPart = rs.getLong(2);
			long sigId = rs.getLong(3);

			if (longPart == Constants.MIN_SEQ_NO)
				throw new RuntimeException("uuid not found");

			if (sigId == 0)
				throw new RuntimeException("uuid not found");

			Uuid uuid = new Uuid();
			uuid.setLongPart(longPart);
			uuid.setProvider((ReplicationProviderSignature) session.get(ReplicationProviderSignature.class, sigId));

			return new Object[]{uuid, rs.getLong(1)};
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			Util.closeResultSet(rs);
		}
	}

	public static void updateMetadata(long version, long uuidLong, Serializable identifier,
			String tableName, String pkColumn, Session session, long sigId) {
		String sql = "UPDATE " + tableName + " SET " + Db4oColumns.VERSION.name + "=?"
				+ ", " + Db4oColumns.UUID_LONG_PART.name + "=?"
				+ ", " + Db4oColumns.PROVIDER_ID.name + "=?"
				+ " WHERE " + pkColumn + " =?";

		PreparedStatement ps = null;
		try {
			ps = session.connection().prepareStatement(sql);

			ps.setLong(1, version);
			ps.setLong(2, uuidLong);
			ps.setLong(3, sigId);
			ps.setObject(4, identifier);

			int affected = ps.executeUpdate();
			if (affected != 1) {
				throw new RuntimeException("Unable to update db4o columns");
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			Util.closePreparedStatement(ps);
		}
	}
}
