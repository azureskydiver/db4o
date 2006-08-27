package com.db4o.inside;

import com.db4o.*;
import com.db4o.foundation.KeyValueIterator;
import com.db4o.inside.btree.*;

public abstract class IndexedNodeBase  implements IndexedNode {
	
	private final Transaction _transaction;
	private final QConObject _constraint;

	public IndexedNodeBase(Transaction transaction, QConObject qcon) {
		_transaction = transaction;
        _constraint = qcon;
	}
	
	public BTree getIndex() {
	    return getYapField().getIndex();
	}

	private YapField getYapField() {
	    return _constraint.getField().getYapField();
	}

	public QConObject constraint() {
	    return _constraint;
	}

	public boolean isResolved() {
		final QCon parent = parentConstraint();
		return null == parent || !parent.hasParent();
	}

	private BTreeNodeSearchResult searchBound(int bound, Object keyPart) {
	    return getIndex().searchLeaf(_transaction, new FieldIndexKey(bound, keyPart), SearchTarget.LOWEST);
	}

	public BTreeRange search(final Object value) {
		BTreeNodeSearchResult lowerBound = searchLowerBound(value);
	    BTreeNodeSearchResult upperBound = searchUpperBound(value);	    
		return lowerBound.createIncludingRange(_transaction, upperBound);
	}

	private BTreeNodeSearchResult searchUpperBound(final Object value) {
		return searchBound(Integer.MAX_VALUE, value);
	}

	private BTreeNodeSearchResult searchLowerBound(final Object value) {
		return searchBound(0, value);
	}

	protected TreeInt addRangeToTree(TreeInt tree, final BTreeRange range) {
		final KeyValueIterator i = range.iterator();
	    while (i.moveNext()) {
	        FieldIndexKey composite = (FieldIndexKey)i.key();
	        tree = (TreeInt) Tree.add(tree, new TreeInt(composite.parentID()));
	    }
		return tree;
	}

	protected IndexedNode parentNode() {
		QCon parent = parentConstraint();
		if (parent instanceof QConObject) {
			return new IndexedPath(_transaction, (QConObject) parent, this);
		}
		return null;
	}

	protected QCon parentConstraint() {
		return constraint().parent();
	}

	public IndexedNode resolve() {
		if (isResolved()) {
			return null;
		}
		return parentNode();
	}

}