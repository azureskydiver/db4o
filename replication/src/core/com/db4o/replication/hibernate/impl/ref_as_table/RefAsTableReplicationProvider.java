package com.db4o.replication.hibernate.impl.ref_as_table;

import com.db4o.ext.Db4oUUID;
import com.db4o.replication.hibernate.cfg.ObjectConfig;
import com.db4o.replication.hibernate.cfg.RefConfig;
import com.db4o.replication.hibernate.impl.AbstractReplicationProvider;
import com.db4o.replication.hibernate.impl.ChangedObjectId;
import com.db4o.replication.hibernate.impl.Util;
import com.db4o.replication.hibernate.metadata.ObjectReference;
import com.db4o.replication.hibernate.metadata.ReplicationProviderSignature;
import org.apache.commons.lang.ArrayUtils;
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
	protected PostInsertEventListener objectInsertedListener = new MyObjectInsertedListener();

	public RefAsTableReplicationProvider(Configuration cfg) {
		this(cfg, null);
	}

	public RefAsTableReplicationProvider(Configuration cfg, String name) {
		//setCurrentSessionContext(cfg);
		_name = name;

		_refCfg = RefAsTableConfiguration.produce(cfg);

		_objectConfig = new ObjectConfig(cfg);

		new RefAsTableTablesCreator(getRefCfg()).createTables();

		initEventListeners();

		_objectSessionFactory = getObjectConfig().getConfiguration().buildSessionFactory();
		_objectSession = _objectSessionFactory.openSession();
		_objectSession.setFlushMode(FlushMode.COMMIT);
		_objectTransaction = _objectSession.beginTransaction();

		init();

		_alive = true;
	}

	protected void initEventListeners() {
		super.initEventListeners();
		EventListeners el = getObjectConfig().getConfiguration().getEventListeners();
		el.setPostInsertEventListeners(createPostInsertEventListeners(el.getPostInsertEventListeners()));
	}

	public void storeNew(Object root) {
		super.storeNew(root);
	}

	public void destroyListeners() {
		super.destroyListeners();

		EventListeners eventListeners = getObjectConfig().getConfiguration().getEventListeners();
		PostInsertEventListener[] o1 = eventListeners.getPostInsertEventListeners();
		PostInsertEventListener[] r1 = (PostInsertEventListener[]) ArrayUtils.removeElement(
				o1, objectInsertedListener);
		if ((o1.length - r1.length) != 1)
			throw new RuntimeException("can't remove");

		eventListeners.setPostInsertEventListeners(r1);
		objectInsertedListener = null;
	}

	protected PostInsertEventListener[] createPostInsertEventListeners(PostInsertEventListener[] defaultListeners) {
		for (int i = 0; i < defaultListeners.length; i++) {
			if (defaultListeners[i] instanceof ObjectInsertedListenerImpl) {
				defaultListeners[i] = objectInsertedListener;
				return defaultListeners;
			}
		}

		PostInsertEventListener[] out;

		final int count = defaultListeners.length;
		out = new PostInsertEventListener[count + 1];
		System.arraycopy(defaultListeners, 0, out, 0, count);
		out[count] = objectInsertedListener;
		return out;
	}

	protected Session getRefSession() {
		return getSession();
	}

	protected com.db4o.inside.replication.ReplicationReference produceObjectReferenceByUUID(Db4oUUID uuid, Class hint) {
		Criteria criteria = getRefSession().createCriteria(ObjectReference.class);
		criteria.add(Restrictions.eq(ObjectReference.UUID_LONG_PART, new Long(uuid.getLongPart())));
		criteria.add(Restrictions.eq(ObjectReference.CLASS_NAME, hint.getName()));
		criteria.add(Restrictions.eq(ObjectReference.PROVIDER, getProviderSignature(uuid.getSignaturePart())));

		final List exisitings = criteria.list();
		int count = exisitings.size();

		if (count == 0)
			return null;
		else if (count > 1)
			throw new RuntimeException("Only one ObjectReference should exist");
		else {
			ObjectReference exist = (ObjectReference) exisitings.get(0);
			Object obj = getSession().load(exist.getClassName(), exist.getObjectId());

			return createReference(obj, uuid, exist.getVersion());
		}
	}

	protected com.db4o.inside.replication.ReplicationReference produceObjectReference(Object obj) {
		if (!getSession().contains(obj)) return null;

		long id = Shared.castAsLong(getSession().getIdentifier(obj));

		final List exisitings = getRefById(id, obj);

		int count = exisitings.size();

		if (count != 1) throw new RuntimeException("ObjectReference must exist for " + obj);

		ObjectReference ref;
		ref = (ObjectReference) exisitings.get(0);

		if (ref.getProvider() == null) {
			ref.setProvider(_mySig);
			ref.setUuidLongPart(uuidLongPartGenerator.next());
			ref.setVersion(getLastReplicationVersion());
			getRefSession().update(ref);
		}

		return createReference(obj, new Db4oUUID(ref.getUuidLongPart(), ref.getProvider().getBytes()), ref.getVersion());
	}

	protected List getRefById(long id, Object obj) {
		Criteria criteria = getRefSession().createCriteria(ObjectReference.class);
		criteria.add(Restrictions.eq(ObjectReference.OBJECT_ID, id));
		criteria.add(Restrictions.eq(ObjectReference.CLASS_NAME, obj.getClass().getName()));
		return criteria.list();
	}

	protected void saveOrUpdateReplicaMetadata(com.db4o.inside.replication.ReplicationReference ref) {
		ensureReplicationActive();
		final Object obj = ref.object();

		final long id = Shared.castAsLong(getSession().getIdentifier(obj));
		final Session s = getRefSession();

		final List existings = getRefById(id, obj);
		if (existings.size() == 0) {
			ReplicationProviderSignature provider = getProviderSignature(ref.uuid().getSignaturePart());

			ObjectReference tmp = new ObjectReference();
			tmp.setClassName(obj.getClass().getName());
			tmp.setObjectId(id);
			tmp.setProvider(provider);
			tmp.setUuidLongPart(ref.uuid().getLongPart());
			tmp.setVersion(ref.version());

			s.save(tmp);
		} else {
			ObjectReference exist = (ObjectReference) existings.get(0);
			exist.setProvider(getProviderSignature(ref.uuid().getSignaturePart()));
			exist.setUuidLongPart(ref.uuid().getLongPart());
			exist.setVersion(ref.version());
			s.update(exist);
		}

	}

	protected Collection getChangedObjectsSinceLastReplication(PersistentClass persistentClass) {
		List<String> classNames = getTypeClassNames(persistentClass);

		Criteria criteria = getRefSession().createCriteria(ObjectReference.class);
		criteria.add(Restrictions.gt(ObjectReference.VERSION, getLastReplicationVersion()));
		final Criterion nestedOr = build(classNames, ObjectReference.CLASS_NAME);
		criteria.add(nestedOr);

		Collection<ChangedObjectId> ids = new HashSet();
		final Iterator results = criteria.list().iterator();
		while (results.hasNext()) {
			ObjectReference ref = (ObjectReference) results.next();
			final ChangedObjectId changedObjectId = new ChangedObjectId(ref.getObjectId(), persistentClass.getRootClass().getClassName());
			ids.add(changedObjectId);
		}

		return loadObj(ids);
	}

	protected Collection getNewObjectsSinceLastReplication(PersistentClass persistentClass) {
		List<String> classNames = getTypeClassNames(persistentClass);

		Criteria criteria = getRefSession().createCriteria(ObjectReference.class);
		criteria.add(Restrictions.isNull(ObjectReference.PROVIDER));
		final Criterion nestedOr = build(classNames, ObjectReference.CLASS_NAME);
		criteria.add(nestedOr);

		Collection<ChangedObjectId> ids = new HashSet();
		final Iterator results = criteria.list().iterator();
		while (results.hasNext()) {
			ObjectReference ref = (ObjectReference) results.next();
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

			if (Util.skip(entity)) return;
			long id = Shared.castAsLong(event.getId());

			ObjectReference ref = new ObjectReference();
			ref.setClassName(entity.getClass().getName());
			ref.setObjectId(id);

			Session s = getRefSession();
			s.save(ref);
		}
	}
}
