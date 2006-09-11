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

    public TreeInt toTreeInt() {
    	return addToTree(null, this);
    }
	
	public final BTree getIndex() {
	    return getYapField().getIndex(transaction());
	}

	private YapField getYapField() {
	    return _constraint.getField().getYapField();
	}

	public QCon constraint() {
	    return _constraint;
	}

	public boolean isResolved() {
		final QCon parent = constraint().parent();
		return null == parent || !parent.hasParent();
	}

	public BTreeRange search(final Object value) {
		return getYapField().search(transaction(), value);
	}

	public static TreeInt addToTree(TreeInt tree, final IndexedNode node) {
	    Iterator4 i = node.iterator();
		while (i.moveNext()) {
		    FieldIndexKey composite = (FieldIndexKey)i.current();
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