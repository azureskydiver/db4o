package com.db4o.test.traversal;

import com.db4o.inside.traversal.Visitor;

import java.util.Vector;

/**
 * A polite visitor will visit objects only once.
 * It will not visit the same object over and over again.
 */
public abstract class PoliteVisitor implements Visitor {

	private final Vector _alreadyVisited = new Vector();

	public boolean visit(Object object) {
		if (_alreadyVisited.contains(object)) return false;
		_alreadyVisited.add(object);
		singleVisit(object);
		return true;
	}

	protected abstract void singleVisit(Object object);

}
