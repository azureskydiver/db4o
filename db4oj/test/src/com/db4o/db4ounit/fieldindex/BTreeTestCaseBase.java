/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.fieldindex;

import com.db4o.*;
import com.db4o.foundation.Visitor4;
import com.db4o.inside.btree.*;

import db4ounit.Assert;
import db4ounit.db4o.*;


public class BTreeTestCaseBase extends Db4oTestCase{

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
        return new BTree(trans(), id, new YInt(stream()), null);
    }

    protected BTree createIntKeyValueBTree(int id) {
        return new BTree(trans(), id, new YInt(stream()), new YInt(stream()));
    }

	protected void expectKeys(BTree btree, final int[] keys) {
	    final int[] cursor = new int[] {0};
	    btree.traverseKeys(trans(), new Visitor4() {
	        public void visit(Object obj) {
	            // System.out.println(obj);
	            Assert.areEqual(keys[cursor[0]], ((Integer)obj).intValue());
	            cursor[0] ++;
	        }
	    });
	    Assert.areEqual(keys.length, cursor[0]);
	}

    protected void assertEmpty(Transaction transaction, BTree tree) {
        final ExpectingVisitor visitor = new ExpectingVisitor(new Object[0]);
        tree.traverseKeys(transaction, visitor);
    	Assert.isTrue(visitor.allAsExpected());
        Assert.areEqual(0, tree.size(transaction));
    }

}
