package com.db4o.replication.hibernate.impl.ref_as_columns;

import com.db4o.ext.Db4oUUID;
import com.db4o.inside.replication.ReplicationReference;
import com.db4o.replication.hibernate.cfg.ObjectConfig;
import com.db4o.replication.hibernate.cfg.RefConfig;
import com.db4o.replication.hibernate.impl.AbstractReplicationProvider;
import com.db4o.replication.hibernate.impl.HibernateObjectId;
import com.db4o.replication.hibernate.impl.Util;
import com.db4o.replication.hibernate.metadata.ReplicationProviderSignature;
import com.db4o.replication.hibernate.metadata.Uuid;
import org.hibernate.FlushMode;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.PostInsertEvent;
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
// --------------------------- CONSTRUCTORS ---------------------------

	public RefAsColumnsReplicationProvider(Configuration cfg) {
		this(cfg, null);
	}

	public RefAsColumnsReplicationProvider(Configuration cfg, String name) {
		_name = name;

		_refCfg = RefAsColumnsConfiguration.produce(cfg);

		Util.initUuidLongPartSequence(cfg);
		lifeCycleEventsListener = new MyObjectLifeCycleEventsListener();
		lifeCycleEventsListener.configure(cfg);

		_objectConfig = new ObjectConfig(cfg);

		new RefAsColumnsTablesCreator(getRefCfg()).createTables();

		initEventListeners();

		_sessionFactory = this.getRefCfg().getConfiguration().buildSessionFactory();
		_session = _sessionFactory.openSession();
		_session.setFlushMode(FlushMode.COMMIT);
		_transaction = _session.beginTransaction();

		init();

		lifeCycleEventsListener.install(getSession(), cfg);

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
		String tableName = getObjectConfig().getTableName(ref.object().getClass());
		String pkColumn = getObjectConfig().getPrimaryKeyColumnName(ref.object());

		long sigId = getProviderSignature(ref.uuid().getSignaturePart()).getId();

		Shared.updateMetadata(ref.version(), ref.uuid().getLongPart(), _session.getIdentifier(ref.object()), tableName, pkColumn, getSession(), sigId);
	}

	public final String getModifiedObjectCriterion() {
		ensureReplicationActive();

		return Db4oColumns.VERSION.name + " > " + getLastReplicationVersion();
	}

	private Object[] getUuidAndVersion(Object obj) {
		String tableName = getObjectConfig().getTableName(obj.getClass());
		String pkColumn = getObjectConfig().getPrimaryKeyColumnName(obj);
		Serializable identifier = getSession().getIdentifier(obj);

		return Shared.getUuidAndVersion(tableName, pkColumn, identifier, getSession());
	}

// -------------------------- INNER CLASSES --------------------------

	final class MyObjectLifeCycleEventsListener extends RefAsColumnsObjectLifeCycleEventsListener {
		public final void onPostInsert(PostInsertEvent event) {
			if (!isReplicationActive())
				super.onPostInsert(event);
		}

		protected final void ObjectUpdated(Object obj, Serializable id) {
			if (!isReplicationActive())
				super.ObjectUpdated(obj, id);
		}
	}
}