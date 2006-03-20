package com.db4o.replication.hibernate.impl;

import com.db4o.replication.hibernate.metadata.MySignature;
import com.db4o.replication.hibernate.metadata.Uuid;
import com.db4o.replication.hibernate.metadata.UuidLongPartSequence;
import org.hibernate.Session;

import java.util.List;

public class UuidGenerator {
// ------------------------------ FIELDS ------------------------------

	private UuidLongPartSequence _sequence;
	private MySignature _mySig;

// --------------------------- CONSTRUCTORS ---------------------------

	public UuidGenerator() {

	}

	public Uuid next() {
		_sequence.increment();
		Uuid out = new Uuid();
		out.setLongPart(_sequence.getCurrent());
		out.setProvider(_mySig);
		return out;
	}

	public void reset(Session session) {
		final List exisitings = session.createCriteria(UuidLongPartSequence.class).list();
		final int count = exisitings.size();

		if (count == 1)
			_sequence = (UuidLongPartSequence) exisitings.get(0);
		else if (count == 0) {
			_sequence = new UuidLongPartSequence();
			session.save(_sequence);
		} else
			throw new RuntimeException("result size = " + count + ". It should be either 1 or 0");

		_mySig = Util.genMySignature(session);
	}
}
