/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.fieldindex;

import com.db4o.*;
import com.db4o.db4ounit.btree.*;
import com.db4o.foundation.*;
import com.db4o.inside.btree.*;
import com.db4o.query.Query;
import com.db4o.reflect.ReflectClass;

import db4ounit.Assert;


public class FieldIndexTestCase extends FieldIndexTestCaseBase {
	
	private static final int[] FOOS = new int[]{3,7,9,4};
    
    public static void main(String[] arguments) {
        new FieldIndexTestCase().runSolo();
    }
    
	public void store() {
		storeItems(FOOS);
	}
    
    public void testAllThere() throws Exception{
        for (int i = 0; i < FOOS.length; i++) {
            Query q = createQuery(FOOS[i]);
            ObjectSet objectSet = q.execute();
            Assert.areEqual(1, objectSet.size());
            FieldIndexItem fii = (FieldIndexItem) objectSet.next();
            Assert.areEqual(FOOS[i], fii.foo);
        }
    }

	public void testAccessingBTree() throws Exception{
    	
        YapStream stream = (YapStream)db();
        ReflectClass claxx = stream.reflector().forObject(new FieldIndexItem());
        YapClass yc = stream.getYapClass(claxx, false);
        YapField yf = yc.getYapField("foo");
        BTree bTree = yf.getIndex();
        
        Assert.isNotNull(bTree);
        expectKeysSearch(bTree, FOOS);
    }
    
    protected void expectKeysSearch(BTree btree, int[] values) {
        int lastValue = Integer.MIN_VALUE;
        for (int i = 0; i < values.length; i++) {
            if(values[i] != lastValue){
                final ExpectingVisitor expectingVisitor = createExpectingVisitor(values[i], occurences(values, values[i]));
                BTreeRange range = fieldIndexKeySearch(trans(), btree, new Integer(values[i]));
                traverseKeys(range, new Visitor4() {
                    public void visit(Object obj) {
                        FieldIndexKey fik = (FieldIndexKey)obj;
                        expectingVisitor.visit(fik.value());
                    }
                });
                expectingVisitor.assertExpectations();
                lastValue = values[i];
            }
        }
    }
    
    private FieldIndexKey fieldIndexKey(int integerPart, Object composite){
        return new FieldIndexKey(integerPart, composite);
    }
    
    public BTreeRange fieldIndexKeySearch(Transaction trans, BTree btree, Object key) {
        // SearchTarget should not make a difference, HIGHEST is faster
        BTreeNodeSearchResult start = btree.searchLeaf(trans, fieldIndexKey(0, key), SearchTarget.LOWEST);
        BTreeNodeSearchResult end = btree.searchLeaf(trans, fieldIndexKey(Integer.MAX_VALUE, key), SearchTarget.LOWEST);
        return start.createIncludingRange(trans, end);
    }
    



    
}
