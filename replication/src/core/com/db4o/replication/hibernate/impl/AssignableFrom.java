package com.db4o.replication.hibernate.impl;

import net.sf.jga.fn.UnaryPredicate;

public class AssignableFrom<T extends Class> extends UnaryPredicate<T> {
	private Class<? extends T> _class;

	public AssignableFrom(Class<? extends T> cl) {
		if (cl == null)
			throw new IllegalArgumentException("A class must be given");

		_class = cl;
	}

	public Boolean fn(T arg) {
		return _class.isAssignableFrom(arg);
	}
}
