package com.db4o.replication.hibernate.impl;

import com.db4o.ext.Db4oUUID;
import com.db4o.inside.replication.ReadonlyReplicationProviderSignature;
import com.db4o.replication.hibernate.HibernateReplicationProvider;
import com.db4o.replication.hibernate.metadata.DeletedObject;
import com.db4o.replication.hibernate.metadata.MySignature;
import com.db4o.replication.hibernate.metadata.ObjectReference;
import com.db4o.replication.hibernate.metadata.PeerSignature;
import com.db4o.replication.hibernate.metadata.ReplicationComponentField;
import com.db4o.replication.hibernate.metadata.ReplicationComponentIdentity;
import com.db4o.replication.hibernate.metadata.ReplicationProviderSignature;
import com.db4o.replication.hibernate.metadata.ReplicationRecord;
import com.db4o.replication.hibernate.metadata.Uuid;
import com.db4o.replication.hibernate.metadata.UuidLongPartSequence;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
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
import java.util.List;

public final class Util {
// -------------------------- STATIC METHODS --------------------------

	public static boolean skip(Table table) {
		return table.getName().equals(ReplicationProviderSignature.TABLE_NAME)
				|| table.getName().equals(ReplicationRecord.TABLE_NAME)
				|| table.getName().equals(ReplicationComponentField.TABLE_NAME)
				|| table.getName().equals(ReplicationComponentIdentity.TABLE_NAME)
				|| table.getName().equals(UuidLongPartSequence.TABLE_NAME)
				|| table.getName().equals(ObjectReference.TABLE_NAME)
				|| table.getName().equals(DeletedObject.TABLE_NAME);
	}

	public static boolean skip(Class claxx) {
		return claxx == ReplicationRecord.class
				|| claxx == ReplicationProviderSignature.class
				|| claxx == PeerSignature.class
				|| claxx == MySignature.class
				|| claxx == ReplicationComponentField.class
				|| claxx == ReplicationComponentIdentity.class
				|| claxx == UuidLongPartSequence.class
				|| claxx == ObjectReference.class
				|| claxx == DeletedObject.class;
	}

	public static boolean skip(Object obj) {
		return obj instanceof ReplicationRecord
				|| obj instanceof ReadonlyReplicationProviderSignature
				|| obj instanceof ReplicationComponentField
				|| obj instanceof ReplicationComponentIdentity
				|| obj instanceof UuidLongPartSequence
				|| obj instanceof ObjectReference
				|| obj instanceof DeletedObject;
	}

	public static Statement getStatement(Connection connection) {
		try {
			return connection.createStatement();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public static PreparedStatement prepareStatement(Connection connection, String sql) {
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

	public static long getMaxVersion(Connection con) {
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

	public static void setCurrentSessionContext(Configuration cfg) {
		String key = Environment.CURRENT_SESSION_CONTEXT_CLASS;
		if (cfg.getProperty(key) == null)
			cfg.setProperty(key, "thread");
	}

	public static void dumpTable(HibernateReplicationProvider p, String s) {
		dumpTable(p.getName(), p.getSession(), s);
	}

	private static void dumpTable(String providerName, Connection con, String tableName) {
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

	private static void dumpTable(String providerName, Session sess, String tableName) {
		dumpTable(providerName, sess.connection(), tableName);
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

	public static void addClass(Configuration cfg, Class aClass) {
		if (cfg.getClassMapping(aClass.getName()) == null)
			cfg.addClass(aClass);
	}

	public static MySignature genMySignature(Session session) {
		final List sigs = session.createCriteria(MySignature.class).list();
		final int mySigCount = sigs.size();

		if (mySigCount < 1) {
			MySignature out = MySignature.generateSignature();
			session.save(out);
			return out;
		} else if (mySigCount == 1)
			return (MySignature) sigs.get(0);
		else
			throw new RuntimeException("Number of MySignature should be exactly 1, but i got " + mySigCount);
	}

	public static void initMySignature(Configuration cfg) {
		SessionFactory sf = cfg.buildSessionFactory();
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();

		if (session.createCriteria(MySignature.class).list().size() < 1)
			session.save(MySignature.generateSignature());

		tx.commit();
		session.close();
		sf.close();
	}

	public static void initUuidLongPartSequence(Configuration cfg) {
		SessionFactory sf = cfg.buildSessionFactory();
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();

		if (session.createCriteria(UuidLongPartSequence.class).list().size() < 1)
			session.save(new UuidLongPartSequence());

		tx.commit();
		session.close();
		sf.close();
	}

	protected static String flattenBytes(byte[] b) {
		String out = "";
		for (int i = 0; i < b.length; i++) {
			out += ", " + b[i];
		}
		return out;
	}

	protected static void sleep(int i, String s) {
		System.out.println(s);
		try {
			Thread.sleep(i * 1000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public static Db4oUUID translate(Uuid uuid) {
		return new Db4oUUID(uuid.getLongPart(), uuid.getProvider().getBytes());
	}

	public static Db4oUUID translate(DeletedObject doo) {
		return translate(doo.getUuid());
	}

	public static DeletedObject getDeletedObject(Session session, Uuid uuid) {
		String alias = "do";
		String uuidPath = alias + "." + DeletedObject.UUID + ".";
		String queryString = "from " + DeletedObject.TABLE_NAME
				+ " as " + alias + " where " + uuidPath + Uuid.LONG_PART + "=?"
				+ " AND " + uuidPath + Uuid.PROVIDER + "." + ReplicationProviderSignature.BYTES + "=?";
		Query c = session.createQuery(queryString);
		c.setLong(0, uuid.getLongPart());
		c.setBinary(1, uuid.getProvider().getBytes());

		List list = c.list();

		if (list.size() == 0)
			return null;
		else
			return (DeletedObject) list.get(0);
	}

}
