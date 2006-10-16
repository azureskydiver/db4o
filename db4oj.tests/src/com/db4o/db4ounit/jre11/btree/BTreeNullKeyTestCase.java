package com.db4o.db4ounit.jre11.btree;

import com.db4o.*;
import com.db4o.db4ounit.common.btree.*;
import com.db4o.foundation.ArgumentNullException;
import com.db4o.inside.btree.*;

import db4ounit.*;



public class BTreeNullKeyTestCase extends BTreeTestCaseBase {	

	public void testKeysCantBeNull() {
		final Integer value = null;
		Assert.expect(ArgumentNullException.class, new CodeBlock() {
			public void run() throws Exception {
				add(value);
			}
		});
	}

	public void testMultipleNullFieldIndexKeys() {
		YapStream stream=trans().stream();
		FieldIndexKeyHandler keyHandler = new FieldIndexKeyHandler(stream,new YInt(stream));
		BTree btree=new BTree(trans(), 0, keyHandler, null, 7, stream.configImpl().bTreeCacheHeight());
		
		final Integer[] keys = new Integer[] { new Integer(1), null, new Integer(2), null, new Integer(3) };
		for (int idx = 0; idx < keys.length; idx++) {
			btree.add(trans(),new FieldIndexKey(42,keys[idx]));
		}
		commit();

		BTreeRange range = btree.search(trans(), new FieldIndexKey(42,null));
		Assert.areEqual(2,range.size());

		BTreeNodeSearchResult lower=btree.searchLeaf(trans(), new FieldIndexKey(42,null),SearchTarget.LOWEST);
		BTreeNodeSearchResult higher=btree.searchLeaf(trans(), new FieldIndexKey(42,null),SearchTarget.HIGHEST);
		range=lower.createIncludingRange(higher);
		Assert.areEqual(2,range.size());		
	}
	
	private void add(Object element) {
		_btree.add(trans(), element);
	}
	
	public static void main(String[] args) {
		new BTreeNullKeyTestCase().runSolo();
	}
}
