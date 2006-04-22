package com.db4o.replication.hibernate.impl;

import com.db4o.foundation.TimeStampIdGenerator;
import org.hibernate.Session;

import java.util.HashMap;
import java.util.Map;

public class GeneratorMap {
	private static Map<Session, TimeStampIdGenerator> sessionTimeStampIdGeneratorMap = new HashMap();

	static void put(Session s, TimeStampIdGenerator t) {
		sessionTimeStampIdGeneratorMap.put(s, t);
	}

	static TimeStampIdGenerator get(Session s) {
		return sessionTimeStampIdGeneratorMap.get(s);
	}
}
