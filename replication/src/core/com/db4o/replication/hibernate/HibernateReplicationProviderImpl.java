package com.db4o.replication.hibernate;

import com.db4o.ObjectSet;
import com.db4o.ext.Db4oUUID;
import com.db4o.foundation.ObjectSetIteratorFacade;
import com.db4o.foundation.Visitor4;
import com.db4o.inside.replication.*;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.event.*;
import org.hibernate.mapping.PersistentClass;

import java.io.Serializable;
import java.sql.*;
import java.util.*;

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
public final class HibernateReplicationProviderImpl implements TestableReplicationProvider, HibernateReplicationProvider, TestableReplicationProviderInside {

	private static final String IS_NULL = " IS NULL ";

	/**
	 * The Hibernate Configuration, for getting metadata of mapped classes.
	 */
	private final Configuration cfg;

	/**
	 * The Hibernate facade to a JDBC Connection. The connection is created when
	 * this provider is instantiated. The connection terminates at  {@link
	 * #clearAllReferences()} .
	 */
	private final Session _session;

	/**
	 * The ReplicationProviderSignature of this  Hibernate-mapped database.
	 */
	private MySignature mySig;

	/**
	 * Allows the application to define units of work, while maintaining
	 * abstraction from the underlying transaction implementation (eg. JTA, JDBC).
	 * A transaction is associated with a Session and is usually instantiated by a
	 * call to Session.beginTransaction().
	 * <p/>
	 * A single session might span multiple transactions since the notion of a
	 * session (a conversation between the application and the datastore) is of
	 * coarser granularity than the notion of a transaction.
	 * <p/>
	 * However, it is intended that there be at most one uncommitted Transaction
	 * associated with a particular Session at any time.
	 */
	private Transaction _transaction;

	/**
	 * Hibernate mapped classes, excluding  {@link ReadonlyReplicationProviderSignature}
	 * and {@link ReplicationRecord}.
	 */
	private final Set mappedClasses;

	/**
	 * The Signature of the peer in the current Transaction.
	 */
	private PeerSignature peerSignature;

	private final Map _referencesByObject = new IdentityHashMap();

	/**
	 * true if this Database has never replicated with the  {@link
	 * #peerSignature}.
	 */
	private boolean newPeer;

	/**
	 * The ReplicationRecord of {@link #peerSignature}.
	 */
	private ReplicationRecord replicationRecord;

	/**
	 * Current transaction number = {@link #getLastReplicationVersion()} + 1. The
	 * minimum version number is 1, when this database is never replicated with
	 * other peers.
	 */
	private long currentVersion;

	/**
	 * The max(version numbers of all replication records).
	 */
	private long lastVersion;

	private final String _name;

	/**
	 * Objects which meta data not yet updated.
	 */
	private final Set dirtyRefs = new HashSet();
	protected SessionFactory _sessionFactory;

	public HibernateReplicationProviderImpl(Configuration cfg) {
		this(cfg, null, null);
	}

	//FIXME: The hibernate replication provider should generate a signature for new files
	//       and read the signature from the db on old files.

	public HibernateReplicationProviderImpl(Configuration cfg, String name, byte[] signature) {
		this.cfg = cfg;
		this.cfg.setProperty("hibernate.format_sql", "true");
		this.cfg.setProperty("hibernate.use_sql_comments", "true");
		this.cfg.setProperty("hibernate.cache.provider_class", "org.hibernate.cache.NoCacheProvider");
		this.cfg.setProperty("hibernate.cache.use_query_cache", "false");
		this.cfg.setProperty("hibernate.cache.use_second_level_cache", "false");
		this.cfg.setProperty("hibernate.cglib.use_reflection_optimizer", "true");
		this.cfg.setProperty("hibernate.connection.release_mode", "after_transaction");

		Util.addMetaDataClasses(cfg);

		new MetaDataTablesCreator(cfg).createTables();

		EventListeners eventListeners = this.cfg.getEventListeners();
		eventListeners.setFlushEventListeners(new FlushEventListener[]{new MyFlushEventListener()});
		eventListeners.setPostUpdateEventListeners(new PostUpdateEventListener[]{new MyPostUpdateEventListener()});
		_sessionFactory = this.cfg.buildSessionFactory();
		_session = _sessionFactory.openSession();
		_session.setFlushMode(FlushMode.ALWAYS);
		mappedClasses = getMappedClasses();
		_name = name;

		if (signature == null) {
			initMySignature();
		} else {
			setSignature(signature);
		}

		_transaction = _session.beginTransaction();

	}

	public final ReadonlyReplicationProviderSignature getSignature() {
		return mySig;
	}

	public final Object getMonitor() {
		return this;
	}

	public final void startReplicationTransaction(ReadonlyReplicationProviderSignature aPeerSignature) {
		byte[] peerSigBytes = aPeerSignature.getBytes();

		if (Arrays.equals(peerSigBytes, getSignature().getBytes()))
			throw new RuntimeException("peerSigBytes must not equal to my own sig");

		_transaction.commit();
		_transaction = _session.beginTransaction();

		PeerSignature existingPeerSignature = getPeerSignature(peerSigBytes);
		if (existingPeerSignature == null) {
			newPeer = true;
			this.peerSignature = new PeerSignature(peerSigBytes);
			_session.save(this.peerSignature);
			_session.flush();
			if (getPeerSignature(peerSigBytes) == null)
				throw new RuntimeException("Cannot insert existingPeerSignature");
		} else {
			newPeer = false;
			this.peerSignature = existingPeerSignature;
		}

		if (newPeer) {
			replicationRecord = new ReplicationRecord();
			replicationRecord.setPeerSignature(peerSignature);
		} else {
			replicationRecord = getRecord(this.peerSignature);
		}

		lastVersion = Util.getMaxVersion(_session.connection());
		currentVersion = lastVersion + 1;
	}

	public void storeReplicationRecord(long version) {
		replicationRecord.setVersion(version);
		_session.saveOrUpdate(replicationRecord);

		if (getRecord(peerSignature).getVersion() != version)
			throw new RuntimeException("The version numbers of persisted record does not match the parameter");
	}

	public final void commit(long raisedDatabaseVersion) {
		_transaction.commit();
		_transaction = _session.beginTransaction();
	}

	public void commit() {
		_session.flush();
		_transaction.commit();
		_transaction = _session.beginTransaction();
	}


	public final void rollbackReplication() {
		_transaction.rollback();
		_transaction = _session.beginTransaction();
		clearAllReferences();
		dirtyRefs.clear();
	}

	public final long getCurrentVersion() {
		return currentVersion;
	}

	public final long getLastReplicationVersion() {
		return lastVersion;
	}

	public final void storeReplica(Object obj) {
		ReplicationReference ref = getCachedReference(obj);

		if (ref == null) {
			throw new RuntimeException("Reference should always be available before storeReplica");
		}

		if (_session.contains(obj))
			update(obj);
		else
			_session.save(obj);

		dirtyRefs.add(ref);
	}

	public final void activate(Object object) {
		Hibernate.initialize(object);
	}

	public final ReplicationReference produceReference(Object obj) {

		ReplicationReference existing = getCachedReference(obj);
		if (existing != null) {
			return existing;
		}

		if (!_session.contains(obj)) {
			return null;
		}

		String tableName = Util.getTableName(cfg, obj.getClass());
		String pkColumn = Util.getPrimaryKeyColumnName(cfg, obj);
		Serializable identifier = _session.getIdentifier(obj);

		String sql = "SELECT "
				+ Db4oColumns.DB4O_VERSION
				+ ", " + Db4oColumns.DB4O_UUID_LONG_PART
				+ ", " + ReplicationProviderSignature.SIGNATURE_ID_COLUMN_NAME
				+ " FROM " + tableName
				+ " where " + pkColumn + "=" + identifier;

		ResultSet rs = null;

		try {
			rs = _session.connection().createStatement().executeQuery(sql);

			if (!rs.next())
				return null;

			ReplicationReference out;

			long longPart = rs.getLong(2);
			if (longPart < Constants.MIN_SEQ_NO) {
				Db4oUUID uuid = new Db4oUUID(generateUuidLongPartSeqNo(), getMySig().getBytes());
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
			Util.closeResultSet(rs);
		}
	}

	public ReplicationReference referenceNewObject(Object obj, ReplicationReference counterpartReference) {

		Db4oUUID uuid = counterpartReference.uuid();
		long version = counterpartReference.version();

		return createReference(obj, uuid, version);
	}

	public final ReplicationReference produceReferenceByUUID(final Db4oUUID uuid, Class hint) {
		if (uuid == null) {
			return null;
		}
		_session.flush();
		ReadonlyReplicationProviderSignature signature = getProviderSignature(uuid.getSignaturePart());

		final long sigId;

		if (signature == null) return null;
		else sigId = signature.getId();

		String alias = "whatever";

		String tableName = Util.getTableName(cfg, hint);

		String sql = "SELECT {" + alias + ".*} FROM " + tableName + " " + alias
				+ " where " + Db4oColumns.DB4O_UUID_LONG_PART + "=" + uuid.getLongPart()
				+ " AND " + ReplicationProviderSignature.SIGNATURE_ID_COLUMN_NAME + "=" + sigId;
		SQLQuery sqlQuery = _session.createSQLQuery(sql);
		sqlQuery.addEntity(alias, hint);

		final List results = sqlQuery.list();

		final int rowCount = results.size();

		if (rowCount == 0) {
			return null;
		} else if (rowCount == 1) {
			final Object obj = results.get(0);

			ReplicationReference existing = getCachedReference(obj);
			if (existing != null) return existing;

			long version = Util.getVersion(cfg, _session, obj);
			return createReference(obj, uuid, version);
		} else {
			throw new RuntimeException("The object may either be found or not, it will never find more than one objects");
		}
	}

	public final void clearAllReferences() {
		_referencesByObject.clear();
	}

	public final ObjectSet objectsChangedSinceLastReplication() {
		_session.flush();
		Set out = new HashSet();

		Set queriedTables = new HashSet();

		for (Iterator iterator = mappedClasses.iterator(); iterator.hasNext();) {
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
		_session.flush();
		Set out = new HashSet();
		PersistentClass persistentClass = cfg.getClassMapping(clazz.getName());
		if (persistentClass != null) {
			out.addAll(getNewObjectsSinceLastReplication(persistentClass));
			out.addAll(getChangedObjectsSinceLastReplication(persistentClass));
		}
		return new ObjectSetIteratorFacade(out.iterator());
	}

	public final boolean hasReplicationReferenceAlready(Object obj) {
		return getCachedReference(obj) != null;
	}

	public final void visitCachedReferences(Visitor4 visitor) {
		Iterator i = _referencesByObject.values().iterator();
		while (i.hasNext()) {
			visitor.visit(i.next());
		}
	}

	public final String getModifiedObjectCriterion() {
		return Db4oColumns.DB4O_VERSION + " > " + getLastReplicationVersion();
	}

	private void setSignature(byte[] b) {
		//Idempotent
		if (mySig != null) return;
		final Criteria criteria = _session.createCriteria(MySignature.class);
		final List firstResult = criteria.list();

		if (firstResult.size() == 1)
			_session.delete(firstResult.get(0));
		else if (firstResult.size() > 1)
			throw new RuntimeException("Number of MySignature should be either 0 or 1");

		mySig = new MySignature(b);
		_session.save(mySig);
		_session.flush();
	}

	public final ObjectSet getStoredObjects(Class aClass) {
		_session.flush();
		return new ObjectSetIteratorFacade(_session.createCriteria(aClass).list().iterator());
	}

	public final void storeNew(Object object) {
		_session.save(object);
	}

	public final void update(Object o) {
		_session.update(o);
		_session.flush();
	}

	public final String getName() {
		return _name;
	}


	private Collection getNewObjectsSinceLastReplication(PersistentClass type) {
		String alias = "whatever";

		String tableName = type.getTable().getName();
		//dumpTable(tableName);
		String sql = "SELECT {" + alias + ".*} FROM " + tableName + " " + alias
				+ " where " + Db4oColumns.DB4O_UUID_LONG_PART + IS_NULL
				//+ " AND " + Db4oColumns.DB4O_VERSION + IS_NULL
				+ " AND " + ReplicationProviderSignature.SIGNATURE_ID_COLUMN_NAME + IS_NULL;
		//+ " AND class=" + type
		SQLQuery sqlQuery = _session.createSQLQuery(sql);
		sqlQuery.addEntity(alias, type.getMappedClass());

		List list = sqlQuery.list();
		generateReplicationMetaData(list);
		return list;
	}

	private Collection getChangedObjectsSinceLastReplication(PersistentClass type) {
		String alias = "whatever";

		String tableName = type.getTable().getName();
		String sql = "SELECT {" + alias + ".*} FROM " + tableName + " " + alias
				+ " where " + Db4oColumns.DB4O_VERSION + ">" + lastVersion;
		//+ " where " + Db4oColumns.DB4O_VERSION + ">" + lastVersion + " OR " + Db4oColumns.DB4O_VERSION + "=0";

		SQLQuery sqlQuery = _session.createSQLQuery(sql);
		sqlQuery.addEntity(alias, type.getMappedClass());

		return sqlQuery.list();
	}

	private ReadonlyReplicationProviderSignature getProviderSignature(byte[] signaturePart) {
		final List exisitingSigs = _session.createCriteria(ReadonlyReplicationProviderSignature.class)
				.add(Restrictions.eq(ReplicationProviderSignature.SIGNATURE_BYTE_ARRAY_COLUMN_NAME, signaturePart))
				.list();
		if (exisitingSigs.size() == 1)
			return (ReadonlyReplicationProviderSignature) exisitingSigs.get(0);
		else if (exisitingSigs.size() == 0) return null;
		else
			throw new RuntimeException("result size = " + exisitingSigs.size() + ". It should be either 1 or 0");
	}

	private void initMySignature() {
		final Criteria criteria = _session.createCriteria(MySignature.class);

		final List firstResult = criteria.list();
		final int mySigCount = firstResult.size();

		if (mySigCount < 1) {
			mySig = MySignature.generateSignature();
			_session.save(mySig);
		} else if (mySigCount == 1) {
			mySig = (MySignature) firstResult.get(0);
		} else {
			throw new RuntimeException("Number of MySignature should be exactly 1, but i got " + mySigCount);
		}
	}

	protected static void sleep(int i, String s) {
		System.out.println(s);
		try {
			Thread.sleep(i * 1000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	private PeerSignature getPeerSignature(byte[] bytes) {
		final List exisitingSigs = _session.createCriteria(PeerSignature.class)
				.add(Restrictions.eq(ReplicationProviderSignature.SIGNATURE_BYTE_ARRAY_COLUMN_NAME, bytes))
				.list();

		if (exisitingSigs.size() == 1)
			return (PeerSignature) exisitingSigs.get(0);
		else if (exisitingSigs.size() == 0) return null;
		else
			throw new RuntimeException("result size = " + exisitingSigs.size() + ". It should be either 1 or 0");
	}

	protected ReplicationRecord getRecord(PeerSignature peerSignature) {
		Criteria criteria = _session.createCriteria(ReplicationRecord.class).createCriteria("peerSignature").add(Restrictions.eq("id", peerSignature.getId()));

		final List exisitingRecords = criteria.list();
		int count = exisitingRecords.size();

		if (count == 0)
			throw new RuntimeException("Record not found. Hibernate was unable to persist the record in the last replication round");
		else if (count > 1)
			throw new RuntimeException("Only one Record should exist for this peer");
		else
			return (ReplicationRecord) exisitingRecords.get(0);
	}

	private void generateReplicationMetaData(Collection newObjects) {
		for (Iterator iterator = newObjects.iterator(); iterator.hasNext();) {
			Object o = iterator.next();
			Db4oUUID uuid = new Db4oUUID(generateUuidLongPartSeqNo(), getMySig().getBytes());
			ReplicationReferenceImpl ref = new ReplicationReferenceImpl(o, uuid, currentVersion);
			storeReplicationMetaData(ref);
		}
	}

	private MySignature getMySig() {
		if (mySig == null)
			initMySignature();
		return mySig;
	}


	private long generateUuidLongPartSeqNo() {

		Connection connection = _session.connection();
		Statement st = null;
		ResultSet rs = null;

		try {
			st = connection.createStatement();
			rs = st.executeQuery("SELECT count(*) FROM " + Constants.UUID_LONG_PART_SEQUENCE);
			rs.next();

			long rowCount = rs.getLong(1);
			if (rowCount == 0) {
				insertSeqNo(st);
			} else if (rowCount > 1) {
				st.executeQuery("DELETE FROM " + Constants.UUID_LONG_PART_SEQUENCE);
				insertSeqNo(st);
			}

			ResultSet rs2 = st.executeQuery("SELECT " + Constants.CURRENT_SEQ_NO + " FROM " + Constants.UUID_LONG_PART_SEQUENCE);
			rs2.next();
			long currSeqNo = rs2.getLong(1);
			long raised = currSeqNo + 1;

			st.execute("UPDATE " + Constants.UUID_LONG_PART_SEQUENCE + " SET " + Constants.CURRENT_SEQ_NO + " = " + raised);
			st.close();

			return currSeqNo;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			Util.closeResultSet(rs);
			Util.closeStatement(st);
		}

	}

	private static void insertSeqNo(Statement st) throws SQLException {
		st.executeUpdate("INSERT INTO " + Constants.UUID_LONG_PART_SEQUENCE + " ( " + Constants.CURRENT_SEQ_NO + " ) VALUES ( " + Constants.MIN_SEQ_NO + " ) ");
	}

	private ReplicationReference createReference(Object obj, Db4oUUID uuid, long version) {
		ReplicationReference result = new ReplicationReferenceImpl(obj, uuid, version);
		_referencesByObject.put(obj, result);
		return result;
	}

	private ReadonlyReplicationProviderSignature getById(long sigId) {
		return (ReadonlyReplicationProviderSignature) _session.get(ReplicationProviderSignature.class, new Long(sigId));
	}

	private void storeReplicationMetaData(ReplicationReference ref) {
		String tableName = Util.getTableName(cfg, ref.object().getClass());
		String pkColumn = Util.getPrimaryKeyColumnName(cfg, ref.object());
		Serializable identifier = _session.getIdentifier(ref.object());

		String sql = "UPDATE " + tableName + " SET " + Db4oColumns.DB4O_VERSION + "=?"
				+ ", " + Db4oColumns.DB4O_UUID_LONG_PART + "=?"
				+ ", " + ReplicationProviderSignature.SIGNATURE_ID_COLUMN_NAME + "=?"
				+ " WHERE " + pkColumn + " =?";

		PreparedStatement ps = null;
		try {
			ps = _session.connection().prepareStatement(sql);

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
			Util.closePreparedStatement(ps);
		}
	}

	private ReplicationReference getCachedReference(Object obj) {
		return (ReplicationReference) _referencesByObject.get(obj);
	}

	private static String flattenBytes(byte[] b) {
		String out = "";
		for (int i = 0; i < b.length; i++) {
			out += ", " + b[i];
		}
		return out;
	}

	private Set getMappedClasses() {
		Set out = new HashSet();
		Iterator classMappings = cfg.getClassMappings();
		while (classMappings.hasNext()) {
			PersistentClass persistentClass = (PersistentClass) classMappings.next();
			Class claxx = persistentClass.getMappedClass();

			if (claxx == ReplicationRecord.class || claxx == ReplicationProviderSignature.class || claxx == PeerSignature.class || claxx == MySignature.class)
				continue;

			out.add(persistentClass);
		}


		return out;
	}

	public final String toString() {
		return "name = " + _name + ", sig = " + flattenBytes(getMySig().getBytes());
	}

	final class MyFlushEventListener implements FlushEventListener {
		public final void onFlush(FlushEvent event) throws HibernateException {
			for (Iterator iterator = dirtyRefs.iterator(); iterator.hasNext();) {
				ReplicationReference ref = (ReplicationReference) iterator.next();
				storeReplicationMetaData(ref);
			}
			dirtyRefs.clear();
		}
	}

	final class MyPostUpdateEventListener implements PostUpdateEventListener {
		public final void onPostUpdate(PostUpdateEvent event) {
			synchronized (HibernateReplicationProviderImpl.this) {
				Object entity = event.getEntity();

				if (Util.skip(entity)) return;

				//TODO performance sucks, but this method is called when testing only.
				long newVer = Util.getMaxVersion(_session.connection()) + 1;
				Util.incrementObjectVersion(_session.connection(), event.getId(), newVer,
						Util.getTableName(cfg, entity.getClass()), Util.getPrimaryKeyColumnName(cfg, entity));
			}
		}
	}

	public void closeIfOpened() {
		_session.close();
		_sessionFactory.close();
	}

	public Session getSession() {
		return _session;
	}

	public void delete(Class clazz) {
		String className = clazz.getName();
		_session.createQuery("delete from " + className).executeUpdate();
	}


}