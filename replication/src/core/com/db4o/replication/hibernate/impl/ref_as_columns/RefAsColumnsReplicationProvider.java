package com.db4o.replication.hibernate.impl.ref_as_columns;

import com.db4o.ext.Db4oUUID;
import com.db4o.inside.replication.ReplicationReference;
import com.db4o.replication.hibernate.cfg.ObjectConfig;
import com.db4o.replication.hibernate.cfg.RefConfig;
import com.db4o.replication.hibernate.impl.AbstractReplicationProvider;
import com.db4o.replication.hibernate.impl.HibernateObjectId;
import com.db4o.replication.hibernate.impl.ReplicationReferenceImpl;
import com.db4o.replication.hibernate.impl.Util;
import com.db4o.replication.hibernate.metadata.ReplicationProviderSignature;
import com.db4o.replication.hibernate.metadata.Uuid;
import org.hibernate.FlushMode;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostUpdateEvent;
import org.hibernate.mapping.PersistentClass;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class RefAsColumnsReplicationProvider extends AbstractReplicationProvider {
// ------------------------------ FIELDS ------------------------------

	protected static final String IS_NULL = " IS NULL ";

// --------------------------- CONSTRUCTORS ---------------------------

	public RefAsColumnsReplicationProvider(Configuration cfg) {
		this(cfg, null);
	}

	public RefAsColumnsReplicationProvider(Configuration cfg, String name) {
		_name = name;

		_refCfg = RefAsColumnsConfiguration.produce(cfg);

		_objectConfig = new ObjectConfig(cfg);

		new RefAsColumnsTablesCreator(getRefCfg()).createTables();

		initEventListeners();

		_sessionFactory = this.getRefCfg().getConfiguration().buildSessionFactory();
		_session = _sessionFactory.openSession();
		_session.setFlushMode(FlushMode.COMMIT);
		_transaction = _session.beginTransaction();

		init();
		_alive = true;
	}

	protected Collection getChangedObjectsSinceLastReplication(PersistentClass type) {
		String primaryKeyColumnName = getObjectConfig().getPrimaryKeyColumnName(type);
		String tableName = type.getTable().getName();

		String sql = "SELECT " + primaryKeyColumnName + " FROM " + tableName
				+ " where " + Db4oColumns.VERSION.name + ">" + getLastReplicationVersion();

		Connection connection = _session.connection();
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

	protected RefConfig getRefConfig() {
		return getRefCfg();
	}

	protected Uuid getUuid(Object obj) {
		return (Uuid) getUuidAndVersion(obj)[0];
	}

	protected Object[] getUuidAndVersion(Object obj) {
		String tableName = getObjectConfig().getTableName(obj.getClass());
		String pkColumn = getObjectConfig().getPrimaryKeyColumnName(obj);
		Serializable identifier = getSession().getIdentifier(obj);

		return Shared.getUuidAndVersion(tableName, pkColumn, identifier, getSession());
	}

	protected void incrementObjectVersion(PostUpdateEvent event) {
		//TODO performance sucks, but this method is called when testing only.
		Object entity = event.getEntity();

		Connection con = getSession().connection();
		long newVer = Util.getMaxVersion(con) + 1;
		Shared.incrementObjectVersion(con, event.getId(), newVer,
				getObjectConfig().getTableName(entity.getClass()), getObjectConfig().getPrimaryKeyColumnName(entity));
	}

	protected void objectInserted(PostInsertEvent event) {
		Db4oUUID db4oUUID = translate(uuidGenerator.next());
		ReplicationReference ref = new ReplicationReferenceImpl(event.getEntity(),
				db4oUUID, Util.getMaxVersion(getSession().connection()) + 1);
		updateMetadata(ref, event.getId());
	}

	protected ReplicationReference produceObjectReference(Object obj) {
		if (!getSession().contains(obj)) return null;

		Object[] uuidAndVersion = getUuidAndVersion(obj);
		Uuid uuid = (Uuid) uuidAndVersion[0];
		long version = (Long) uuidAndVersion[1];

		return _objRefs.put(obj, Util.translate(uuid), version);
	}

	protected ReplicationReference produceObjectReferenceByUUID(Db4oUUID uuid, Class hint) {
		_session.flush();

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
			ResultSet rs = _session.connection().createStatement().executeQuery(sql);

			if (rs.next())
				return _objRefs.put(loadObject(new HibernateObjectId(
						(Serializable) rs.getObject(1), hint.getName())), uuid, rs.getLong(2));
			else
				return null;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	protected void saveOrUpdateReplicaMetadata(ReplicationReference ref) {
		updateMetadata(ref, _session.getIdentifier(ref.object()));
	}

	public final String getModifiedObjectCriterion() {
		ensureReplicationActive();

		return Db4oColumns.VERSION.name + " > " + getLastReplicationVersion();
	}

	private void updateMetadata(ReplicationReference ref, Serializable identifier) {
		String tableName = getObjectConfig().getTableName(ref.object().getClass());
		String pkColumn = getObjectConfig().getPrimaryKeyColumnName(ref.object());

		long sigId = getProviderSignature(ref.uuid().getSignaturePart()).getId();

		Shared.updateMetadata(ref.version(), ref.uuid().getLongPart(), identifier, tableName, pkColumn, getSession(), sigId);
	}
}