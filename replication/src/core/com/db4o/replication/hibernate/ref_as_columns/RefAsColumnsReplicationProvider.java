package com.db4o.replication.hibernate.ref_as_columns;

import com.db4o.ext.Db4oUUID;
import com.db4o.inside.replication.ReadonlyReplicationProviderSignature;
import com.db4o.inside.replication.ReplicationReference;
import com.db4o.inside.replication.ReplicationReferenceImpl;
import com.db4o.replication.hibernate.AbstractReplicationProvider;
import com.db4o.replication.hibernate.ObjectConfig;
import com.db4o.replication.hibernate.RefConfig;
import com.db4o.replication.hibernate.common.ChangedObjectId;
import com.db4o.replication.hibernate.common.Common;
import com.db4o.replication.hibernate.common.ReplicationRecord;
import org.hibernate.FlushMode;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.PostUpdateEvent;
import org.hibernate.mapping.PersistentClass;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Facade to a Hibernate-mapped database. During Instantiation of an instance of
 * this class, it will do <ol> <li> Registers {@link ReadonlyReplicationProviderSignature}
 * and {@link ReplicationRecord} with Hibernate. Hibernate generates
 * corresponding table if they do not exist. </li> <li> Each POJO is mapped to a
 * table in Hibernate, this Provider checks each table for the existence of
 * version, long part and the provider id columns. </li> <li> Creates a table to
 * hold the version/transaction number </li> </ol>
 *
 * @author Albert Kwan
 * @since 5.0
 */
public final class RefAsColumnsReplicationProvider extends AbstractReplicationProvider {
	protected static final String IS_NULL = " IS NULL ";


	public RefAsColumnsReplicationProvider(Configuration cfg) {
		this(cfg, null);
	}

	public RefAsColumnsReplicationProvider(Configuration cfg, String name) {
		_name = name;

		_refCfg = RefAsColumnsConfiguration.produce(cfg);

		_objectConfig = new ObjectConfig(cfg);

		new RefAsColumnsTablesCreator(getRefCfg()).execute();

		initEventListeners();

		_objectSessionFactory = this.getRefCfg().getConfiguration().buildSessionFactory();
		_objectSession = _objectSessionFactory.openSession();
		_objectSession.setFlushMode(FlushMode.ALWAYS);
		_objectTransaction = _objectSession.beginTransaction();

		init();
	}

	protected RefConfig getRefConfig() {
		return getRefCfg();
	}

	public final String getModifiedObjectCriterion() {
		ensureReplicationActive();

		return Db4oColumns.VERSION.name + " > " + getLastReplicationVersion();
	}

	protected ReplicationReference produceObjectReference(Object obj) {
		//System.out.println("produceObjectReference() obj = " + obj);
		if (!getObjectSession().contains(obj)) return null;

		String tableName = getObjectConfig().getTableName(obj.getClass());
		String pkColumn = getObjectConfig().getPrimaryKeyColumnName(obj);
		Serializable identifier = _objectSession.getIdentifier(obj);

		String sql = "SELECT "
				+ Db4oColumns.VERSION.name
				+ ", " + Db4oColumns.UUID_LONG_PART.name
				+ ", " + Db4oColumns.PROVIDER_ID.name
				+ " FROM " + tableName
				+ " where " + pkColumn + "=" + identifier;

		ResultSet rs = null;

		try {
			rs = _objectSession.connection().createStatement().executeQuery(sql);

			if (!rs.next())
				return null;

			ReplicationReference out;

			//if the value is SQL NULL, the value returned is 0
			long longPart = rs.getLong(2);
			if (longPart == 0) {
				Db4oUUID uuid = new Db4oUUID(uuidLongPartGenerator.next(), getSignature().getBytes());
				ReplicationReferenceImpl ref = new ReplicationReferenceImpl(obj, uuid, getLastReplicationVersion());
				storeReplicationMetaData(ref);
				out = createReference(obj, uuid, ref.version());
			} else {
				ReadonlyReplicationProviderSignature owner = getById(rs.getLong(3));
				Db4oUUID uuid = new Db4oUUID(rs.getLong(2), owner.getBytes());
				long version = rs.getLong(1);
				out = createReference(obj, uuid, version);
			}
			return out;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			com.db4o.replication.hibernate.common.Common.closeResultSet(rs);
		}
	}

	protected Session getRefSession() {
		return getObjectSession();
	}

	protected ReplicationReference produceObjectReferenceByUUID(Db4oUUID uuid, Class hint) {
		_objectSession.flush();
		ReadonlyReplicationProviderSignature signature = getProviderSignature(uuid.getSignaturePart());

		final long sigId;

		if (signature == null) return null;
		else sigId = signature.getId();

		String alias = "whatever";

		String tableName = getObjectConfig().getTableName(hint);

		String sql = "SELECT {" + alias + ".*} FROM " + tableName + " " + alias
				+ " where " + Db4oColumns.UUID_LONG_PART.name + "=" + uuid.getLongPart()
				+ " AND " + Db4oColumns.PROVIDER_ID.name + "=" + sigId;
		SQLQuery sqlQuery = _objectSession.createSQLQuery(sql);
		sqlQuery.addEntity(alias, hint);

		final List results = sqlQuery.list();

		final int rowCount = results.size();

		ReplicationReference out;
		if (rowCount == 0) {
			out = null;
		} else if (rowCount == 1) {
			final Object obj = results.get(0);

			ReplicationReference existing = getCachedReference(obj);
			if (existing != null) return existing;

			long version = Shared.getVersion(getRefCfg().getConfiguration(), _objectSession, obj);
			out = createReference(obj, uuid, version);
		} else {
			throw new RuntimeException("The object may either be found or not, it will never find more than one objects");
		}

		return out;
	}

	protected Collection getNewObjectsSinceLastReplication(PersistentClass type) {
		String primaryKeyColumnName = getObjectConfig().getPrimaryKeyColumnName(type);
		String tableName = type.getTable().getName();

		String sql = "SELECT " + primaryKeyColumnName + " FROM " + tableName
				+ " where " + Db4oColumns.UUID_LONG_PART.name + IS_NULL
				+ " AND " + Db4oColumns.PROVIDER_ID.name + IS_NULL;

		Collection out = loadObj(getChangedObjectIds(_objectSession, sql, type));

		generateReplicationMetaData(out);

		return out;
	}

	protected Collection getChangedObjectsSinceLastReplication(PersistentClass type) {
		String primaryKeyColumnName = getObjectConfig().getPrimaryKeyColumnName(type);
		String tableName = type.getTable().getName();

		String sql = "SELECT " + primaryKeyColumnName + " FROM " + tableName
				+ " where " + Db4oColumns.VERSION.name + ">" + getLastReplicationVersion();

		return loadObj(getChangedObjectIds(_objectSession, sql, type));
	}

	protected Collection<ChangedObjectId> getChangedObjectIds(Session sess, String sql, PersistentClass type) {
		Connection connection = sess.connection();
		Statement st = Common.getStatement(connection);

		Set<ChangedObjectId> changedObjectIds = new HashSet();

		try {
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				Object object = rs.getObject(1);
				changedObjectIds.add(new ChangedObjectId((Serializable) object, type.getClassName()));
			}

			rs.close();
			st.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return changedObjectIds;
	}

	protected void storeReplicationMetaData(ReplicationReference ref) {
		String tableName = getObjectConfig().getTableName(ref.object().getClass());
		String pkColumn = getObjectConfig().getPrimaryKeyColumnName(ref.object());
		Serializable identifier = _objectSession.getIdentifier(ref.object());

		String sql = "UPDATE " + tableName + " SET " + Db4oColumns.VERSION.name + "=?"
				+ ", " + Db4oColumns.UUID_LONG_PART.name + "=?"
				+ ", " + Db4oColumns.PROVIDER_ID.name + "=?"
				+ " WHERE " + pkColumn + " =?";

		PreparedStatement ps = null;
		try {
			ps = _objectSession.connection().prepareStatement(sql);

			long refVer = ref.version();
			ps.setLong(1, refVer);
			ps.setLong(2, ref.uuid().getLongPart());
			ps.setLong(3, getProviderSignature(ref.uuid().getSignaturePart()).getId());
			ps.setObject(4, identifier);

			int affected = ps.executeUpdate();
			if (affected != 1) {
				throw new RuntimeException("Unable to update db4o columns");
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			Common.closePreparedStatement(ps);
		}
	}

	protected void incrementObjectVersion(PostUpdateEvent event) {
		//TODO performance sucks, but this method is called when testing only.
		Object entity = event.getEntity();

		long newVer = Common.getMaxVersion(_objectSession.connection()) + 1;
		Shared.incrementObjectVersion(_objectSession.connection(), event.getId(), newVer,
				getObjectConfig().getTableName(entity.getClass()), getObjectConfig().getPrimaryKeyColumnName(entity));
	}
}