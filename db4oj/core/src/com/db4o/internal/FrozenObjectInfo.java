/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.internal;

import com.db4o.ext.*;

public class FrozenObjectInfo implements ObjectInfo {
	
	private final Db4oUUID _uuid;
	private final long _id;
	private final long _version;
	private final Object _object;
	
	public FrozenObjectInfo(ObjectInfo info) {
		this(info.getObject(), info.getInternalID(), info.getUUID(), info.getVersion());
	}

	public FrozenObjectInfo(Object object, long id, Db4oUUID uuid, long version) {
		_uuid = uuid;
		_id = id;
		_version = version;
		_object = object;
	}

	public long getInternalID() {
		return _id;
	}

	public Object getObject() {
		return _object;
	}

	public Db4oUUID getUUID() {
		return _uuid;
	}

	public long getVersion() {
		return _version;
	}
}