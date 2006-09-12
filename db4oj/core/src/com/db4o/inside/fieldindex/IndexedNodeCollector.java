package com.db4o.inside.fieldindex;

import com.db4o.*;
import com.db4o.foundation.*;

public class IndexedNodeCollector {

	private final Collection4 _nodes;
	
	private final Hashtable4 _nodeCache;

	public IndexedNodeCollector(QCandidates candidates) {
		_nodes = new Collection4();
		_nodeCache = new Hashtable4();
		collectIndexedNodes(candidates);
	}
	
	public Iterator4 getNodes() {
		return _nodes.iterator();
	}
	
	private void collectIndexedNodes(QCandidates candidates) {
		collectIndexedNodes(candidates.iterateConstraints());
		implicitlyAndJoinsOnSameField();
	}

	private void implicitlyAndJoinsOnSameField() {
		final Object[] nodes = _nodes.toArray();
		for (int i = 0; i < nodes.length; i++) {
			Object node = nodes[i];
			if (node instanceof OrIndexedLeaf) {
				OrIndexedLeaf current = (OrIndexedLeaf) node;
				OrIndexedLeaf other = findJoinOnSameFieldAtSameLevel(current);
				if (null != other) {
					nodes[Arrays4.indexOf(nodes, other)] = null;
					collectImplicitAnd(current.getConstraint(), current, other);
				}
			}
		}
	}

	private OrIndexedLeaf findJoinOnSameFieldAtSameLevel(OrIndexedLeaf join) {
		final Iterator4 i = _nodes.iterator();
		while (i.moveNext()) {
			if (i.current() == join) {
				continue;
			}
			if (i.current() instanceof OrIndexedLeaf) {
				OrIndexedLeaf current = (OrIndexedLeaf) i.current();
				if (current.getIndex() == join.getIndex()
					&& parentConstraint(current) == parentConstraint(join)) {
					return current;
				}
			}
		}
		return null;
	}

	private Object parentConstraint(OrIndexedLeaf node) {
		return node.getConstraint().parent();
	}

	private void collectIndexedNodes(final Iterator4 qcons) {
		
		while (qcons.moveNext()) {
			QCon qcon = (QCon)qcons.current();
			if (isCached(qcon)) {
				continue;
			}
			if (isLeaf(qcon)) {
				if (qcon.canLoadByIndex()) {					
					final QConObject conObject = (QConObject) qcon;
					if (conObject.hasJoins()) {
						collectJoinedNode(conObject);
					} else {
						collectStandaloneNode(conObject);
					}
				}
			} else {
				collectIndexedNodes(qcon.iterateChildren());
			}
		}		
	}

	private boolean isCached(QCon qcon) {
		return null != _nodeCache.get(qcon);
	}

	private void collectStandaloneNode(final QConObject conObject) {
		IndexedLeaf existing = findLeafOnSameField(conObject);
		if (existing != null) {
			collectImplicitAnd(conObject, existing, new IndexedLeaf(conObject));
		} else {
			_nodes.add(new IndexedLeaf(conObject));
		}
	}

	private void collectJoinedNode(QConObject constraintWithJoins) {
		Collection4 joins = collectTopLevelJoins(constraintWithJoins);
		if (1 == joins.size()) {
			_nodes.add(nodeForConstraint((QCon)joins.singleElement()));
			return;
		}
		collectImplicitlyAndingJoins(joins, constraintWithJoins);
	}

	private void collectImplicitlyAndingJoins(Collection4 joins, QConObject constraintWithJoins) {
		final Iterator4 i = joins.iterator();
		i.moveNext();
		IndexedNodeWithRange last = nodeForConstraint((QCon)i.current());
		while (i.moveNext()) {
			final IndexedNodeWithRange node = nodeForConstraint((QCon)i.current());
			last = new AndIndexedLeaf(constraintWithJoins, node, last);
			_nodes.add(last);
		}
	}

	private Collection4 collectTopLevelJoins(QConObject constraintWithJoins) {
		Collection4 joins = new Collection4();
		constraintWithJoins.collectTopLevelJoins(joins);
		return joins;
	}

	private IndexedNodeWithRange newNodeForConstraint(QConJoin join) {
		final IndexedNodeWithRange c1 = nodeForConstraint(join.i_constraint1);
		if (c1 instanceof MultiFieldNode) {
			return c1;
		}
		final IndexedNodeWithRange c2 = nodeForConstraint(join.i_constraint2);
		if (c2 instanceof MultiFieldNode) {
			return c2;
		}
		if (c1.getIndex() != c2.getIndex()) {
			return new MultiFieldNode();
		}
		if (join.isOr()) {
			return new OrIndexedLeaf(join.i_constraint1, c1, c2);
		}
		return new AndIndexedLeaf(join.i_constraint1, c1, c2);
	}
	
	private IndexedNodeWithRange nodeForConstraint(QCon con) {
		IndexedNodeWithRange node = (IndexedNodeWithRange) _nodeCache.get(con);
		if (null != node) {
			return node;
		}
		node = newNodeForConstraint(con);
		_nodeCache.put(con, node);
		return node;
	}

	private IndexedNodeWithRange newNodeForConstraint(QCon con) {
		if (con instanceof QConJoin) {
			return newNodeForConstraint((QConJoin)con);
		}
		return new IndexedLeaf((QConObject)con);
	}

	private void collectImplicitAnd(final QCon constraint, IndexedNodeWithRange x, final IndexedNodeWithRange y) {
		_nodes.remove(x);
		_nodes.remove(y);
		_nodes.add(new AndIndexedLeaf(constraint, x, y));
	}

	private IndexedLeaf findLeafOnSameField(QConObject conObject) {
		final Iterator4 i = _nodes.iterator();
		while (i.moveNext()) {
			if (i.current() instanceof IndexedLeaf) {
				IndexedLeaf leaf = (IndexedLeaf)i.current();
				if (conObject.onSameFieldAs(leaf.constraint())) {
					return leaf;
				}
			}
		}
		return null;
	}

	private boolean isLeaf(QCon qcon) {
		return !qcon.hasChildren();
	}
}
