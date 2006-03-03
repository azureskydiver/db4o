package com.db4o.replication.hibernate;

import com.db4o.replication.hibernate.metadata.MetaDataTablesCreator;
import com.db4o.replication.hibernate.metadata.PeerSignature;
import com.db4o.replication.hibernate.metadata.ReplicationProviderSignature;
import com.db4o.replication.hibernate.metadata.ReplicationRecord;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class DefaultMetadataProvider implements MetadataProvider {
	MetadataProviderReplicationConfiguration cfg;

	protected ReplicationRecord _replicationRecord;

	protected PeerSignature _peerSignature;
	private long _currentVersion;

	Session _session;
	protected SessionFactory sessionFactory;
	protected Transaction transaction;

	public DefaultMetadataProvider(MetadataProviderReplicationConfiguration cfg) {
		this.cfg = cfg;

		new MetaDataTablesCreator(cfg).execute();

		sessionFactory = cfg.getConfiguration().buildSessionFactory();
	}

	long getCurrentVersion() {
		return _currentVersion;
	}

	void startReplication(byte[] peerSig) {
		if (_session == null)
			_session = sessionFactory.openSession();

		transaction = _session.beginTransaction();

		PeerSignature existingPeerSignature = getPeerSignature(peerSig);
		if (existingPeerSignature == null) {
			this._peerSignature = new PeerSignature(peerSig);
			_session.save(this._peerSignature);
			_session.flush();
			if (getPeerSignature(peerSig) == null)
				throw new RuntimeException("Cannot insert existingPeerSignature");
			_replicationRecord = new ReplicationRecord();
			_replicationRecord.setPeerSignature(_peerSignature);
			_session.save(_replicationRecord);
		} else {
			this._peerSignature = existingPeerSignature;
			_replicationRecord = getRecord(this._peerSignature);
		}

		_currentVersion = Util.getMaxVersion(_session.connection()) + 1;
	}

	protected ReplicationRecord getRecord(PeerSignature peerSignature) {
		Criteria criteria = _session.createCriteria(ReplicationRecord.class).createCriteria("peerSignature").add(Restrictions.eq("id", new Long(peerSignature.getId())));

		final List exisitingRecords = criteria.list();
		int count = exisitingRecords.size();

		if (count == 0)
			throw new RuntimeException("Record not found. Hibernate was unable to persist the record in the last replication round");
		else if (count > 1)
			throw new RuntimeException("Only one Record should exist for this peer");
		else
			return (ReplicationRecord) exisitingRecords.get(0);
	}

	public void syncVersionWithPeer(long version) {
		_replicationRecord.setVersion(version);
		_session.update(_replicationRecord);

		if (getRecord(_peerSignature).getVersion() != version)
			throw new RuntimeException("The version numbers of persisted record does not match the parameter");
	}

	public void commit() {
		transaction.commit();
	}

	protected PeerSignature getPeerSignature(byte[] bytes) {
		final List exisitingSigs = _session.createCriteria(PeerSignature.class)
				.add(Restrictions.eq(ReplicationProviderSignature.SIGNATURE_BYTE_ARRAY_COLUMN_NAME, bytes))
				.list();

		if (exisitingSigs.size() == 1)
			return (PeerSignature) exisitingSigs.get(0);
		else if (exisitingSigs.size() == 0) return null;
		else
			throw new RuntimeException("result size = " + exisitingSigs.size() + ". It should be either 1 or 0");
	}

	public final void rollbackReplication() {
		transaction.rollback();
	}
}
