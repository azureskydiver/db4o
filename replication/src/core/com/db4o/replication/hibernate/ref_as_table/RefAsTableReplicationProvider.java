package com.db4o.replication.hibernate.ref_as_table;

import com.db4o.ext.Db4oUUID;
import com.db4o.replication.hibernate.AbstractReplicationProvider;
import com.db4o.replication.hibernate.ObjectConfig;
import com.db4o.replication.hibernate.RefConfig;
import com.db4o.replication.hibernate.common.ChangedObjectId;
import com.db4o.replication.hibernate.common.Common;
import com.db4o.replication.hibernate.common.ReplicationProviderSignature;
import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.criterion.Restrictions;
import org.hibernate.event.EventListeners;
import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostInsertEventListener;
import org.hibernate.event.PostUpdateEvent;
import org.hibernate.mapping.PersistentClass;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class RefAsTableReplicationProvider extends AbstractReplicationProvider {
	public RefAsTableReplicationProvider(Configuration cfg) {
		this(cfg, null);
	}

	protected void setCurrentSessionContext(Configuration cfg) {
		String key = Environment.CURRENT_SESSION_CONTEXT_CLASS;
		if (cfg.getProperty(key) == null)
			cfg.setProperty(key, "thread");
	}

	public RefAsTableReplicationProvider(Configuration cfg, String name) {
		//setCurrentSessionContext(cfg);
		_name = name;

		_refCfg = RefAsTableConfiguration.produce(cfg);

		_objectConfig = new ObjectConfig(cfg);

		new RefAsTableTablesCreator(getRefCfg()).execute();

		initEventListeners();

		_objectSessionFactory = getObjectConfig().getConfiguration().buildSessionFactory();
		_objectSession = _objectSessionFactory.openSession();
		_objectSession.setFlushMode(FlushMode.COMMIT);
		_objectTransaction = _objectSession.beginTransaction();

		init();
	}

	protected void initEventListeners() {
		super.initEventListeners();
		EventListeners el = getObjectConfig().getConfiguration().getEventListeners();
		el.setPostInsertEventListeners(createPostInsertEventListeners(el.getPostInsertEventListeners()));
	}

	public void storeNew(Object root) {
		super.storeNew(root);
	}

	protected PostInsertEventListener[] createPostInsertEventListeners(PostInsertEventListener[] defaultListeners) {
		PostInsertEventListener objectInsertedListener = new MyObjectInsertedListener();

		if (defaultListeners == null) {
			return new PostInsertEventListener[]{objectInsertedListener};
		} else {
			PostInsertEventListener[] out;
			final int count = defaultListeners.length;
			out = new PostInsertEventListener[count + 1];
			System.arraycopy(defaultListeners, 0, out, 0, count);
			out[count] = objectInsertedListener;
			return out;
		}
	}

	protected Session getRefSession() {
		return getObjectSession();
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

		long id = Shared.castAsLong(getObjectSession().getIdentifier(obj));

		final List exisitings = getByHibernateId(obj.getClass().getName(), id);

		int count = exisitings.size();

		if (count != 1) throw new RuntimeException("ReplicationReference must exist for " + obj);

		ReplicationReference ref;
		ref = (ReplicationReference) exisitings.get(0);

		if (ref.getProvider() == null) {
			ref.setProvider(_mySig);
			ref.setUuidLongPart(uuidLongPartGenerator.next());
			ref.setVersion(getLastReplicationVersion());
			getRefSession().update(ref);
		}

		return createReference(obj, new Db4oUUID(ref.getUuidLongPart(), ref.getProvider().getBytes()), ref.getVersion());
	}

	private List getByHibernateId(String className, long id) {
		Criteria criteria = getRefSession().createCriteria(ReplicationReference.class);
		criteria.add(Restrictions.eq("objectId", id));
		criteria.add(Restrictions.eq("className", className));

		return criteria.list();
	}

	protected void saveOrUpdateReplicaMetadata(com.db4o.inside.replication.ReplicationReference ref) {
		ensureReplicationActive();
		final Object obj = ref.object();

		final long id = Shared.castAsLong(getObjectSession().getIdentifier(obj));
		final Session s = getRefSession();

		final List existings = getByHibernateId(obj.getClass().getName(), id);
		if (existings.size() == 0) {
			ReplicationProviderSignature provider = getProviderSignature(ref.uuid().getSignaturePart());

			ReplicationReference tmp = new ReplicationReference();
			tmp.setClassName(obj.getClass().getName());
			tmp.setObjectId(id);
			tmp.setProvider(provider);
			tmp.setUuidLongPart(ref.uuid().getLongPart());
			tmp.setVersion(ref.version());

			s.save(tmp);
		} else {
			ReplicationReference exist = (ReplicationReference) existings.get(0);
			exist.setProvider(getProviderSignature(ref.uuid().getSignaturePart()));
			exist.setUuidLongPart(ref.uuid().getLongPart());
			exist.setVersion(ref.version());
			s.update(exist);
		}

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
		criteria.add(Restrictions.isNull("provider"));
		criteria.add(Restrictions.eq("className", className));

		Collection<ChangedObjectId> ids = new HashSet();
		final Iterator results = criteria.list().iterator();
		while (results.hasNext()) {
			ReplicationReference ref = (ReplicationReference) results.next();
			final ChangedObjectId changedObjectId = new ChangedObjectId(ref.getObjectId(), className);
			ids.add(changedObjectId);

			ref.setProvider(_mySig);
			ref.setUuidLongPart(uuidLongPartGenerator.next());
			ref.setVersion(_currentVersion);
			getRefSession().update(ref);
		}

		getRefSession().flush();

		return loadObj(ids);
	}

	protected RefConfig getRefConfig() {
		return _refCfg;
	}

	protected void incrementObjectVersion(PostUpdateEvent event) {
		ensureReplicationInActive();
		final Object entity = event.getEntity();
		final long id = Shared.castAsLong(event.getId());

		final List exisitings = getByHibernateId(entity.getClass().getName(), id);
		int count = exisitings.size();

		if (count != 1)
			throw new RuntimeException("ReplicationReference not found");
		else {
			ReplicationReference exist = (ReplicationReference) exisitings.get(0);

			long newVer = Common.getMaxVersion(_objectSession.connection()) + 1;

			exist.setVersion(newVer);
			getRefSession().update(exist);

			final ReplicationReference loaded = (ReplicationReference) getRefSession().load(ReplicationReference.class, getRefSession().getIdentifier(exist));
			if (loaded.getVersion() != newVer)
				throw new RuntimeException("Unable to update the version");
		}
	}


	public class MyObjectInsertedListener implements PostInsertEventListener {
		public void onPostInsert(PostInsertEvent event) {
			if (_inReplication) return;

			Object entity = event.getEntity();

			if (Common.skip(entity)) return;
			long id = Shared.castAsLong(event.getId());

			ReplicationReference ref = new ReplicationReference();
			ref.setClassName(entity.getClass().getName());
			ref.setObjectId(id);

			Session s = getRefSession();
			s.save(ref);
		}
	}
}
