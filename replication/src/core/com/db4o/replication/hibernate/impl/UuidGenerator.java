package com.db4o.replication.hibernate.impl;

import com.db4o.replication.hibernate.metadata.UuidLongPartSequence;
import org.hibernate.Session;

import java.util.List;

public class UuidGenerator {
	private final UuidLongPartSequence _sequence;
	private final Session _session;

	public UuidGenerator(Session session) {
		this._session = session;
		final List exisitings = _session.createCriteria(UuidLongPartSequence.class).list();
		final int count = exisitings.size();

		if (count == 1)
			_sequence = (UuidLongPartSequence) exisitings.get(0);
		else if (count == 0) {
			_sequence = new UuidLongPartSequence();
			_session.save(_sequence);
			_session.flush();
		} else
			throw new RuntimeException("result size = " + count + ". It should be either 1 or 0");
	}

	public long next() {
		_sequence.increment();
		return _sequence.getCurrent();
	}
}
