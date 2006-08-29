package com.db4o.db4ounit.fieldindex;

import com.db4o.TreeInt;
import com.db4o.inside.*;
import com.db4o.inside.fieldindex.IndexedNode;
import com.db4o.query.Query;

import db4ounit.Assert;

public class IndexedNodeTestCase extends FieldIndexProcessorTestCaseBase {
	
	public static void main(String[] args) {
		new IndexedNodeTestCase().runSolo();
	}
	
	public void store() {
		storeItems(new int[] { 3, 4, 7, 9 });
		storeComplexItems(
						new int[] { 3, 4, 7, 9 },
						new int[] { 2, 2, 8, 8 });
	}

	public void testDoubleDescendingOnIndexedNodes() {
		final Query query = createComplexItemQuery();
		query.descend("child").descend("foo").constrain(new Integer(3));
		query.descend("bar").constrain(new Integer(2));
		
		final IndexedNode index = selectBestIndex(query);
		assertComplexItemIndex("foo", index);
		
		Assert.isFalse(index.isResolved());
		
		IndexedNode result = index.resolve();
		Assert.isNotNull(result);
		assertComplexItemIndex("child", result);
		
		Assert.isTrue(result.isResolved());
		Assert.isNull(result.resolve());
		
		assertComplexItems(new int[] { 4 }, result.toTreeInt());
	}	
	
	public void testTripleDescendingOnQuery() {
		final Query query = createComplexItemQuery();
		query.descend("child").descend("child").descend("foo").constrain(new Integer(3));
		
		final IndexedNode index = selectBestIndex(query);
		assertComplexItemIndex("foo", index);
		
		Assert.isFalse(index.isResolved());
		
		IndexedNode result = index.resolve();
		Assert.isNotNull(result);
		assertComplexItemIndex("child", result);
		
		Assert.isFalse(result.isResolved());
		result = result.resolve();
		Assert.isNotNull(result);
		assertComplexItemIndex("child", result);
		
		assertComplexItems(new int[] { 7 } , result.toTreeInt());
	}
	
	private void assertComplexItems(final int[] expectedFoos, final TreeInt found) {
		Assert.isNotNull(found);
		assertTreeInt(
				mapToObjectIds(createComplexItemQuery(), expectedFoos),
				found);
	}
}
