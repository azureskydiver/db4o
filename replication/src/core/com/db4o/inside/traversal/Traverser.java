package com.db4o.inside.traversal;

public interface Traverser {

	/**
	 * Traversal will only stop when visitor.visit(...) returns false, EVEN IN
	 * THE PRESENCE OF CIRCULAR REFERENCES. So it is up to the visitor to detect
	 * circular references if necessary. Transient fields are not visited. The
	 * fields of second class objects such as Strings and Dates are also not visited.
	 */
	void traverseGraph(Object object, Visitor visitor);

	/**
	 * Should only be called during a traversal. Will traverse the graph
	 * for the received object too, using the current visitor. Used to
	 * extend the traversal to a possibly disconnected object graph.
	 */
	void extendTraversalTo(Object disconnected);

}
