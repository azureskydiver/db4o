package com.db4o.test.traversal;

import com.db4o.inside.replication.CollectionHandlerImpl;
import com.db4o.inside.traversal.GenericTraverser;
import com.db4o.inside.traversal.Traverser;
import com.db4o.reflect.Reflector;
import com.db4o.test.Test;

public class TraversalTest extends Test {

	public void test() {
		Traverser traverser = new GenericTraverser(reflector(), new CollectionHandlerImpl());

		TraversalTestSubject subject = new TraversalTestSubject();
		CountingVisitor visitor = new CountingVisitor();

		traverser.traverseGraph(subject, visitor);
		ensureEquals(subject.objectsReferenced() + 1, visitor._objectsVisited);
	}

	private Reflector reflector() {
		return objectContainer().reflector();
	}

	private class CountingVisitor extends PoliteVisitor {
		private int _objectsVisited = 0;

		protected void singleVisit(Object object) {
			_objectsVisited++;
		}

	}

}
