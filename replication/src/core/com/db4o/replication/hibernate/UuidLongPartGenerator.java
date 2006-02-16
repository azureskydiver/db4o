package com.db4o.replication.hibernate;

import org.hibernate.Session;

import java.util.List;

public class UuidLongPartGenerator {
	protected Session _session;
	protected UuidLongPartSequence _uuidLongPartSequence;
	public static final long MIN_SEQ_NO = 1000;

	public UuidLongPartGenerator(Session session) {
		_session = session;

		final List exisitings = _session.createCriteria(UuidLongPartSequence.class).list();
		final int count = exisitings.size();

		if (count == 1)
			_uuidLongPartSequence = (UuidLongPartSequence) exisitings.get(0);
		else if (count == 0) {
			_uuidLongPartSequence = new UuidLongPartSequence();
			_session.save(_uuidLongPartSequence);
		} else
			throw new RuntimeException("result size = " + count + ". It should be either 1 or 0");
	}

	public long next() {
		_uuidLongPartSequence.increment();
		_session.flush();

		return _uuidLongPartSequence.getCurrent();
	}
}
