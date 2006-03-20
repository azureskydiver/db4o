package com.db4o.replication.hibernate.impl.ref_as_table;

import com.db4o.ext.Db4oUUID;
import com.db4o.inside.replication.ReplicationReference;
import com.db4o.replication.hibernate.cfg.ObjectConfig;
import com.db4o.replication.hibernate.cfg.RefConfig;
import com.db4o.replication.hibernate.impl.AbstractReplicationProvider;
import com.db4o.replication.hibernate.impl.HibernateObjectId;
import com.db4o.replication.hibernate.impl.Util;
import com.db4o.replication.hibernate.metadata.ObjectReference;
import com.db4o.replication.hibernate.metadata.ReplicationProviderSignature;
import com.db4o.replication.hibernate.metadata.Uuid;
import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostUpdateEvent;
import org.hibernate.event.PreDeleteEvent;
import org.hibernate.mapping.PersistentClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class RefAsTableReplicationProvider extends AbstractReplicationProvider {
// --------------------------- CONSTRUCTORS ---------------------------

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

		_sessionFactory = getObjectConfig().getConfiguration().buildSessionFactory();
		_session = _sessionFactory.openSession();
		_session.setFlushMode(FlushMode.COMMIT);
		_transaction = _session.beginTransaction();

		init();

		_alive = true;
	}

	protected Collection getChangedObjectsSinceLastReplication(PersistentClass persistentClass) {
		List<String> classNames = getTypeClassNames(persistentClass);

		Criteria criteria = getSession().createCriteria(ObjectReference.class);
		criteria.add(Restrictions.gt(ObjectReference.VERSION, getLastReplicationVersion()));
		final Criterion nestedOr = build(classNames, ObjectReference.CLASS_NAME);
		criteria.add(nestedOr);

		Collection<HibernateObjectId> ids = new HashSet();
		final Iterator results = criteria.list().iterator();
		while (results.hasNext()) {
			ObjectReference ref = (ObjectReference) results.next();
			final HibernateObjectId hibernateObjectId = new HibernateObjectId(ref.getObjectId(), persistentClass.getRootClass().getClassName());
			ids.add(hibernateObjectId);
		}

		return loadObject(ids);
	}

	protected RefConfig getRefConfig() {
		return _refCfg;
	}

	protected Uuid getUuid(Object obj) {
		return Shared.getUuid(getSession(), obj);
	}

	protected void incrementObjectVersion(PostUpdateEvent event) {
		ensureReplicationInActive();
		final Object entity = event.getEntity();
		final long id = Shared.castAsLong(event.getId());

		final Session sess = getSession();
		Shared.incrementObjectVersion(sess, entity, id);
	}

	protected void objectInserted(PostInsertEvent event) {
		long id = Shared.castAsLong(event.getId());
		Session s = getSession();

		ObjectReference ref = new ObjectReference();
		ref.setClassName(event.getEntity().getClass().getName());
		ref.setObjectId(id);

		ref.setUuid(uuidGenerator.next());

		ref.setVersion(Util.getMaxVersion(s.connection()) + 1);

		s.save(ref);
	}

	protected void objectToBeDeleted(PreDeleteEvent event) {
		super.objectToBeDeleted(event);
		ObjectReference ref = Shared.getObjectReferenceById(getSession(), event.getEntity());

		if (ref == null) throw new RuntimeException("ObjectReference must exist");

		getSession().delete(ref);
	}

	protected ReplicationReference produceObjectReference(Object obj) {
		if (!getSession().contains(obj)) return null;

		final ObjectReference ref = Shared.getObjectReferenceById(getSession(), obj);

		if (ref == null) throw new RuntimeException("ObjectReference must exist for " + obj);

		Uuid uuid = ref.getUuid();
		return _objRefs.put(obj, new Db4oUUID(uuid.getLongPart(), uuid.getProvider().getBytes()), ref.getVersion());
	}

	protected ReplicationReference produceObjectReferenceByUUID(Db4oUUID uuid, Class hint) {
		String alias = "objRef";
		String uuidPath = alias + "." + ObjectReference.UUID + ".";
		String queryString = "from " + ObjectReference.TABLE_NAME
				+ " as " + alias + " where " + uuidPath + Uuid.LONG_PART + "=?"
				+ " AND " + uuidPath + Uuid.PROVIDER + "." + ReplicationProviderSignature.BYTES + "=?";
		Query c = getSession().createQuery(queryString);
		c.setLong(0, uuid.getLongPart());
		c.setBinary(1, uuid.getSignaturePart());

		final List exisitings = c.list();
		int count = exisitings.size();

		if (count == 0)
			return null;
		else if (count > 1)
			throw new RuntimeException("Only one ObjectReference should exist");
		else {
			ObjectReference exist = (ObjectReference) exisitings.get(0);
			Object obj = getSession().load(exist.getClassName(), exist.getObjectId());

			return _objRefs.put(obj, uuid, exist.getVersion());
		}
	}

	protected void saveOrUpdateReplicaMetadata(ReplicationReference ref) {
		ensureReplicationActive();
		final Object obj = ref.object();

		final long id = Shared.castAsLong(getSession().getIdentifier(obj));
		final Session s = getSession();

		final ObjectReference exist = Shared.getObjectReferenceById(getSession(), obj);
		if (exist == null) {
			ReplicationProviderSignature provider = getProviderSignature(ref.uuid().getSignaturePart());

			ObjectReference tmp = new ObjectReference();
			tmp.setClassName(obj.getClass().getName());
			tmp.setObjectId(id);

			Uuid uuid = new Uuid();
			uuid.setLongPart(ref.uuid().getLongPart());
			uuid.setProvider(provider);
			tmp.setUuid(uuid);

			tmp.setVersion(ref.version());

			s.save(tmp);
		} else {
			exist.setVersion(ref.version());
			s.update(exist);
		}
	}

	private Criterion build(List<String> classNames, String fieldName) {
		Disjunction disjunction = Restrictions.disjunction();

		for (String s : classNames)
			disjunction.add(Restrictions.eq(fieldName, s));
		return disjunction;
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
}
