package com.db4o.replication.hibernate.impl.ref_as_table;

import com.db4o.replication.hibernate.impl.Util;
import com.db4o.replication.hibernate.metadata.ObjectReference;
import com.db4o.replication.hibernate.metadata.Uuid;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class Shared {
// -------------------------- STATIC METHODS --------------------------

	public static void ensureLong(Serializable id) {
		if (!(id instanceof Long))
			throw new IllegalStateException("You must use 'long' as the type of the hibernate id");
	}

	public static long castAsLong(Serializable id) {
		ensureLong(id);
		return ((Long) id).longValue();
	}

	static void incrementObjectVersion(Connection con, String className, long id) {
		long newVer = Util.getMaxVersion(con) + 1;
		String sql = "UPDATE " + ObjectReference.TABLE_NAME
				+ " SET " + ObjectReference.VERSION + " = " + newVer
				+ " WHERE " + ObjectReference.CLASS_NAME + " = '" + className + "'"
				+ " AND " + ObjectReference.OBJECT_ID + " = " + id;

		final Statement st = Util.getStatement(con);

		try {
			final int affected = st.executeUpdate(sql);

			if (affected != 1)
				throw new RuntimeException("unable to update the version of an object");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			Util.closeStatement(st);
		}
	}

	static void incrementObjectVersion(Session sess, Object entity, long id) {
		incrementObjectVersion(sess.connection(), entity.getClass().getName(), castAsLong(sess.getIdentifier(entity)));
	}

	public static long getVersion(Connection con, String className, long id) {
		final Statement st = Util.getStatement(con);

		String sql = "SELECT " + ObjectReference.VERSION + " FROM " + ObjectReference.TABLE_NAME
				+ " WHERE " + ObjectReference.CLASS_NAME + " = ?"
				+ " AND " + ObjectReference.OBJECT_ID + " = ?";
		final PreparedStatement ps = Util.prepareStatement(con, sql);


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
			Util.closeStatement(st);
			Util.closeResultSet(rs);
		}
	}

	public static ObjectReference getObjectReferenceById(Session session, Object obj) {
		Serializable id = session.getIdentifier(obj);
		Criteria criteria = session.createCriteria(ObjectReference.class);
		criteria.add(Restrictions.eq(ObjectReference.OBJECT_ID, id));
		criteria.add(Restrictions.eq(ObjectReference.CLASS_NAME, obj.getClass().getName()));
		List list = criteria.list();

		if (list.size() == 0)
			return null;
		else if (list.size() == 1)
			return (ObjectReference) list.get(0);
		else
			throw new RuntimeException("Duplicated uuid");
	}

	public static Uuid getUuid(Session session, Object obj) {
		return Shared.getObjectReferenceById(session, obj).getUuid();
	}
}
