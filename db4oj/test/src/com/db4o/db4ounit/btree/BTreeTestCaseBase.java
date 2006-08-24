/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.btree;

import com.db4o.*;
import com.db4o.foundation.KeyValueIterator;
import com.db4o.foundation.Visitor4;
import com.db4o.inside.btree.*;

import db4ounit.Assert;
import db4ounit.extensions.*;


public class BTreeTestCaseBase extends Db4oTestCase{

    public static ExpectingVisitor createExpectingVisitor(int value, int count) {
	    int[] values = new int[count];
	    for (int i = 0; i < values.length; i++) {
	        values[i] = value;
	    }
	    return new ExpectingVisitor(toObjectArray(values));
	}
    
    public static ExpectingVisitor createExpectingVisitor(int[] keys) {
    	return new ExpectingVisitor(toObjectArray(keys));
	}

	public static Object[] toObjectArray(int[] values) {
	    Object[] ret = new Object[values.length];
	    for (int i = 0; i < values.length; i++) {
	        ret[i] = new Integer(values[i]);
	    }
	    return ret;
	}

	protected static void traverseKeys(BTreeRange result, Visitor4 expectingVisitor) {
		final KeyValueIterator i = result.iterator();
		while (i.moveNext()) {
			expectingVisitor.visit(i.key());
		} 
	}
    
    protected static void traverseValues(BTreeRange result, ExpectingVisitor expectingVisitor) {
        final KeyValueIterator i = result.iterator();
        while (i.moveNext()) {
            expectingVisitor.visit(i.key());
        } 
    }

	protected YapStream stream() {
        return (YapStream) db();
    }

    protected Transaction trans() {
        return stream().getTransaction();
    }
    
    protected Transaction systemTrans() {
        return stream().getSystemTransaction();
    }

    protected BTree createIntKeyBTree(int id) {
        return new BTree(trans(), id, new YInt(stream()));
    }

    protected BTree createIntKeyValueBTree(int id) {
        return new BTree(trans(), id, new YInt(stream()), new YInt(stream()));
    }
    
	protected int occurences(int[] values, int value) {
	    int count = 0;
	    for (int i = 0; i < values.length; i++) {
	        if(values[i] == value){
	            count ++;
	        }
	    }
	    return count;
	}
	
	protected void expectKeysSearch(BTree btree, int[] values) {
	    int lastValue = Integer.MIN_VALUE;
	    for (int i = 0; i < values.length; i++) {
	        if(values[i] != lastValue){
	            ExpectingVisitor expectingVisitor = createExpectingVisitor(values[i], occurences(values, values[i]));
	            BTreeRange range = btree.search(trans(), new Integer(values[i]));
	            traverseKeys(range, expectingVisitor);
	            expectingVisitor.assertExpectations();
	            lastValue = values[i];
	        }
	    }
	}

	protected void expectKeys(BTree btree, final int[] keys) {
	    btree.traverseKeys(trans(), createExpectingVisitor(keys));
	}

	protected void assertEmpty(Transaction transaction, BTree tree) {
        final ExpectingVisitor visitor = new ExpectingVisitor(new Object[0]);
        tree.traverseKeys(transaction, visitor);
    	visitor.assertExpectations();
        Assert.areEqual(0, tree.size(transaction));
    }

	protected void dumpKeys(BTree tree) {
		tree.traverseKeys(trans(), new Visitor4() {
			public void visit(Object obj) {
				System.out.println(obj);
			}
		});
	}

	protected ExpectingVisitor createExpectingVisitor(final int expectedID) {
		return createExpectingVisitor(expectedID, 1);
	}
}
