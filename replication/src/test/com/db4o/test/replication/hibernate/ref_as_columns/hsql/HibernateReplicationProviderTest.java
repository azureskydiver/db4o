package com.db4o.test.replication.hibernate.ref_as_columns.hsql;

import com.db4o.ext.Db4oUUID;
import com.db4o.inside.replication.ReplicationReference;
import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.hibernate.impl.ReplicationReferenceImpl;
import com.db4o.replication.hibernate.impl.ref_as_columns.RefAsColumnsReplicationProvider;
import com.db4o.test.replication.collections.ListContent;
import com.db4o.test.replication.collections.ListHolder;
import com.db4o.test.replication.hibernate.HibernateUtil;
import com.db4o.test.replication.provider.Car;
import com.db4o.test.replication.provider.Pilot;
import com.db4o.test.replication.provider.ReplicationProviderTest;
import org.hibernate.cfg.Configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HibernateReplicationProviderTest extends ReplicationProviderTest {
	public void testReplicationProvider() {
		//TODO HibernateReplicationProvider does not cache a just stored object  
		//super.testReplicationProvider();
		tstCollection();
	}

	private void tstCollection() {
		subject = prepareSubject();
		subject.startReplicationTransaction(PEER_SIGNATURE);

		Db4oUUID listHolderUuid = new Db4oUUID(1234, PEER_SIGNATURE_BYTES);

		ListHolder listHolderFromA = new ListHolder("i am a list");

		ReplicationReference refFromA = new ReplicationReferenceImpl(listHolderFromA, listHolderUuid, 9555);

		ListHolder listHolderClonedInB = new ListHolder("i am a list");

		subject.referenceNewObject(listHolderClonedInB, refFromA, null, null);
		subject.storeReplica(listHolderClonedInB);
		ReplicationReference listHolderFromB = subject.produceReference(listHolderClonedInB, null, null);
		ensure(listHolderFromB.object() == listHolderClonedInB);

		Collection collectionInB = listHolderClonedInB.getList();

		ReplicationReference collectionRefFromB = subject.produceReference(collectionInB, listHolderClonedInB, "list");
		ensure(collectionRefFromB.object() == collectionInB);

		final Db4oUUID collectionUuid = collectionRefFromB.uuid();
		ReplicationReference collectionRefFromA = new ReplicationReferenceImpl(new ArrayList(), collectionUuid, 9555);

		subject.referenceNewObject(collectionInB, collectionRefFromA, listHolderFromB, "list");
		subject.storeReplica(collectionInB);
		ensure(subject.produceReference(collectionInB, null, null) == collectionRefFromB);
		ensure(subject.produceReference(collectionInB, null, null).object() == collectionInB);

		final ReplicationReference byUuid = subject.produceReferenceByUUID(collectionUuid, List.class);
		ensure(byUuid != null);

		subject.clearAllReferences();
		final ReplicationReference refFromBAfterClear = subject.produceReferenceByUUID(listHolderUuid, ListHolder.class);
		ensure(refFromBAfterClear != null);

		final ListHolder listHolderInBAfterClear = ((ListHolder) refFromBAfterClear.object());
		final ReplicationReference collectionRefFromBAfterClear = subject.produceReference(listHolderInBAfterClear.getList(), listHolderInBAfterClear, "list");
		ensure(collectionRefFromBAfterClear != null);

		ensure(collectionRefFromBAfterClear.uuid().equals(collectionUuid));

		destroySubject();
	}

	protected boolean subjectSupportsRollback() {
		return true;
	}

	protected TestableReplicationProviderInside prepareSubject() {
		return new RefAsColumnsReplicationProvider(newCfg());
	}

	protected static Configuration newCfg() {
		Configuration configuration = HibernateUtil.createNewDbConfig();

		configuration.addClass(Car.class);
		configuration.addClass(Pilot.class);
		configuration.addClass(ListHolder.class);
		configuration.addClass(ListContent.class);
		return configuration;
	}

	protected void destroySubject() {
		subject.destroy();
		subject = null;
	}
}