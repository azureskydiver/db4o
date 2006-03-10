/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.btree;

import com.db4o.*;
import com.db4o.inside.ix.*;

/**
 * We work with BTreeNode in three states:
 * 
 * - deactivated: never read, no valid members, ID correct or 0 if new
 * - read: _address, _count and _height are valid
 * - write: real representation of keys, values and childre in arrays
 * The above states can be detected with canRead() and canWrite() and 
 * changed as needed with prepareRead() and prepareWrite().
 * 
 * @exclude
 */
public class BTreeNode extends YapMeta{
    
    private static final int MAX_ENTRIES = 8;

    private static final int HALF_ENTRIES = MAX_ENTRIES / 2;
    
    
    final Indexable4 _handler;
    
    
    private Object[] _keys;
    
    private BTreeNode[] _children;
    
    private Object[] _values;

    
    private int _count;
    
    private int _height;
    
    
    private int _address;

    
    /* Constructor for new nodes */
    public BTreeNode(Indexable4 handler){
        _handler = handler;
        setStateClean();
    }
    
    /* Constructor for existing nodes, requires valid ID */
    public BTreeNode(Indexable4 handler, int id){
        _handler = handler;
        setID(id);
        setStateDeactivated();
    }
    
    public BTreeNode add(Transaction trans){
        
        Object obj = _handler.current();
        
        if(isLeaf()){
            int xxx = 1;
        }
        

        prepareRead(trans);
        Searcher s = search(trans);
        if(s._cursor < 0){
            s._cursor = 0;
        }
        
        if(isLeaf()){
            // Check last comparison result and position beyond last
            // if added is greater.
            if(s._cmp < 0){
                s._cursor ++;
            }
            insert(trans, s._cursor);
            _keys[s._cursor] = _handler.current();
        }else{
            BTreeNode splitChild = _children[s._cursor].add(trans);
            if(splitChild == null){
                return null;
            }
            s._cursor ++;
            insert(trans, s._cursor);
            _keys[s._cursor] = splitChild._keys[0];
            _children[s._cursor] = splitChild;
        }
        setStateDirty();
        if(_count == MAX_ENTRIES){
            return split(trans);
        }
        return null;
    }
    
    private void compare(Searcher s){
        if(_keys != null){
            s.resultIs(_handler.compareTo(_keys[s._cursor]));
        }else{
            
            
        }
    }
    
    BTreeNode newRoot(Transaction trans, BTreeNode peer){
        BTreeNode res = new BTreeNode(_handler);
        res._height = _height + 1;
        res._count = 2;
        res.prepareWrite(trans);
        res._keys[0] = _keys[0];
        res._children[0] = this;
        res._keys[1] = peer._keys[0];
        res._children[1] = peer;
        return res;
    }
    
    
    public byte getIdentifier() {
        return YapConst.BTREE_NODE;
    }
    
    private void insert(Transaction trans, int pos){
        prepareWrite(trans);
        if(pos < 0){
            pos = 0;
        }
        if(pos > _count -1){
            _count ++;
            return;
        }
        int len = _count - pos;
        System.arraycopy(_keys, pos, _keys, pos + 1, len);
        if(_values != null){
            System.arraycopy(_values, pos, _values, pos + 1, len);
        }
        if(_children != null){
            System.arraycopy(_children, pos, _children, pos + 1, len);
        }
        _count++;
    }
    
    private boolean isLeaf(){
        return _height == 0;
    }
    
    public int ownLength() {
        
        int length = YapConst.YAPINT_LENGTH * 2;  // height, count
        
        
        if(isLeaf()){
            
            
        }
        
        return length;
    }
    
    public void readThis(Transaction a_trans, YapReader a_reader) {
        
        // TODO Auto-generated method stub
        
    }
    

    private void prepareRead(Transaction trans){
        if(canWrite()){
            return;
        }
        if(isNew()){
            return;
        }
        if(! canRead()){
            _address = trans.readSlot(getID())._address;
        }
    }
    
    private boolean canWrite(){
        return _keys != null;
    }
    
    private boolean canRead(){
        return _address != 0;
    }

    private void prepareWrite(Transaction trans){
        if(canWrite()){
            return;
        }
        if(isNew()){
            _keys = new Object[MAX_ENTRIES];
            if(isLeaf()){
                
                // create value array, if we carry values
                
            }else{
             
                _children = new BTreeNode[MAX_ENTRIES];
            }
            return;
        }
        if(! isActive()){
            read(trans);
        }
    }
    
    private Searcher search(Transaction trans){
        Searcher s = new Searcher(_count);
        while(s.incomplete()){
            compare(s);
        }
        return s;
    }
    

    private BTreeNode split(Transaction trans){
        BTreeNode res = new BTreeNode(_handler);
        res.prepareWrite(trans);
        System.arraycopy(_keys, HALF_ENTRIES, res._keys, 0, HALF_ENTRIES);
        if(_values != null){
            res._values = new Object[MAX_ENTRIES];
            System.arraycopy(_values, HALF_ENTRIES, res._values, 0, HALF_ENTRIES);
        }
        if(_children != null){
            res._children = new BTreeNode[MAX_ENTRIES];
            System.arraycopy(_children, HALF_ENTRIES, res._children, 0, HALF_ENTRIES);
        }
        res._count = HALF_ENTRIES;
        
        _count = HALF_ENTRIES;
        
        return res;
    }

    public void writeThis(Transaction trans, YapReader a_writer) {
        
        
        // TODO Auto-generated method stub
        
    }
    

}
