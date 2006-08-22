/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.btree;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.inside.btree.*;

import db4ounit.*;


public class BTreeSimpleTestCase extends BTreeTestCaseBase{
    
    public BTreeSimpleTestCase(){
        super();
        
        _values = new int[_keys.length];
        for (int i = 0; i < _keys.length; i++) {
            _values[i] = _keys[i];
        }
        
        _sortedValues = new int[_sortedKeys.length];
        for (int i = 0; i < _sortedKeys.length; i++) {
            _sortedValues[i] = _sortedKeys[i];
        }
        
        _valuesOnRemoval = new int[_keysOnRemoval.length];
        for (int i = 0; i < _keysOnRemoval.length; i++) {
            _valuesOnRemoval[i] = _keysOnRemoval[i];
        }
    }
    
    public void testIntKeys() throws Exception{
        BTree btree = createIntKeyBTree(0);
        for (int i = 0; i < 5; i++) {
            btree = cycleIntKeys(btree);    
        }
    }

    public void testIntKeysIntValues() throws Exception{
        BTree btree = createIntKeyValueBTree(0);
        for (int i = 0; i < 5; i++) {
            btree = cycleIntKeysIntValues(btree);    
        }
    }
    
    
    private BTree cycleIntKeys(BTree btree) throws Exception{
        addKeys(btree);
        expectKeys(btree, _sortedKeys);
        
        btree.commit(trans());
        
        expectKeys(btree, _sortedKeys);
        
        removeKeys(btree);
        
        expectKeys(btree, _keysOnRemoval);
        
        btree.rollback(trans());
        
        expectKeys(btree, _sortedKeys);
        
        int id = btree.getID();
        
        reopen();
        
        btree = createIntKeyBTree(id);
        
        expectKeys(btree, _sortedKeys);
        
        removeKeys(btree);
        
        expectKeys(btree, _keysOnRemoval);
        
        btree.commit(trans());
        
        expectKeys(btree, _keysOnRemoval);
        
        // remove all but 1
        for (int i = 1; i < _keysOnRemoval.length; i++) {
            btree.remove(trans(), new Integer(_keysOnRemoval[i]));
        }
        
        expectKeys(btree, _one);
        
        btree.commit(trans());
        
        expectKeys(btree, _one);
        
        btree.remove(trans(), new Integer(1));
        
        btree.rollback(trans());
        
        expectKeys(btree, _one);
        
        btree.remove(trans(), new Integer(1));
        
        btree.commit(trans());
        
        expectKeys(btree, _none);
        
        return btree;
        
    }
    
    
    private BTree cycleIntKeysIntValues(BTree btree) throws Exception{
        
        addKeysValues(btree);
        expectKeys(btree, _sortedKeys);
        expectValues(btree, _sortedValues);
        
        btree.commit(trans());
        
        expectKeys(btree, _sortedKeys);
        expectValues(btree, _sortedValues);
        
        removeKeys(btree);
        
        expectKeys(btree, _keysOnRemoval);
        expectValues(btree, _valuesOnRemoval);
        
        btree.rollback(trans());
        
        expectKeys(btree, _sortedKeys);
        expectValues(btree, _sortedValues);
        
        int id = btree.getID();
        
        reopen();
        
        btree = createIntKeyValueBTree(id);
        
        expectKeys(btree, _sortedKeys);
        expectValues(btree, _sortedValues);
        
        removeKeys(btree);
        
        expectKeys(btree, _keysOnRemoval);
        expectValues(btree, _valuesOnRemoval);
        
        btree.commit(trans());
        
        expectKeys(btree, _keysOnRemoval);
        expectValues(btree, _valuesOnRemoval);
        
        // remove all but 1
        for (int i = 1; i < _keysOnRemoval.length; i++) {
            btree.remove(trans(), new Integer(_keysOnRemoval[i]));
        }
        
        expectKeys(btree, _one);
        expectValues(btree, _one);
        
        btree.commit(trans());
        
        expectKeys(btree, _one);
        expectValues(btree, _one);
        
        btree.remove(trans(), new Integer(1));
        
        btree.rollback(trans());
        
        expectKeys(btree, _one);
        expectValues(btree, _one);
        
        btree.remove(trans(), new Integer(1));
        
        btree.commit(trans());
        
        expectKeys(btree, _none);
        expectValues(btree, _none);
        
        return btree;
    }
    
    
    private void removeKeys(BTree btree){
        btree.remove(trans(), new Integer(3));
        btree.remove(trans(), new Integer(101));
    }
    
    
    private void addKeys(BTree btree){
        Transaction trans = trans(); 
        for (int i = 0; i < _keys.length; i++) {
            btree.add(trans, new Integer(_keys[i]));
        }
    }
    
    private void addKeysValues(BTree btree){
        Transaction trans = trans(); 
        for (int i = 0; i < _keys.length; i++) {
            btree.add(trans, new Integer(_keys[i]), new Integer(_values[i]));
        }
    }
    
    private void expectValues(BTree btree, final int[] values){
        final int[] cursor = new int[] {0};
        btree.traverseValues(trans(), new Visitor4() {
            public void visit(Object obj) {
                // System.out.println(obj);
                Assert.areEqual(values[cursor[0]], ((Integer)obj).intValue());
                cursor[0] ++;
            }
        });
        Assert.areEqual(values.length, cursor[0]);
    }

    
    int[] _keys = {3, 234, 55, 87, 2, 1, 101, 59, 70, 300, 288};
    
    int[] _values;
    
    int[] _sortedKeys = {1, 2, 3, 55, 59, 70, 87, 101, 234, 288, 300};
    
    int[] _sortedValues;
    
    int[] _keysOnRemoval = {1, 2, 55, 59, 70, 87, 234, 288, 300};
    
    int[] _valuesOnRemoval;
    
    int[] _one = {1};
    
    int[] _none = {};

}
