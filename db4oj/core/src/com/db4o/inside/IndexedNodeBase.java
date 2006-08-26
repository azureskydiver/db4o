package com.db4o.inside;

import com.db4o.*;
import com.db4o.foundation.KeyValueIterator;
import com.db4o.inside.btree.*;

public abstract class IndexedNodeBase  implements IndexedNode {

	protected final Transaction _transaction;
	protected final QConObject _constraint;
	protected BTreeRange _range;

	public IndexedNodeBase(Transaction transaction, QConObject qcon, BTreeRange range) {
		_transaction = transaction;
        _constraint = qcon;
        _range = range;
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
		final QCon parent = constraint().parent();
		return null == parent || !parent.hasParent();
	}

	private BTreeNodeSearchResult searchBound(int bound, Object keyPart) {
	    return getIndex().searchLeaf(_transaction, new FieldIndexKey(bound, keyPart), SearchTarget.LOWEST);
	}

	protected BTreeRange search(final Object value) {
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

}