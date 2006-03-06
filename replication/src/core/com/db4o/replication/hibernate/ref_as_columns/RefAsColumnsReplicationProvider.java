package com.db4o.replication.hibernate.ref_as_columns;

import com.db4o.ObjectSet;
import com.db4o.ext.Db4oUUID;
import com.db4o.foundation.ObjectSetIteratorFacade;
import com.db4o.foundation.Visitor4;
import com.db4o.inside.replication.ReadonlyReplicationProviderSignature;
import com.db4o.inside.replication.ReplicationReference;
import com.db4o.inside.replication.ReplicationReferenceImpl;
import com.db4o.replication.hibernate.AbstractReplicationProvider;
import com.db4o.replication.hibernate.RefConfig;
import com.db4o.replication.hibernate.common.ChangedObjectId;
import com.db4o.replication.hibernate.common.Common;
import com.db4o.replication.hibernate.common.ReplicationRecord;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.EventListeners;
import org.hibernate.event.FlushEvent;
import org.hibernate.event.FlushEventListener;
import org.hibernate.event.PostUpdateEvent;
import org.hibernate.event.PostUpdateEventListener;
import org.hibernate.mapping.PersistentClass;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
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

	protected RefConfig _refCfg;

	protected Session _session;

	protected Transaction _transaction;

	protected SessionFactory _sessionFactory;

	{myFlushEventListener = new MyFlushEventListener();}

	{myPostUpdateEventListener = new MyPostUpdateEventListener();}

	public RefAsColumnsReplicationProvider(Configuration cfg) {
		this(cfg, null);
	}

	public RefAsColumnsReplicationProvider(Configuration cfg, String name) {
		_name = name;
		_refCfg = RefAsColumnsConfiguration.produce(cfg);

		new MetaDataTablesCreator(_refCfg).execute();

		EventListeners eventListeners = this._refCfg.getConfiguration().getEventListeners();

		eventListeners.setFlushEventListeners(createFlushEventListeners(eventListeners.getFlushEventListeners()));
		eventListeners.setPostUpdateEventListeners(createPostUpdateListeners(eventListeners.getPostUpdateEventListeners()));

		_sessionFactory = this._refCfg.getConfiguration().buildSessionFactory();
		_session = _sessionFactory.openSession();
		_session.setFlushMode(FlushMode.ALWAYS);
		_transaction = _session.beginTransaction();

		init();
	}

	public final void startReplicationTransaction(ReadonlyReplicationProviderSignature aPeerSignature) {
		ensureReplicationInActive();

		byte[] peerSigBytes = aPeerSignature.getBytes();

		if (Arrays.equals(peerSigBytes, getSignature().getBytes()))
			throw new RuntimeException("peerSigBytes must not equal to my own sig");

		_transaction.commit();
		_transaction = _session.beginTransaction();

		initPeerSigAndRecord(peerSigBytes, _session);

		_currentVersion = Shared.getMaxVersion(_session.connection()) + 1;
		_inReplication = true;
	}


	public final void storeReplica(Object obj) {
		ensureReplicationActive();

		//Hibernate does not treat Collection as 1st class object, so storing a Collection is no-op
		if (_collectionHandler.canHandle(obj)) return;

		ReplicationReference ref = getCachedReference(obj);
		if (ref == null) throw new RuntimeException("Reference should always be available before storeReplica");

		_uuidsReplicatedInThisSession.add(ref.uuid());

		_session.saveOrUpdate(obj);
		_dirtyRefs.add(ref);
	}

	public final ObjectSet objectsChangedSinceLastReplication() {
		ensureReplicationActive();

		_session.flush();

		Set out = new HashSet();

		Set queriedTables = new HashSet();

		for (Iterator iterator = _mappedClasses.iterator(); iterator.hasNext();) {
			PersistentClass persistentClass = (PersistentClass) iterator.next();
			String tableName = persistentClass.getTable().getName();

			if (!queriedTables.contains(tableName)) {
				queriedTables.add(tableName);

				//Case 1 - Objects inserted to Db since last replication with any peers.
				out.addAll(getNewObjectsSinceLastReplication(persistentClass));

				//Case 2 - Objects updated since last replication with any peers.
				out.addAll(getChangedObjectsSinceLastReplication(persistentClass));
			}
		}
		return new ObjectSetIteratorFacade(out.iterator());
	}

	public final ObjectSet objectsChangedSinceLastReplication(Class clazz) {
		ensureReplicationActive();

		_session.flush();
		Set out = new HashSet();
		PersistentClass persistentClass = _refCfg.getConfiguration().getClassMapping(clazz.getName());
		if (persistentClass != null) {
			out.addAll(getNewObjectsSinceLastReplication(persistentClass));
			out.addAll(getChangedObjectsSinceLastReplication(persistentClass));
		}
		return new ObjectSetIteratorFacade(out.iterator());
	}

	public final void visitCachedReferences(Visitor4 visitor) {
		ensureReplicationActive();

		Iterator i = _referencesByObject.values().iterator();
		while (i.hasNext()) {
			visitor.visit(i.next());
		}
	}

	public void closeIfOpened() {
		_session.close();
		_sessionFactory.close();
	}

	public final String getModifiedObjectCriterion() {
		ensureReplicationActive();

		return Db4oColumns.VERSION.name + " > " + getLastReplicationVersion();
	}

	public void delete(Class clazz) {
		String className = clazz.getName();
		_session.createQuery("delete from " + className).executeUpdate();
	}

	public void commit() {
		_session.flush();
		_transaction.commit();
		_transaction = _session.beginTransaction();
	}

	public final ObjectSet getStoredObjects(Class aClass) {
		if (_collectionHandler.canHandle(aClass))
			throw new IllegalArgumentException("Hibernate does not query by Collection");

		return new ObjectSetIteratorFacade(_session.createCriteria(aClass).list().iterator());
	}

	public final void storeNew(Object object) {
		_session.save(object);
	}

	public final void update(Object obj) {
		if (_collectionHandler.canHandle(obj))
			return;
		else
			_session.update(obj);

		_session.flush();
	}

	public final String getName() {
		return _name;
	}

	protected ReplicationReference produceObjectReference(Object obj) {
		//System.out.println("produceObjectReference() obj = " + obj);
		if (!_session.contains(obj)) return null;

		String tableName = _refCfg.getTableName(obj.getClass());
		String pkColumn = _refCfg.getPrimaryKeyColumnName(obj);
		Serializable identifier = _session.getIdentifier(obj);

		String sql = "SELECT "
				+ Db4oColumns.VERSION.name
				+ ", " + Db4oColumns.UUID_LONG_PART.name
				+ ", " + Db4oColumns.PROVIDER_ID.name
				+ " FROM " + tableName
				+ " where " + pkColumn + "=" + identifier;

		ResultSet rs = null;

		try {
			rs = _session.connection().createStatement().executeQuery(sql);

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
				ReadonlyReplicationProviderSignature owner = getById(rs.getLong(3), _session);
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
		return _session;
	}

	protected ReplicationReference produceObjectReferenceByUUID(Db4oUUID uuid, Class hint) {
		_session.flush();
		ReadonlyReplicationProviderSignature signature = getProviderSignature(uuid.getSignaturePart(), _session);

		final long sigId;

		if (signature == null) return null;
		else sigId = signature.getId();

		String alias = "whatever";

		String tableName = _refCfg.getTableName(hint);

		String sql = "SELECT {" + alias + ".*} FROM " + tableName + " " + alias
				+ " where " + Db4oColumns.UUID_LONG_PART.name + "=" + uuid.getLongPart()
				+ " AND " + Db4oColumns.PROVIDER_ID.name + "=" + sigId;
		SQLQuery sqlQuery = _session.createSQLQuery(sql);
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

			long version = Shared.getVersion(_refCfg.getConfiguration(), _session, obj);
			out = createReference(obj, uuid, version);
		} else {
			throw new RuntimeException("The object may either be found or not, it will never find more than one objects");
		}

		return out;
	}

	protected Collection getNewObjectsSinceLastReplication(PersistentClass type) {
		String primaryKeyColumnName = _refCfg.getPrimaryKeyColumnName(type);
		String tableName = type.getTable().getName();

		String sql = "SELECT " + primaryKeyColumnName + " FROM " + tableName
				+ " where " + Db4oColumns.UUID_LONG_PART.name + IS_NULL
				+ " AND " + Db4oColumns.PROVIDER_ID.name + IS_NULL;

		Collection out = loadObj(getChangedObjectIds(_session, sql, type), _session);

		generateReplicationMetaData(out);

		return out;
	}

	protected Collection getChangedObjectsSinceLastReplication(PersistentClass type) {
		String primaryKeyColumnName = _refCfg.getPrimaryKeyColumnName(type);
		String tableName = type.getTable().getName();

		String sql = "SELECT " + primaryKeyColumnName + " FROM " + tableName
				+ " where " + Db4oColumns.VERSION.name + ">" + getLastReplicationVersion();

		return loadObj(getChangedObjectIds(_session, sql, type), _session);
	}

	protected static Collection<ChangedObjectId> getChangedObjectIds(Session sess, String sql, PersistentClass type) {
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
		String tableName = _refCfg.getTableName(ref.object().getClass());
		String pkColumn = _refCfg.getPrimaryKeyColumnName(ref.object());
		Serializable identifier = _session.getIdentifier(ref.object());

		String sql = "UPDATE " + tableName + " SET " + Db4oColumns.VERSION.name + "=?"
				+ ", " + Db4oColumns.UUID_LONG_PART.name + "=?"
				+ ", " + Db4oColumns.PROVIDER_ID.name + "=?"
				+ " WHERE " + pkColumn + " =?";

		PreparedStatement ps = null;
		try {
			ps = _session.connection().prepareStatement(sql);

			long refVer = ref.version();
			ps.setLong(1, refVer);
			ps.setLong(2, ref.uuid().getLongPart());
			ps.setLong(3, getProviderSignature(ref.uuid().getSignaturePart(), _session).getId());
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

	protected void initMappedClasses() {
		_mappedClasses = new HashSet();

		Iterator classMappings = _refCfg.getConfiguration().getClassMappings();
		while (classMappings.hasNext()) {
			PersistentClass persistentClass = (PersistentClass) classMappings.next();
			Class claxx = persistentClass.getMappedClass();

			if (Common.skip(claxx))
				continue;

			_mappedClasses.add(persistentClass);
		}
	}

	public Session getSession() {
		return _session;
	}

	final class MyFlushEventListener implements FlushEventListener {
		public final void onFlush(FlushEvent event) throws HibernateException {
			for (Iterator iterator = _dirtyRefs.iterator(); iterator.hasNext();) {
				ReplicationReference ref = (ReplicationReference) iterator.next();
				storeReplicationMetaData(ref);
			}
			_dirtyRefs.clear();
		}
	}

	final class MyPostUpdateEventListener implements PostUpdateEventListener {
		public final void onPostUpdate(PostUpdateEvent event) {
			synchronized (RefAsColumnsReplicationProvider.this) {
				Object entity = event.getEntity();

				if (Common.skip(entity)) return;

				//TODO performance sucks, but this method is called when testing only.
				long newVer = Shared.getMaxVersion(_session.connection()) + 1;
				Shared.incrementObjectVersion(_session.connection(), event.getId(), newVer,
						_refCfg.getTableName(entity.getClass()), _refCfg.getPrimaryKeyColumnName(entity));
			}
		}
	}

	public synchronized final void rollbackReplication() {
		ensureReplicationActive();

		_transaction.rollback();
		_transaction = _session.beginTransaction();
		clearAllReferences();
		_dirtyRefs.clear();
		_uuidsReplicatedInThisSession.clear();
		_inReplication = false;
	}
}