package com.db4odoc.timestamp;

import java.sql.Timestamp;
import java.util.Date;

import com.db4o.*;
import com.db4o.config.*;


public class TimeTranslator implements ObjectConstructor {
	public Object onStore(ObjectContainer container, Object applicationObject) {
		Timestamp timestamp = (Timestamp) applicationObject;

		return new Object[] { new Date(timestamp.getTime()) };
	}

	public Object onInstantiate(ObjectContainer container, Object storedObject) {
		Object[] raw = (Object[]) storedObject;
		Date date = (Date) raw[0];
		return new Timestamp(date.getTime());
	}

	public void onActivate(ObjectContainer container, Object applicationObject,
			Object storedObject) {
	}

	public Class storedClass() {
		return Object[].class;
	}
}