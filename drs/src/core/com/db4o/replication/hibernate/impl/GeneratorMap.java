package com.db4o.replication.hibernate.impl;

import com.db4o.foundation.TimeStampIdGenerator;
import org.hibernate.Session;

import java.util.HashMap;
import java.util.Map;

public class GeneratorMap {
	private static Map<Session, TimeStampIdGenerator> _sessionTimeStampIdGeneratorMap = new HashMap();

	static void put(Session s, TimeStampIdGenerator t) {
		_sessionTimeStampIdGeneratorMap.put(s, t);
	}

	static TimeStampIdGenerator get(Session s) {
		return _sessionTimeStampIdGeneratorMap.get(s);
	}
}
