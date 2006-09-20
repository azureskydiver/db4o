package com.db4o.db4ounit.fieldindex;

import com.db4o.TreeInt;
import com.db4o.foundation.Iterator4;
import com.db4o.inside.fieldindex.*;
import com.db4o.query.*;

import db4ounit.Assert;

public class IndexedNodeTestCase extends FieldIndexProcessorTestCaseBase {
	
	public static void main(String[] args) {
		new IndexedNodeTestCase().runSolo();
	}
	
	protected void store() {
		storeItems(new int[] { 3, 4, 7, 9 });
		storeComplexItems(
						new int[] { 3, 4, 7, 9 },
						new int[] { 2, 2, 8, 8 });
	}
	
	public void testTwoLevelDescendOr() {
    	Query query = createComplexItemQuery();
        Constraint c1 = query.descend("child").descend("foo").constrain(new Integer(4)).smaller();
        Constraint c2 = query.descend("child").descend("foo").constrain(new Integer(4)).greater();        
        c1.or(c2);
        
        assertSingleOrNode(query);
	}
	
	public void testMultipleOrs() {
    	Query query = createComplexItemQuery();    	
        Constraint c1 = query.descend("foo").constrain(new Integer(4)).smaller();
        for (int i = 0; i < 5; i++) {
        	Constraint c2 = query.descend("foo").constrain(new Integer(4)).greater();        
        	c1 = c1.or(c2);
        }
        assertSingleOrNode(query);
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
	
	private void assertSingleOrNode(Query query) {
		Iterator4 nodes = createProcessor(query).collectIndexedNodes();
        Assert.isTrue(nodes.moveNext());
        
        OrIndexedLeaf node = (OrIndexedLeaf)nodes.current();
        Assert.isNotNull(node);
        
        Assert.isFalse(nodes.moveNext());
	}
}
