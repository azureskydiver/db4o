package com.db4o.test.replication.hibernate;

import com.db4o.ext.Db4oUUID;
import com.db4o.inside.replication.ReplicationReference;
import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.hibernate.impl.ReplicationReferenceImpl;
import com.db4o.test.replication.collections.ListHolder;
import com.db4o.test.replication.provider.ReplicationProviderTest;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HibernateProviderTest extends ReplicationProviderTest {
// ------------------------------ FIELDS ------------------------------

	protected Configuration cfg = HibernateUtil.refAsTableProviderA().getConfiguration();
	;

	protected void clean() {
		final SchemaExport schemaExport = new SchemaExport(cfg);
		schemaExport.setHaltOnError(true);
		schemaExport.drop(false, true);
	}

	protected void destroySubject() {
		subject.destroy();
		subject = null;
	}

	protected TestableReplicationProviderInside prepareSubject() {
		return HibernateUtil.refAsTableProviderA();
	}

	protected boolean subjectSupportsRollback() {
		return true;
	}

	public void testReplicationProvider() {
		super.testReplicationProvider();
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
}