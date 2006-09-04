package com.db4o.inside.fieldindex;

import com.db4o.*;
import com.db4o.foundation.*;

public class IndexedNodeCollector {

	private final Collection4 _nodes;
	
	private final Hashtable4 _visitedNodes;

	public IndexedNodeCollector(QCandidates candidates) {
		_nodes = new Collection4();
		_visitedNodes = new Hashtable4();
		collectIndexedNodes(candidates);
	}
	
	public Iterator4 getNodes() {
		return _nodes.iterator();
	}
	
	private void collectIndexedNodes(QCandidates candidates) {
		collectIndexedNodes(candidates.iterateConstraints());
	}

	private void collectIndexedNodes(final Iterator4 qcons) {
		
		while (qcons.moveNext()) {
			QCon qcon = (QCon)qcons.current();
			if (isLeaf(qcon)) {
				if (qcon.canLoadByIndex() && qcon instanceof QConObject) {					
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

	private void collectStandaloneNode(final QConObject conObject) {
		IndexedLeaf existing = findLeafOnSameField(conObject);
		if (existing != null) {
			collectImplicitAnd(existing, conObject);
		} else {
			_nodes.add(new IndexedLeaf(conObject));
		}
	}

	private void collectJoinedNode(QConObject constraintWithJoins) {
		Collection4 joins = collectTopLevelJoins(constraintWithJoins);
		if (1 == joins.size()) {
			_nodes.add(nodeForConstraint((QConJoin)joins.singleElement()));
			return;
		}
		collectImplicitlyAndingJoins(joins, constraintWithJoins);
	}

	private void collectImplicitlyAndingJoins(Collection4 joins, QConObject constraintWithJoins) {
		final Iterator4 i = joins.iterator();
		i.moveNext();
		IndexedNodeWithRange last = nodeForCurrentJoin(i);
		while (i.moveNext()) {
			final IndexedNodeWithRange node = nodeForCurrentJoin(i);
			last = new AndIndexedLeaf(constraintWithJoins, node, last);
			_nodes.add(last);
		}
	}

	private Collection4 collectTopLevelJoins(QConObject constraintWithJoins) {
		Collection4 joins = new Collection4();
		constraintWithJoins.collectTopLevelJoins(joins);
		return joins;
	}

	private IndexedNodeWithRange nodeForCurrentJoin(final Iterator4 i) {
		final QConJoin join = (QConJoin)i.current();
		return nodeForConstraint(join);
	}

	private IndexedNodeWithRange nodeForConstraint(QConJoin join) {
		final IndexedNodeWithRange c1 = nodeForConstraint(join.i_constraint1);
		final IndexedNodeWithRange c2 = nodeForConstraint(join.i_constraint2);
		if (join.isOr()) {
			return new OrIndexedLeaf(join.i_constraint1, c1, c2);
		}
		return new AndIndexedLeaf(join.i_constraint1, c1, c2);
	}
	
	private IndexedNodeWithRange nodeForConstraint(QCon con) {
		if (con instanceof QConJoin) {
			return nodeForConstraint((QConJoin)con);
		}
		return new IndexedLeaf((QConObject)con);
	}

	private void collectImplicitAnd(IndexedLeaf existing, final QConObject conObject) {
		_nodes.remove(existing);
		_nodes.add(new AndIndexedLeaf(
						existing.constraint(),
						existing,
						new IndexedLeaf(conObject)));
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
