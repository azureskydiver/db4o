package com.db4o.inside.replication;

import com.db4o.replication.ObjectState;

class ObjectStateImpl implements ObjectState {

	private Object _object;
	private boolean _isNew;
	private boolean _wasModified;
	private long _modificationDate;

	public Object getObject() {
		return _object;
	}

	public boolean isNew() {
		return _isNew;
	}

	public boolean wasModified() {
		return _wasModified;
	}

	public long modificationDate() {
		return _modificationDate;
	}

	void setAll(Object obj, boolean isNew, boolean wasModified, long modificationDate) {
		_object = obj;
		_isNew = isNew;
		_wasModified = wasModified;
		_modificationDate = modificationDate;
	}


	public String toString() {
		return "ObjectStateImpl{" +
				"_object=" + _object +
				", _isNew=" + _isNew +
				", _wasModified=" + _wasModified +
				", _modificationDate=" + _modificationDate +
				'}';
	}
}
