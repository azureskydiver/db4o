package com.db4o.inside.replication;

import com.db4o.replication.ObjectState;

class ObjectStateImpl implements ObjectState {

	private Object _object;
	private boolean _isNew;
	private boolean _wasModified;
	private boolean _wasDeleted;

	public Object getObject() {
		return _object;
	}

	public boolean isNew() {
		return _isNew;
	}

	public boolean wasModified() {
		return _wasModified;
	}

	public boolean wasDeleted() {
		return _wasDeleted;
	}
	
	void setAll(Object obj, boolean isNew, boolean wasModified, boolean wasDeleted) {
		_object = obj;
		_isNew = isNew;
		_wasModified = wasModified;
		_wasDeleted = wasDeleted;
	}

}
