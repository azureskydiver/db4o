/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.inside.fieldindex;

import com.db4o.*;
import com.db4o.foundation.*;

public class IndexedPath extends IndexedNodeBase {
	
	public static IndexedNode newParentPath(IndexedNode next, QCon constraint) {
		if (!canFollowParent(constraint)) {
			return null;
		}
		return new IndexedPath((QConObject) constraint.parent(), next);
	}	
	
	private static boolean canFollowParent(QCon con) {
		final QCon parent = con.parent();
		final YapField parentField = getYapField(parent);
		if (null == parentField) return false;
		final YapField conField = getYapField(con);
		if (null == conField) return false;		
		return parentField.hasIndex()
			&& parentField.getParentYapClass().isAssignableFrom(conField.getParentYapClass());
	}
	
	private static YapField getYapField(QCon con) {
		QField field = con.getField();
		if (null == field) return null;
		return field.getYapField();
	}
	
	private IndexedNode _next;

	public IndexedPath(QConObject parent, IndexedNode next) {
		super(parent);
		_next = next;
	}
	
	public Iterator4 iterator() {		
		return new IndexedPathIterator(this, _next.iterator());
	}

	public int resultSize() {
		throw new NotSupportedException();
	}
}
