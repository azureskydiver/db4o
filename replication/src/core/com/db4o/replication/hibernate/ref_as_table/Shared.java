package com.db4o.replication.hibernate.ref_as_table;

import com.db4o.replication.hibernate.common.Common;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.io.Serializable;
import java.util.List;

public class Shared {
	public static void ensureLong(Serializable id) {
		if (!(id instanceof Long))
			throw new IllegalStateException("You must use 'long' as the type of the hibernate id");
	}

	public static long castAsLong(Serializable id) {
		ensureLong(id);
		return ((Long) id).longValue();
	}

	static List getByHibernateId(Session session, String className, long id) {
		Criteria criteria = session.createCriteria(ReplicationReference.class);
		criteria.add(Restrictions.eq(ReplicationReference.OBJECT_ID, id));
		criteria.add(Restrictions.eq(ReplicationReference.CLASS_NAME, className));

		return criteria.list();
	}

	static void incrementObjectVersion(Session sess, Object entity, long id) {
		final List exisitings = getByHibernateId(sess, entity.getClass().getName(), id);
		int count = exisitings.size();

		if (count != 1)
			throw new RuntimeException("ReplicationReference not found");
		else {
			ReplicationReference exist = (ReplicationReference) exisitings.get(0);

			long newVer = Common.getMaxVersion(sess.connection()) + 1;

			exist.setVersion(newVer);
			sess.update(exist);
			sess.flush();

			final ReplicationReference loaded = (ReplicationReference) sess.load(ReplicationReference.class, sess.getIdentifier(exist));
			if (loaded.getVersion() != newVer)
				throw new RuntimeException("Unable to update the version");
		}
	}

}
