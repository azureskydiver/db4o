package com.db4o.replication.hibernate.impl;

import com.db4o.replication.hibernate.metadata.ComponentIdentity;

final class ObjectIdField extends HibernateObjectId{
	public final String _fieldName;
	
	public ObjectIdField(long hibernateId, String className, String fieldName) {
		super(hibernateId, className);
		_fieldName = fieldName;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result + ((_fieldName == null) ? 0 : _fieldName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ObjectIdField other = (ObjectIdField) obj;
		if (_fieldName == null) {
			if (other._fieldName != null)
				return false;
		} else if (!_fieldName.equals(other._fieldName))
			return false;
		return true;
	}
}
