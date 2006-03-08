package com.db4o.replication.hibernate.ref_as_table;

import com.db4o.ext.Db4oUUID;
import com.db4o.inside.replication.ReadonlyReplicationProviderSignature;
import com.db4o.replication.hibernate.AbstractReplicationProvider;
import com.db4o.replication.hibernate.ObjectConfig;
import com.db4o.replication.hibernate.RefConfig;
import com.db4o.replication.hibernate.common.ChangedObjectId;
import com.db4o.replication.hibernate.common.ReplicationProviderSignature;
import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.event.PostUpdateEvent;
import org.hibernate.mapping.PersistentClass;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class RefAsTableReplicationProvider extends AbstractReplicationProvider {
	private SessionFactory _refSessionFactory;

	private Session _refSession;

	private Transaction _refTransaction;

	public RefAsTableReplicationProvider(Configuration cfg) {
		this(cfg, cfg, null);
	}

	public RefAsTableReplicationProvider(Configuration objCfg, Configuration refCfg) {
		this(objCfg, refCfg, null);
	}

	public RefAsTableReplicationProvider(Configuration objCfg, Configuration refCfg, String name) {
		_name = name;

		_refCfg = RefAsTableConfiguration.produce(refCfg);

		_objectConfig = new ObjectConfig(objCfg);

		new RefAsTableTablesCreator(getRefCfg()).execute();

		initEventListeners();

		_objectSessionFactory = getObjectConfig().getConfiguration().buildSessionFactory();
		_objectSession = _objectSessionFactory.openSession();
		_objectSession.setFlushMode(FlushMode.ALWAYS);
		_objectTransaction = _objectSession.beginTransaction();

		_refSessionFactory = getRefConfig().getConfiguration().buildSessionFactory();
		_refSession = _refSessionFactory.openSession();
		_refSession.setFlushMode(FlushMode.ALWAYS);

		init();
	}

	public void startReplicationTransaction(ReadonlyReplicationProviderSignature aPeerSignature) {
		super.startReplicationTransaction(aPeerSignature);
		_refTransaction = _refSession.beginTransaction();
	}

	public void rollbackReplication() {
		super.rollbackReplication();
		_refTransaction.rollback();
	}

	public synchronized void commitReplicationTransaction(long raisedDatabaseVersion) {
		super.commitReplicationTransaction(raisedDatabaseVersion);
		_refTransaction.commit();
	}

	protected Session getRefSession() {
		return _refSession;
	}

	protected com.db4o.inside.replication.ReplicationReference produceObjectReferenceByUUID(Db4oUUID uuid, Class hint) {
		Criteria criteria = getRefSession().createCriteria(ReplicationReference.class);
		criteria.add(Restrictions.eq("uuidLongPart", new Long(uuid.getLongPart())));
		criteria.add(Restrictions.eq("className", hint.getName()));
		criteria.createCriteria("provider").add(Restrictions.eq("bytes", uuid.getSignaturePart()));

		final List exisitings = criteria.list();
		int count = exisitings.size();

		if (count == 0)
			return null;
		else if (count > 1)
			throw new RuntimeException("Only one ReplicationReference should exist");
		else {
			ReplicationReference exist = (ReplicationReference) exisitings.get(0);
			Object obj = getObjectSession().load(exist.getClassName(), exist.getObjectId());

			return createReference(obj, uuid, exist.getVersion());
		}
	}

	protected com.db4o.inside.replication.ReplicationReference produceObjectReference(Object obj) {
		if (!getObjectSession().contains(obj)) return null;

		Serializable id = getObjectSession().getIdentifier(obj);

		ensureLong(id);

		Criteria criteria = getRefSession().createCriteria(ReplicationReference.class);
		criteria.add(Restrictions.eq("objectId", id));
		criteria.add(Restrictions.eq("className", obj.getClass().getName()));

		final List exisitings = criteria.list();
		int count = exisitings.size();

		if (count == 0)
			return null;
		else if (count > 1)
			throw new RuntimeException("Only one ReplicationReference should exist");
		else {
			ReplicationReference exist = (ReplicationReference) exisitings.get(0);

			Db4oUUID uuid = new Db4oUUID(exist.getUuidLongPart(), exist.getProvider().getBytes());

			return createReference(obj, uuid, exist.getVersion());
		}
	}

	protected void ensureLong(Serializable id) {
		if (!(id instanceof Long))
			throw new IllegalStateException("You must use 'long' as the type of the hibernate id");
	}

	protected void storeReplicationMetaData(com.db4o.inside.replication.ReplicationReference in) {
		long id = (Long) getObjectSession().getIdentifier(in.object());
		ReplicationProviderSignature provider = getProviderSignature(in.uuid().getSignaturePart());

		ReplicationReference ref = new ReplicationReference();
		ref.setClassName(in.object().getClass().getName());
		ref.setObjectId(id);
		ref.setProvider(provider);
		ref.setUuidLongPart(in.uuid().getLongPart());
		ref.setVersion(_currentVersion);

		Serializable refId = getRefSession().save(ref);
		getRefSession().load(ReplicationReference.class, refId);
	}

	protected Collection getChangedObjectsSinceLastReplication(PersistentClass persistentClass) {
		final String className = persistentClass.getClassName();

		Criteria criteria = getRefSession().createCriteria(ReplicationReference.class);
		criteria.add(Restrictions.gt("version", getLastReplicationVersion()));
		criteria.add(Restrictions.eq("className", className));

		Collection<ChangedObjectId> ids = new HashSet();
		final Iterator results = criteria.list().iterator();
		while (results.hasNext()) {
			ReplicationReference ref = (ReplicationReference) results.next();
			final ChangedObjectId changedObjectId = new ChangedObjectId(ref.getObjectId(), className);
			ids.add(changedObjectId);
		}

		return loadObj(ids);
	}

	protected Collection getNewObjectsSinceLastReplication(PersistentClass persistentClass) {
		final String className = persistentClass.getClassName();

		Criteria criteria = getRefSession().createCriteria(ReplicationReference.class);
		criteria.add(Restrictions.isNull("uuidLongPart"));
		criteria.add(Restrictions.isNull("provider"));

		Collection<ChangedObjectId> ids = new HashSet();
		final Iterator results = criteria.list().iterator();
		while (results.hasNext()) {
			ReplicationReference ref = (ReplicationReference) results.next();
			final ChangedObjectId changedObjectId = new ChangedObjectId(ref.getObjectId(), className);
			ids.add(changedObjectId);
		}

		final Collection newObjects = loadObj(ids);

		generateReplicationMetaData(newObjects);

		return newObjects;
	}

	protected RefConfig getRefConfig() {
		return _refCfg;
	}

	protected void incrementObjectVersion(PostUpdateEvent event) {
		final String className = event.getEntity().getClass().getName();
		final Serializable id = event.getId();
		ensureLong(id);

		Criteria criteria = getRefSession().createCriteria(ReplicationReference.class);
		criteria.add(Restrictions.eq("objectId", id));
		criteria.add(Restrictions.eq("className", className));

		final List exisitings = criteria.list();
		int count = exisitings.size();

		if (count != 1)
			throw new RuntimeException("ReplicationReference not found");
		else {
			ReplicationReference exist = (ReplicationReference) exisitings.get(0);
			final long newVersion = exist.getVersion() + 1;
			exist.setVersion(newVersion);
			getRefSession().update(exist);

			final ReplicationReference loaded = (ReplicationReference) getRefSession().load(ReplicationReference.class, getRefSession().getIdentifier(exist));
			if (loaded.getVersion() != newVersion)
				throw new RuntimeException("Unable to update the version");
		}
	}

	public void closeIfOpened() {
		super.closeIfOpened();

		_refSession.close();
		_refSessionFactory.close();
	}
}
