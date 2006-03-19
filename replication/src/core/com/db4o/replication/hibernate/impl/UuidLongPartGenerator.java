package com.db4o.replication.hibernate.impl;

import org.hibernate.Session;

public class UuidLongPartGenerator {
	protected Session _session;

	public UuidLongPartGenerator(Session session) {
//		_session = session;
//
//		final List exisitings = _session.createCriteria(UuidLongPartSequence.class).list();
//		final int count = exisitings.size();
//
//		if (count == 1)
//			_uuidLongPartSequence = (UuidLongPartSequence) exisitings.get(0);
//		else if (count == 0) {
//			_uuidLongPartSequence = new UuidLongPartSequence();
//			_session.save(_uuidLongPartSequence);
//			_session.flush();
//		} else
//			throw new RuntimeException("result size = " + count + ". It should be either 1 or 0");
	}

	public long next() {
//		_uuidLongPartSequence.increment();
//		_session.update(_uuidLongPartSequence);
//
//		return _uuidLongPartSequence.getCurrent();
		return 1;
	}
}
