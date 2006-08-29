package com.db4o.inside.fieldindex;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.inside.btree.*;

public abstract class IndexedNodeBase  implements IndexedNode {
	
	private final QConObject _constraint;

	public IndexedNodeBase(QConObject qcon) {
		if (null == qcon) {
			throw new ArgumentNullException();
		}
		if (null == qcon.getField()) {
			throw new IllegalArgumentException();
		}
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
		final QCon parent = constraint().parent();
		return null == parent || !parent.hasParent();
	}

	private BTreeNodeSearchResult searchBound(int bound, Object keyPart) {
	    return getIndex().searchLeaf(transaction(), new FieldIndexKey(bound, keyPart), SearchTarget.LOWEST);
	}

	public BTreeRange search(final Object value) {
		BTreeNodeSearchResult lowerBound = searchLowerBound(value);
	    BTreeNodeSearchResult upperBound = searchUpperBound(value);	    
		return lowerBound.createIncludingRange(upperBound);
	}

	private BTreeNodeSearchResult searchUpperBound(final Object value) {
		return searchBound(Integer.MAX_VALUE, value);
	}

	private BTreeNodeSearchResult searchLowerBound(final Object value) {
		return searchBound(0, value);
	}

	public static TreeInt addRangeToTree(TreeInt tree, final BTreeRange range) {
		final KeyValueIterator i = range.iterator();
	    while (i.moveNext()) {
	        FieldIndexKey composite = (FieldIndexKey)i.key();
	        tree = (TreeInt) Tree.add(tree, new TreeInt(composite.parentID()));
	    }
		return tree;
	}

	public IndexedNode resolve() {
		if (isResolved()) {
			return null;
		}
		return IndexedPath.newParentPath(this, constraint());
	}

	private Transaction transaction() {
		return constraint().transaction();
	}

}