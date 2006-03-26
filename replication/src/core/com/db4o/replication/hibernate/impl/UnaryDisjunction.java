package com.db4o.replication.hibernate.impl;

import net.sf.jga.fn.UnaryPredicate;
import net.sf.jga.fn.adaptor.OrUnary;

import java.util.ArrayList;
import java.util.List;

public class UnaryDisjunction<T extends UnaryPredicate> extends UnaryPredicate {
	OrUnary delegate;
	List<T> functors = new ArrayList();

	public Boolean fn(Object arg) {
		if (delegate == null) {
			if (functors.size() <= 2)
				throw new RuntimeException("pass in at least two functors");

			delegate = new OrUnary(functors.get(0), functors.get(1));
			for (int i = 2; i < functors.size(); i++)
				delegate = new OrUnary(delegate, functors.get(i));
		}

		return delegate.fn(arg);
	}

	public void add(T functor) {
		functors.add(functor);
	}
}


