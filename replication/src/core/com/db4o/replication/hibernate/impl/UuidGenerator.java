package com.db4o.replication.hibernate.impl;

import com.db4o.replication.hibernate.metadata.Uuid;
import com.db4o.replication.hibernate.metadata.UuidLongPartSequence;
import org.hibernate.Session;

import java.util.List;

final class UuidGenerator {
// -------------------------- STATIC METHODS --------------------------

	public static Uuid next(Session session) {
		final List exisitings = session.createCriteria(UuidLongPartSequence.class).list();
		final int count = exisitings.size();

		if (count != 1)
			throw new RuntimeException("UuidLongPartSequence not found");
		else {
			UuidLongPartSequence uuidLongPartSequence = (UuidLongPartSequence) exisitings.get(0);

			Uuid out = new Uuid();
			out.setLongPart(uuidLongPartSequence.getNext());
			out.setProvider(Util.genMySignature(session));

			return out;
		}
	}
}
