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
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Restrictions;
import org.hibernate.event.EventListeners;
import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostInsertEventListener;
import org.hibernate.event.PostUpdateEvent;
import org.hibernate.mapping.PersistentClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class RefAsTableReplicationProvider extends AbstractReplicationProvider {
	public RefAsTableReplicationProvider(Configuration cfg) {
		this(cfg, null);
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
		criteria.add(Restrictions.eq(ReplicationReference.UUID_LONG_PART, new Long(uuid.getLongPart())));
		criteria.add(Restrictions.eq(ReplicationReference.CLASS_NAME, hint.getName()));
		criteria.add(Restrictions.eq(ReplicationReference.PROVIDER, getProviderSignature(uuid.getSignaturePart())));

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

		final List exisitings = Shared.getByHibernateId(getRefSession(), obj.getClass().getName(), id);

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

	protected void saveOrUpdateReplicaMetadata(com.db4o.inside.replication.ReplicationReference ref) {
		ensureReplicationActive();
		final Object obj = ref.object();

		final long id = Shared.castAsLong(getObjectSession().getIdentifier(obj));
		final Session s = getRefSession();

		final List existings = Shared.getByHibernateId(s, obj.getClass().getName(), id);
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
		List<String> classNames = getTypeClassNames(persistentClass);

		Criteria criteria = getRefSession().createCriteria(ReplicationReference.class);
		criteria.add(Restrictions.gt(ReplicationReference.VERSION, getLastReplicationVersion()));
		final Criterion nestedOr = build(classNames, ReplicationReference.CLASS_NAME);
		criteria.add(nestedOr);

		Collection<ChangedObjectId> ids = new HashSet();
		final Iterator results = criteria.list().iterator();
		while (results.hasNext()) {
			ReplicationReference ref = (ReplicationReference) results.next();
			final ChangedObjectId changedObjectId = new ChangedObjectId(ref.getObjectId(), persistentClass.getRootClass().getClassName());
			ids.add(changedObjectId);
		}

		return loadObj(ids);
	}

	protected Collection getNewObjectsSinceLastReplication(PersistentClass persistentClass) {
		List<String> classNames = getTypeClassNames(persistentClass);

		Criteria criteria = getRefSession().createCriteria(ReplicationReference.class);
		criteria.add(Restrictions.isNull(ReplicationReference.PROVIDER));
		final Criterion nestedOr = build(classNames, ReplicationReference.CLASS_NAME);
		criteria.add(nestedOr);

		Collection<ChangedObjectId> ids = new HashSet();
		final Iterator results = criteria.list().iterator();
		while (results.hasNext()) {
			ReplicationReference ref = (ReplicationReference) results.next();
			final ChangedObjectId changedObjectId = new ChangedObjectId(ref.getObjectId(), persistentClass.getRootClass().getClassName());
			ids.add(changedObjectId);

			ref.setProvider(_mySig);
			ref.setUuidLongPart(uuidLongPartGenerator.next());
			ref.setVersion(_currentVersion);
			getRefSession().update(ref);
		}

		getRefSession().flush();

		return loadObj(ids);
	}

	private Criterion build(List<String> classNames, String fieldName) {
		final int tail = classNames.size() - 1;
		return recursiveBuild(classNames, fieldName, Restrictions.eq(fieldName, classNames.remove(tail)));
	}

	private Criterion recursiveBuild(List<String> classNames, String fieldName, Criterion rhs) {
		if (classNames.size() == 0) {
			return rhs;
		} else {
			final int tail = classNames.size() - 1;
			final LogicalExpression tmp = Restrictions.or(Restrictions.eq(fieldName, classNames.remove(tail)), rhs);
			return recursiveBuild(classNames, fieldName, tmp);
		}
	}

	private List<String> getTypeClassNames(PersistentClass rootClass) {
		List<String> out = new ArrayList<String>();
		out.add(rootClass.getClassName());
		if (rootClass.hasSubclasses()) {
			final Iterator it = rootClass.getSubclassClosureIterator();
			while (it.hasNext()) {
				PersistentClass subC = (PersistentClass) it.next();
				out.add(subC.getClassName());
			}
		}
		return out;
	}

	protected RefConfig getRefConfig() {
		return _refCfg;
	}

	protected void incrementObjectVersion(PostUpdateEvent event) {
		ensureReplicationInActive();
		final Object entity = event.getEntity();
		final long id = Shared.castAsLong(event.getId());

		final Session sess = getRefSession();
		Shared.incrementObjectVersion(sess, entity, id);
	}


	public class MyObjectInsertedListener implements PostInsertEventListener {
		public void onPostInsert(PostInsertEvent event) {
			if (isReplicationActive()) return;

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
