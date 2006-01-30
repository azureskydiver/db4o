package com.db4o.test.traversal;

import java.util.Vector;

import com.db4o.inside.traversal.Traverser;

/** A polite visitor will visit objects only once.
 * It will not visit the same object over and over again. */
public abstract class PoliteVisitor implements Traverser.Visitor {

	private final Vector _alreadyVisited = new Vector();

	public boolean visit(Object object) {
		if (_alreadyVisited.contains(object)) return false;
		_alreadyVisited.add(object);
		singleVisit(object);
		return true;
	}

	protected abstract void singleVisit(Object object);

}
