package com.db4o.replication.hibernate.impl.ref_as_columns;

import com.db4o.ext.Db4oUUID;
import com.db4o.inside.replication.ReadonlyReplicationProviderSignature;
import com.db4o.inside.replication.ReplicationReference;
import com.db4o.replication.hibernate.cfg.ObjectConfig;
import com.db4o.replication.hibernate.cfg.RefConfig;
import com.db4o.replication.hibernate.impl.AbstractReplicationProvider;
import com.db4o.replication.hibernate.impl.HibernateObjectId;
import com.db4o.replication.hibernate.impl.ReplicationReferenceImpl;
import com.db4o.replication.hibernate.impl.Util;
import com.db4o.replication.hibernate.metadata.ReplicationProviderSignature;
import com.db4o.replication.hibernate.metadata.ReplicationRecord;
import com.db4o.replication.hibernate.metadata.Uuid;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.PostInsertEvent;
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

		new RefAsColumnsTablesCreator(getRefCfg()).createTables();

		initEventListeners();

		_objectSessionFactory = this.getRefCfg().getConfiguration().buildSessionFactory();
		_objectSession = _objectSessionFactory.openSession();
		_objectSession.setFlushMode(FlushMode.COMMIT);
		_objectTransaction = _objectSession.beginTransaction();

		init();
		_alive = true;

	}

	protected RefConfig getRefConfig() {
		return getRefCfg();
	}

	public final String getModifiedObjectCriterion() {
		ensureReplicationActive();

		return Db4oColumns.VERSION.name + " > " + getLastReplicationVersion();
	}

	protected ReplicationReference produceObjectReference(Object obj) {
		if (!getSession().contains(obj)) return null;

		Object[] uuidAndVersion = getUuidAndVersion(obj);
		Uuid uuid = (Uuid) uuidAndVersion[0];
		long version = (Long) uuidAndVersion[1];

		return objRefs.put(obj, translate(uuid), version);
	}

	protected Session getRefSession() {
		return getSession();
	}

	protected ReplicationReference produceObjectReferenceByUUID(Db4oUUID uuid, Class hint) {
		_objectSession.flush();

		ReplicationProviderSignature signature = getProviderSignature(uuid.getSignaturePart());
		final long sigId;

		if (signature == null) return null;
		else sigId = signature.getId();

		String tableName = getObjectConfig().getTableName(hint);
		String primaryKeyColumnName = getObjectConfig().getPrimaryKeyColumnName(hint);
		String verColName = Db4oColumns.VERSION.name;

		String sql = "SELECT " + primaryKeyColumnName + "," + verColName + " FROM " + tableName
				+ " where " + Db4oColumns.UUID_LONG_PART.name + "=" + uuid.getLongPart()
				+ " AND " + Db4oColumns.PROVIDER_ID.name + "=" + sigId;

		try {
			ResultSet rs = _objectSession.connection().createStatement().executeQuery(sql);

			if (rs.next())
				return objRefs.put(loadObject(new HibernateObjectId(
						(Serializable) rs.getObject(1), hint.getName())), uuid, rs.getLong(2));
			else
				return null;

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	protected Collection getChangedObjectsSinceLastReplication(PersistentClass type) {
		String primaryKeyColumnName = getObjectConfig().getPrimaryKeyColumnName(type);
		String tableName = type.getTable().getName();

		String sql = "SELECT " + primaryKeyColumnName + " FROM " + tableName
				+ " where " + Db4oColumns.VERSION.name + ">" + getLastReplicationVersion();

		Connection connection = _objectSession.connection();
		Statement st = Util.getStatement(connection);

		Set<HibernateObjectId> changedObjectIds = new HashSet();

		try {
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				Object object = rs.getObject(1);
				changedObjectIds.add(new HibernateObjectId((Serializable) object, type.getClassName()));
			}

			rs.close();
			st.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return loadObject(changedObjectIds);
	}

	protected void saveOrUpdateReplicaMetadata(ReplicationReference ref) {
		updateMetadata(ref, _objectSession.getIdentifier(ref.object()));
	}

	protected Uuid getUuid(Object obj) {
		return (Uuid) getUuidAndVersion(obj)[0];
	}

	protected Object[] getUuidAndVersion(Object obj) {
		String tableName = getObjectConfig().getTableName(obj.getClass());
		String pkColumn = getObjectConfig().getPrimaryKeyColumnName(obj);
		Serializable identifier = getSession().getIdentifier(obj);

		String sql = "SELECT "
				+ Db4oColumns.VERSION.name
				+ ", " + Db4oColumns.UUID_LONG_PART.name
				+ ", " + Db4oColumns.PROVIDER_ID.name
				+ " FROM " + tableName
				+ " where " + pkColumn + "=" + identifier;

		ResultSet rs = null;

		try {
			rs = getSession().connection().createStatement().executeQuery(sql);

			if (!rs.next()) throw new RuntimeException("record not found");

			Uuid uuid = new Uuid();
			uuid.setLongPart(rs.getLong(2));
			uuid.setProvider(getById(rs.getLong(3)));

			return new Object[]{uuid, rs.getLong(1)};
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			Util.closeResultSet(rs);
		}
	}

	protected void objectInserted(PostInsertEvent event) {
		Db4oUUID db4oUUID = new Db4oUUID(nextt(), _mySig.getBytes());
		ReplicationReference ref = new ReplicationReferenceImpl(event.getEntity(),
				db4oUUID, Util.getMaxVersion(getRefSession().connection()) + 1);
		updateMetadata(ref, event.getId());
	}

	private void updateMetadata(ReplicationReference ref, Serializable identifier) {
		String tableName = getObjectConfig().getTableName(ref.object().getClass());
		String pkColumn = getObjectConfig().getPrimaryKeyColumnName(ref.object());

		String sql = "UPDATE " + tableName + " SET " + Db4oColumns.VERSION.name + "=?"
				+ ", " + Db4oColumns.UUID_LONG_PART.name + "=?"
				+ ", " + Db4oColumns.PROVIDER_ID.name + "=?"
				+ " WHERE " + pkColumn + " =?";

		PreparedStatement ps = null;
		try {
			ps = _objectSession.connection().prepareStatement(sql);

			ps.setLong(1, ref.version());
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
			Util.closePreparedStatement(ps);
		}
	}

	protected void incrementObjectVersion(PostUpdateEvent event) {
		//TODO performance sucks, but this method is called when testing only.
		Object entity = event.getEntity();

		Connection con = getSession().connection();
		long newVer = Util.getMaxVersion(con) + 1;
		Shared.incrementObjectVersion(con, event.getId(), newVer,
				getObjectConfig().getTableName(entity.getClass()), getObjectConfig().getPrimaryKeyColumnName(entity));
	}
}