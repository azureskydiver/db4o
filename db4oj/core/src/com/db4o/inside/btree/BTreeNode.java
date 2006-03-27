/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.btree;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.inside.ix.*;

/**
 * We work with BTreeNode in two states:
 * 
 * - deactivated: never read, no valid members, ID correct or 0 if new
 * - write: real representation of keys, values and childre in arrays
 * The write state can be detected with canWrite(). States can be changed
 * as needed with prepareRead() and prepareWrite().
 * 
 * @exclude
 */
public class BTreeNode extends YapMeta{
    
    private static final int MAX_ENTRIES = 8;

    private static final int HALF_ENTRIES = MAX_ENTRIES / 2;
    
    private static final int LEADING_INT_LENGTH = YapConst.YAPINT_LENGTH * 2; 

    
    final BTree _btree;
    
    
    private int _count;
    
    private int _height;
    
    
    private Object[] _keys;
    
    /**
     * Can contain BTreeNode or Integer for ID of BTreeNode 
     */
    private Object[] _children;  
    
    /**
     * Only used for leafs where _height == 0
     */
    private Object[] _values;
    
    
    /* Constructor for new nodes */
    public BTreeNode(BTree btree){
        _btree = btree;
        setStateClean();
    }
    
    /* Constructor for existing nodes, requires valid ID */
    public BTreeNode(BTree btree, int id){
        _btree = btree;
        setID(id);
        setStateDeactivated();
    }
    
    public BTreeNode add(Transaction trans){
        YapReader reader = prepareRead(trans);
        Searcher s = search(trans, reader);
        if(s._cursor < 0){
            s._cursor = 0;
        }
        if(isLeaf()){
            if(s._cmp == 0){
                prepareWrite(trans);
                
                
                
            }else{
                // Check last comparison result and position beyond last
                // if added is greater.
                if(s._cmp < 0){
                    s._cursor ++;
                }
                insert(trans, s._cursor);
                _keys[s._cursor] = new BTreeAdd(trans, keyHandler().current());
                if(handlesValues()){
                    _values[s._cursor] = valueHandler().current();
                }
            }
        }else{
            BTreeNode splitChild = child(reader, s._cursor).add(trans);
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
    
    private boolean canWrite(){
        return _keys != null;
    }
    
    private BTreeNode child(YapReader reader, int index){
        if( ! childLoaded(index) ){
            _children[index] = _btree.produceNode(((Integer)_children[index]).intValue());
        }
        return (BTreeNode)_children[index];
    }
    
    private int childID(int index){
        if(childLoaded(index)){
            return ((BTreeNode)_children[index]).getID();
        }
        return ((Integer)_children[index]).intValue();
    }
    
    private boolean childLoaded(int index){
        return _children[index] instanceof BTreeNode;
    }
    
    void commit(Transaction trans){
        
        
    }
    
    private void compare(Searcher s, YapReader reader){
        Indexable4 handler = keyHandler();
        if(_keys != null){
            s.resultIs(handler.compareTo(key(s._cursor)));
        }else{
            seekKey(reader, s._cursor);
            s.resultIs(handler.compareTo(handler.readIndexEntry(reader)));
        }
    }
    
    public byte getIdentifier() {
        return YapConst.BTREE_NODE;
    }
    
    private boolean handlesValues(){
        return _btree._valueHandler != Null.INSTANCE; 
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
    
    private Object key(int index){
        BTreePatch patch = keyPatch(index);
        if(patch == null){
            return _keys[index];
        }
        return patch._object;
    }
    
    private Object key(Transaction trans, YapReader reader, int index){
        if( _keys != null ){
            return key(trans, index);
        }
        seekKey(reader, index);
        return keyHandler().readIndexEntry(reader);
    }
    
    private Object key(Transaction trans, int index){
        BTreePatch patch = keyPatch(index);
        if(patch == null){
            return _keys[index];
        }
        return patch.getObject(trans);
    }
    
    private BTreePatch keyPatch(int index){
        if( _keys[index] instanceof BTreePatch){
            return (BTreePatch)_keys[index];
        }
        return null;
    }
    
    private Indexable4 keyHandler(){
        return _btree._keyHandler;
    }
    
    BTreeNode newRoot(Transaction trans, BTreeNode peer){
        BTreeNode res = new BTreeNode(_btree);
        res._height = _height + 1;
        res._count = 2;
        res.prepareWrite(trans);
        res._keys[0] = _keys[0];
        res._children[0] = this;
        res._keys[1] = peer._keys[0];
        res._children[1] = peer;
        return res;
    }
    
    public int ownLength() {
        int length = YapConst.YAPINT_LENGTH * 2;  // height, count
        length += _count * keyHandler().linkLength();
        if(isLeaf()){
            if(handlesValues()){
                length += _count * valueHandler().linkLength();
            }
        }else{
           length += _count * YapConst.YAPID_LENGTH;
        }
        return length;
    }
    
    private YapReader prepareRead(Transaction trans){
        if(canWrite()){
            return null;
        }
        if(isNew()){
            return null;
        }
        
        YapReader reader = trans.i_file.readReaderByID(trans, getID());
        _count = reader.readInt();
        _height = reader.readInt();
        
        return reader;
    }

    private void prepareWrite(Transaction trans){
        if(canWrite()){
            return;
        }
        if(isNew()){
            _keys = new Object[MAX_ENTRIES];
            if(isLeaf()){
                if(handlesValues()){
                    _values = new Object[MAX_ENTRIES];
                }
            }else{
                _children = new Object[MAX_ENTRIES];
            }
            return;
        }
        if(! isActive()){
            read(trans);
        }
    }
    
    public void readThis(Transaction a_trans, YapReader a_reader) {
        _count = a_reader.readInt();
        _height = a_reader.readInt();
        boolean vals = handlesValues() && isLeaf();
        for (int i = 0; i < _count; i++) {
            _keys[i] = keyHandler().readIndexEntry(a_reader);
            if(vals){
                _values[i] = valueHandler().readIndexEntry(a_reader);
            }
        }
        if(! isLeaf()){
            for (int i = 0; i < _count; i++) {
                _children[i] = new Integer(a_reader.readInt());
            }
        }
    }
    
    public BTreeNode remove(Transaction trans){
        YapReader reader = prepareRead(trans);
        Searcher s = search(trans, reader);
        if(s._cursor < 0){
            return this;
        }
        if(isLeaf()){
            if(s._cmp == 0){
                prepareWrite(trans);
                
                Object obj = _keys[s._cursor];
                
                if(obj instanceof BTreePatch){
                    
                }
                
                
                
                
            }else{
                // Check last comparison result and position beyond last
                // if added is greater.
                if(s._cmp < 0){
                    s._cursor ++;
                }
                insert(trans, s._cursor);
                _keys[s._cursor] = new BTreeAdd(trans, keyHandler().current());
                if(handlesValues()){
                    _values[s._cursor] = valueHandler().current();
                }
            }
        }else{
            child(reader, s._cursor).remove(trans);
            
        }
        return this;
    }

    
    void rollback(Transaction trans){
        
    }
    
    private Searcher search(Transaction trans, YapReader reader){
        Searcher s = new Searcher(_count);
        while(s.incomplete()){
            compare(s, reader);
        }
        return s;
    }
    
    private void seekChild(YapReader reader, int ix){
        reader._offset = LEADING_INT_LENGTH + 
             (keyHandler().linkLength() * _count) +
             (YapConst.YAPID_LENGTH * ix);
    }
    
    private void seekKey(YapReader reader, int ix){
        reader._offset = LEADING_INT_LENGTH + (keyHandler().linkLength() * ix);
    }
    
    private void seekValue(YapReader reader, int ix){
        if(valueHandler() == null){
            seekKey(reader, ix);
            return;
        }
        reader._offset = LEADING_INT_LENGTH + 
            (keyHandler().linkLength() * _count) +
            (valueHandler().linkLength() * ix);
    }

    
    public void setID(int a_id) {
        if(getID() == 0){
            _btree.addNode(a_id, this);
        }
        super.setID(a_id);
    }

    private BTreeNode split(Transaction trans){
        BTreeNode res = new BTreeNode(_btree);
        res.prepareWrite(trans);
        System.arraycopy(_keys, HALF_ENTRIES, res._keys, 0, HALF_ENTRIES);
        if(_values != null){
            res._values = new Object[MAX_ENTRIES];
            System.arraycopy(_values, HALF_ENTRIES, res._values, 0, HALF_ENTRIES);
        }
        if(_children != null){
            res._children = new Object[MAX_ENTRIES];
            System.arraycopy(_children, HALF_ENTRIES, res._children, 0, HALF_ENTRIES);
        }
        res._count = HALF_ENTRIES;
        
        _count = HALF_ENTRIES;
        
        return res;
    }
    
    public void traverseKeys(Transaction trans, Visitor4 visitor){
        YapReader reader = prepareRead(trans);
        if(isLeaf()){
            for (int i = 0; i < _count; i++) {
                Object obj = key(trans,reader, i);
                if(obj != Null.INSTANCE){
                    visitor.visit(obj);
                }
            }
        }else{
            for (int i = 0; i < _count; i++) {
                child(reader,i).traverseKeys(trans, visitor);
            }
        }
    }
    
    public void traverseValues(Transaction trans, Visitor4 visitor){
        if(! handlesValues()){
            traverseKeys(trans, visitor);
            return;
        }
        YapReader reader = prepareRead(trans);
        if(isLeaf()){
            for (int i = 0; i < _count; i++) {
                if(key(trans,reader, i) != Null.INSTANCE){
                    visitor.visit(value(reader, i));
                }
            }
        }else{
            for (int i = 0; i < _count; i++) {
                child(reader,i).traverseValues(trans, visitor);
            }
        }
    }
    
    private Object value(YapReader reader, int index){
        if( _values != null ){
            return _values[index];
        }
        seekValue(reader, index);
        return valueHandler().readIndexEntry(reader);
    }

    
    private Indexable4 valueHandler(){
        return _btree._valueHandler;
    }
    
    
    public void writeThis(Transaction trans, YapReader a_writer) {
        if(isLeaf()){
            int count = 0;
            int startOffset = a_writer._offset;
            a_writer.incrementOffset(YapConst.YAPINT_LENGTH * 2);
            
            boolean vals = handlesValues();
            for (int i = 0; i < _count; i++) {
                Object obj = key(trans, i);
                if(obj != Null.INSTANCE){
                    count ++;
                    keyHandler().writeIndexEntry(a_writer, obj);
                    if(vals){
                        valueHandler().writeIndexEntry(a_writer, _values[i]);
                    }
                }
            }
            
            int endOffset = a_writer._offset;
            a_writer._offset = startOffset;
            a_writer.writeInt(count);
            a_writer.writeInt(_height);
            a_writer._offset = endOffset;
        }else{
            a_writer.writeInt(_count);
            a_writer.writeInt(_height);
            for (int i = 0; i < _count; i++) {
                keyHandler().writeIndexEntry(a_writer, _keys[i]);
            }
            for (int i = 0; i < _count; i++) {
                a_writer.writeIDOf(trans, _children[i]);
            }
        }
    }
    

}
