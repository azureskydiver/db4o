/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.btree;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.inside.ix.*;

/**
 * We work with BTreeNode in two states:
 * 
 * - deactivated: never read, no valid members, ID correct or 0 if new
 * - write: real representation of keys, values and children in arrays
 * The write state can be detected with canWrite(). States can be changed
 * as needed with prepareRead() and prepareWrite().
 * 
 * @exclude
 */
public class BTreeNode extends YapMeta{
    
    private static final int MAX_ENTRIES = 4;

    private static final int HALF_ENTRIES = MAX_ENTRIES / 2;
    
    private static final int SLOT_LEADING_LENGTH = YapConst.LEADING_LENGTH +  YapConst.YAPINT_LENGTH * 2; 

    
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
    
    
    private int _parentID;
    
    private int _previousID;
    
    private int _nextID;
    
    
    
    /* Constructor for new nodes */
    public BTreeNode(BTree btree, int count, int height){
        _btree = btree;
        _count = count;
        _height = height;
        prepareArrays();
        setStateDirty();
        btree.addNewNode(this);
    }
    
    /* Constructor for existing nodes, requires valid ID */
    public BTreeNode(int id, BTree btree){
        _btree = btree;
        setID(id);
        setStateDeactivated();
    }
    
    
    /**
     * @return a split node if the node is split
     * or the first key, if the first key has changed
     */
    public Object add(Transaction trans){
        
        YapReader reader = prepareRead(trans);
        
        Searcher s = search(trans, reader);
        
        if(isLeaf()){
            
            prepareWrite(trans);
            

            // TODO: Anything special on exact match?  Possibly compare value part also?
            
//            if(s._cmp == 0){
//                
//            }
            
            // Leaf only: Check last comparison result and position
            //            beyond last if added is greater.
            if(s._cmp < 0){
                s._cursor ++;
            }
            
            insert(trans, s._cursor);
            _keys[s._cursor] = new BTreeAdd(trans, keyHandler().current());
            if(handlesValues()){
                _values[s._cursor] = valueHandler().current();
            }
            
        }else{
            
            BTreeNode childNode = child(reader, s._cursor);
            Object addResult = childNode.add(trans);
            if(addResult == null){
                return null;
            }
            prepareWrite(trans);
            _keys[s._cursor] = childNode._keys[0];
            if(addResult instanceof BTreeNode){
                BTreeNode splitChild = (BTreeNode)addResult;
                int splitCursor = s._cursor + 1;
                insert(trans, splitCursor);
                _keys[splitCursor] = splitChild._keys[0];
                _children[splitCursor] = splitChild;
            }
        }
        
        if(_count == MAX_ENTRIES){
            return split(trans);
        }
        
        if(s._cursor == 0){
            return _keys[0];
        }
        
        return null;
    }
    
    private boolean canWrite(){
        return _keys != null;
    }
    
    private BTreeNode child(YapReader reader, int index){
        if( childLoaded(index) ){
            return (BTreeNode)_children[index];
        }
        BTreeNode child = _btree.produceNode(childID(reader, index));
        
        // TODO: Check exactly, when we want to keep a reference
        // to children. This should probably happen from the child.
        
        if(_children != null){
            _children[index] = child; 
        }
        return child;
    }
    
    private int childID(YapReader reader, int index){
        if(_children == null){
            seekChild(reader, index);
            return reader.readInt();
        }
        if(childLoaded(index)){
            return ((BTreeNode)_children[index]).getID();
        }
        return ((Integer)_children[index]).intValue();
    }
    
    
    private boolean childLoaded(int index){
        if(_children == null){
            return false;
        }
        return _children[index] instanceof BTreeNode;
    }
    
    private boolean childCanSupplyFirstKey(int index){
        if(! childLoaded(index)){
            return false;
        }
        return ((BTreeNode)_children[index])._keys != null;
    }
    
    void commit(Transaction trans){
        
        if(! canWrite()){
            return;
        }
        
        if(! isDirty(trans)){
            return;
        }
        
        
        // TODO: Here we are doing writes twice.
        // New nodes will already have been written in BTree#commit().
        // Currently they are rewritten on commit.
        setStateDirty();
        write(trans);
        
        
        if(isLeaf()){
            
            boolean vals = handlesValues();
            
            Object[] tempKeys = new Object[MAX_ENTRIES];
            Object[] tempValues = vals ? new Object[MAX_ENTRIES] : null; 
            
            int count = 0;
        
            for (int i = 0; i < _count; i++) {
                Object key = _keys[i];
                BTreePatch patch = keyPatch(i);
                if(patch != null){
                    key = patch.commit(trans, _btree);
                }
                if(key != No4.INSTANCE){
                    tempKeys[count] = key;
                    if(vals){
                        tempValues[count] = _values[i];
                    }
                    count ++;
                }
            }
            
            _keys = tempKeys;
            _values = tempValues;
            
            _count = count;
            
            // TODO: Merge nodes here on low _count value.
        }
        
    }
    
    private boolean isDirty(Transaction trans){
        if(! canWrite()){
            return false;
        }
        
        for (int i = 0; i < _count; i++) {
            BTreePatch patch = keyPatch(i);
            if(patch != null){
                if(patch.forTransaction(trans) != null){
                    return true;
                }
            }
        }
        
        return false;
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
    
    private int entryLength(){
        int len = keyHandler().linkLength();
        if(isLeaf()){
            if(handlesValues()){
                len += valueHandler().linkLength();
            }
        }else{
            len += YapConst.YAPID_LENGTH;
        }
        return len;
    }
    
    private Object firstKey(Transaction trans){
        for (int ix = 0; ix < _count; ix++) {
            BTreePatch patch = keyPatch(ix);
            if(patch == null){
                return _keys[ix];
            }
            Object obj = patch.getObject(trans);
            if(obj != No4.INSTANCE){
                return obj;
            }
        }
        return No4.INSTANCE;
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
            if(Debug.atHome){
                for (int i = _count + 1; i < _values.length; i++) {
                    _values[i] = null;
                }
            }
        }
        if(_children != null){
            System.arraycopy(_children, pos, _children, pos + 1, len);
            if(Debug.atHome){
                for (int i = _count + 1; i < _children.length; i++) {
                    _children[i] = null;
                }
            }
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
        BTreeNode res = new BTreeNode(_btree, 2, _height + 1 );
        res._keys[0] = _keys[0];
        res._children[0] = this;
        res._keys[1] = peer._keys[0];
        res._children[1] = peer;
        res.write(trans);
        return res;
    }
    
    public int ownLength() {
        return YapConst.OBJECT_LENGTH 
          + YapConst.YAPINT_LENGTH * 2  // height, count
          + _count * entryLength();
    }
    
    private YapReader prepareRead(Transaction trans){
        if(canWrite()){
            return null;
        }
        if(isNew()){
            return null;
        }
        
        YapReader reader = trans.i_file.readReaderByID(trans, getID());
        
        if (Deploy.debug) {
            reader.readBegin(getIdentifier());
        }
        
        
        _count = reader.readInt();
        _height = reader.readInt();
        
        return reader;
    }

    private void prepareWrite(Transaction trans){
        
        if(canWrite()){
            setStateDirty();
            return;
        }
        
        if(isNew()){
            prepareArrays();
            setStateDirty();
            return;
        }
        
        if(! isActive()){
            prepareArrays();
            read(trans);
            setStateDirty();
        }
    }
    
    void prepareArrays(){
        _keys = new Object[MAX_ENTRIES];
        if(isLeaf()){
            if(handlesValues()){
                _values = new Object[MAX_ENTRIES];
            }
        }else{
            _children = new Object[MAX_ENTRIES];
        }
    }
    
    public void readThis(Transaction a_trans, YapReader a_reader) {
        _count = a_reader.readInt();
        _height = a_reader.readInt();
        boolean isInner = ! isLeaf();
        boolean vals = handlesValues() && isLeaf();
        for (int i = 0; i < _count; i++) {
            _keys[i] = keyHandler().readIndexEntry(a_reader);
            if(vals){
                _values[i] = valueHandler().readIndexEntry(a_reader);
            }else{
                if(isInner){
                    _children[i] = new Integer(a_reader.readInt());
                }
            }
        }
    }
    
    public void remove(Transaction trans){
        YapReader reader = prepareRead(trans);
        
        Searcher s = search(trans, reader);
        if(s._cursor < 0){
            return;
        }
        
        if(isLeaf()){
            
            if(s._cmp != 0){
                return;
            }
            
            prepareWrite(trans);
            
            BTreeRemove btr = new BTreeRemove(trans, keyHandler().current());
            
            BTreePatch patch = keyPatch(s._cursor);
            if(patch != null){
                _keys[s._cursor] = patch.append(btr);
            }else{
                _keys[s._cursor] = btr;    
            }
            
            return;
        }
            
        child(reader, s._cursor).remove(trans);
    }
    
    void rollback(Transaction trans){
        
        if(! canWrite()){
            return;
        }
        
        if(isLeaf()){
            
            boolean vals = handlesValues();
            
            Object[] tempKeys = new Object[MAX_ENTRIES];
            Object[] tempValues = vals ? new Object[MAX_ENTRIES] : null; 
            
            int count = 0;
        
            for (int i = 0; i < _count; i++) {
                Object key = _keys[i];
                BTreePatch patch = keyPatch(i);
                if(patch != null){
                    key = patch.rollback(trans, _btree);
                }
                if(key != No4.INSTANCE){
                    tempKeys[count] = key;
                    if(vals){
                        tempValues[count] = _values[i];
                    }
                    count ++;
                }
            }
            
            _keys = tempKeys;
            _values = tempValues;
            
            _count = count;
            
            // TODO: Merge nodes here on low _count value.
        }
    }
    
    private Searcher search(Transaction trans, YapReader reader){
        Searcher s = new Searcher(_count);
        while(s.incomplete()){
            compare(s, reader);
        }
        if(s._cursor < 0){
            s._cursor = 0;
        }else{
            if(! isLeaf()){
            
                // Check last comparison result and step back one if added
                // is smaller than last comparison.
                
                if(s._cmp > 0 && s._cursor > 0){
                    s._cursor --;
                }
            }
        }
        return s;
    }
    
    private void seekAfterKey(YapReader reader, int ix){
        seekKey(reader, ix);
        reader._offset += keyHandler().linkLength();
    }
    
    private void seekChild(YapReader reader, int ix){
        seekAfterKey(reader, ix);
    }
    
    private void seekKey(YapReader reader, int ix){
        reader._offset = SLOT_LEADING_LENGTH + (entryLength() * ix);
    }
    
    private void seekValue(YapReader reader, int ix){
        if(handlesValues()){
            seekAfterKey(reader, ix);
        }else{
            seekKey(reader, ix);
        }
    }
    
    private BTreeNode split(Transaction trans){
        BTreeNode res = new BTreeNode(_btree, HALF_ENTRIES, _height);
        System.arraycopy(_keys, HALF_ENTRIES, res._keys, 0, HALF_ENTRIES);
        if(_values != null){
            res._values = new Object[MAX_ENTRIES];
            System.arraycopy(_values, HALF_ENTRIES, res._values, 0, HALF_ENTRIES);
            if(Debug.atHome){
                for (int i = HALF_ENTRIES; i < _values.length; i++) {
                    _values[i] = null;
                }
            }
        }
        if(_children != null){
            res._children = new Object[MAX_ENTRIES];
            System.arraycopy(_children, HALF_ENTRIES, res._children, 0, HALF_ENTRIES);
            if(Debug.atHome){
                for (int i = HALF_ENTRIES; i < _children.length; i++) {
                    _children[i] = null;
                }
            }

        }
        
        _count = HALF_ENTRIES;
        
        return res;
    }
    
    public void traverseKeys(Transaction trans, Visitor4 visitor){
        YapReader reader = prepareRead(trans);
        if(isLeaf()){
            for (int i = 0; i < _count; i++) {
                Object obj = key(trans,reader, i);
                if(obj != No4.INSTANCE){
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
                if(key(trans,reader, i) != No4.INSTANCE){
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
    
    public boolean writeObjectBegin() {
        if(_keys == null){
            return false;
        }
        return super.writeObjectBegin();
    }
    
    
    public void writeThis(Transaction trans, YapReader a_writer) {
        
        int count = 0;
        int startOffset = a_writer._offset;
        
        a_writer.incrementOffset(YapConst.YAPINT_LENGTH * 2);

        if(isLeaf()){
            boolean vals = handlesValues();
            for (int i = 0; i < _count; i++) {
                Object obj = key(trans, i);
                if(obj != No4.INSTANCE){
                    count ++;
                    keyHandler().writeIndexEntry(a_writer, obj);
                    if(vals){
                        valueHandler().writeIndexEntry(a_writer, _values[i]);
                    }
                }
            }
        }else{
            for (int i = 0; i < _count; i++) {
                if(childCanSupplyFirstKey(i)){
                    BTreeNode child = (BTreeNode)_children[i];
                    Object childKey = child.firstKey(trans);
                    if(childKey != No4.INSTANCE){
                        count ++;
                        keyHandler().writeIndexEntry(a_writer, childKey);
                        a_writer.writeIDOf(trans, child);
                    }
                }else{
                    count ++;
                    keyHandler().writeIndexEntry(a_writer, _keys[i]);
                    a_writer.writeIDOf(trans, _children[i]);
                }
            }
        }
        
        int endOffset = a_writer._offset;
        a_writer._offset = startOffset;
        a_writer.writeInt(count);
        a_writer.writeInt(_height);
        a_writer._offset = endOffset;

    }
    
    public String toString() {
        if(_count == 0 && _height == 0){
            return "Node not loaded";
        }
        String str = "BTreeNode";
        str += " count:" + _count;
        str += " height:" + _height;
        
        if(_keys != null){
            
            str += " { ";
            
            boolean first = true;
            
            for (int i = 0; i < _count; i++) {
                if(_keys[i] != null){
                    if(! first){
                        str += ", ";
                    }
                    str += _keys[i].toString();
                    first = false;
                }
            }
            
            str += " }";
        }
        return str;
    }
    

}
