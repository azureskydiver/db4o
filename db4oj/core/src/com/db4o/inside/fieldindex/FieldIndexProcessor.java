package com.db4o.inside.fieldindex;

import com.db4o.*;
import com.db4o.foundation.*;

public class FieldIndexProcessor {

	private final QCandidates _candidates;

	public FieldIndexProcessor(QCandidates candidates) {
		_candidates = candidates;
	}
	
	public FieldIndexProcessorResult run() {
		IndexedNode bestIndex = selectBestIndex();
		if (null == bestIndex) {
			return FieldIndexProcessorResult.NO_INDEX_FOUND;
		}
		if (bestIndex.resultSize() > 0) {
			return new FieldIndexProcessorResult(resolveFully(bestIndex));
		}
		return FieldIndexProcessorResult.FOUND_INDEX_BUT_NO_MATCH;
	}

	private TreeInt resolveFully(IndexedNode bestIndex) {
		if (bestIndex.isResolved()) {
			return bestIndex.toTreeInt();
		}
		return resolveFully(bestIndex.resolve());
	}
	
	public IndexedNode selectBestIndex() {		
		final Iterator4 i = collectIndexedNodes();
		if (!i.moveNext()) {
			return null;
		}
		
		IndexedNode best = (IndexedNode)i.current();
		while (i.moveNext()) {
			IndexedNode leaf = (IndexedNode)i.current();
			if (leaf.resultSize() < best.resultSize()) {
				best = leaf;
			}
		}
		return best;
	}

	private Iterator4 collectIndexedNodes() {
		final Collection4 nodes = new Collection4();
		collectIndexedNodes(nodes, _candidates.iterateConstraints());		
		return nodes.iterator();
	}

	private void collectIndexedNodes(final Collection4 nodes, final Iterator4 qcons) {
		
		while (qcons.moveNext()) {
			QCon qcon = (QCon)qcons.current();
			if (isLeaf(qcon)) {
				if (qcon.canLoadByIndex() && qcon instanceof QConObject) {					
					final QConObject conObject = (QConObject) qcon;
					if (conObject.hasJoins()) {
						collectionJoinedNode(nodes, conObject);
					} else {
						collectStandaloneNode(nodes, conObject);
					}
				}
			} else {
				collectIndexedNodes(nodes, qcon.iterateChildren());
			}
		}
	}

	private void collectStandaloneNode(final Collection4 nodes, final QConObject conObject) {
		IndexedLeaf existing = findLeafOnSameField(nodes, conObject);
		if (existing != null) {
			collectImplicitAnd(nodes, existing, conObject);
		} else {
			nodes.add(new IndexedLeaf(conObject));
		}
	}

	private void collectionJoinedNode(Collection4 nodes, QConObject conObject) {
		QConJoin join = findTopLevelJoin(conObject);
		nodes.add(nodeForConstraint(join));
	}

	private QConJoin findTopLevelJoin(QCon conObject) {
		final Iterator4 i = conObject.i_joins.iterator();
		if (!i.moveNext()) {
			return null;
		}
		QConJoin join = (QConJoin)i.current();
		if (!join.hasJoins()) {
			return join;
		}
		return findTopLevelJoin(join);
	}

	private IndexedNodeWithRange nodeForConstraint(QConJoin join) {
		final IndexedNodeWithRange c1 = nodeForConstraint(join.i_constraint1);
		final IndexedNodeWithRange c2 = nodeForConstraint(join.i_constraint2);
		if (join.isOr()) {
			return new OrIndexedLeaf(join, c1, c2);
		}
		return new AndIndexedLeaf(join, c1, c2);
	}
	
	private IndexedNodeWithRange nodeForConstraint(QCon con) {
		if (con instanceof QConJoin) {
			return nodeForConstraint((QConJoin)con);
		}
		return new IndexedLeaf((QConObject)con);
	}

	private void collectImplicitAnd(final Collection4 nodes, IndexedLeaf existing, final QConObject conObject) {
		nodes.remove(existing);
		nodes.add(new AndIndexedLeaf(existing.constraint(), existing, new IndexedLeaf(conObject)));
	}

	private IndexedLeaf findLeafOnSameField(Collection4 nodes, QConObject conObject) {
		final Iterator4 i = nodes.iterator();
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
