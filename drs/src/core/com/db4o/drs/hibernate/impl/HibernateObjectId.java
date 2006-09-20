package com.db4o.drs.hibernate.impl;

class HibernateObjectId {
	public final long _hibernateId;
	
	public final String _className;
	
	public HibernateObjectId(long hibernateId, String className) {
		this._hibernateId = hibernateId;
		this._className = className;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((_className == null) ? 0 : _className.hashCode());
		result = PRIME * result + (int) (_hibernateId ^ (_hibernateId >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final HibernateObjectId other = (HibernateObjectId) obj;
		if (_className == null) {
			if (other._className != null)
				return false;
		} else if (!_className.equals(other._className))
			return false;
		if (_hibernateId != other._hibernateId)
			return false;
		return true;
	}
}
