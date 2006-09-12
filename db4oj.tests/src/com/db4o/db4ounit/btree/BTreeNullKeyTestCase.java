package com.db4o.db4ounit.btree;

import com.db4o.*;
import com.db4o.inside.btree.*;

import db4ounit.*;



public class BTreeNullKeyTestCase extends BTreeTestCaseBase {	

	public void testSingleRemoveAddNull() {
		
		final Integer element = null;
		add(element);		
		assertSize(1);
		
		remove(element);		
		assertSize(0);
		
		add(element);
		
		assertSingleElement(element);
	}
	
	public void testMultipleNullKeys() {
		
		final Integer[] keys = new Integer[] { new Integer(1), null, new Integer(2), null, new Integer(3) };
		for (int idx = 0; idx < keys.length; idx++) {
			add(keys[idx]);
		}
		commit();

		BTreeRange range = _btree.search(trans(), null);
		Assert.areEqual(2,range.size());

		BTreeNodeSearchResult lower=_btree.searchLeaf(trans(), null,SearchTarget.LOWEST);
		BTreeNodeSearchResult higher=_btree.searchLeaf(trans(), null,SearchTarget.HIGHEST);
		range=lower.createIncludingRange(higher);
		Assert.areEqual(2,range.size());		
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

	public static void main(String[] args) {
		new BTreeNullKeyTestCase().runSolo();
	}
}
