/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.btree;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.inside.*;
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
public final class BTreeNode extends YapMeta{
    
    private static final int COUNT_LEAF_AND_3_LINK_LENGTH = (YapConst.INT_LENGTH * 4) + 1; 
 
    private static final int SLOT_LEADING_LENGTH = YapConst.LEADING_LENGTH  + COUNT_LEAF_AND_3_LINK_LENGTH;
    
   
    final BTree _btree;
    
    
    private int _count;
    
    private boolean _isLeaf;
    
    
    private Object[] _keys;
    
    /**
     * Can contain BTreeNode or Integer for ID of BTreeNode 
     */
    private Object[] _children;  
    
    /**
     * Only used for leafs
     */
    private Object[] _values;
    
    
    private int _parentID;
    
    private int _previousID;
    
    private int _nextID;
    
    private boolean _cached;
    
    private boolean _dead;
    
    
    
    /* Constructor for new nodes */
    public BTreeNode(BTree btree, 
                     int count, 
                     boolean isLeaf,
                     int parentID, 
                     int previousID, 
                     int nextID){
        _btree = btree;
        _parentID = parentID;
        _previousID = previousID;
        _nextID = nextID;
        _count = count;
        _isLeaf = isLeaf;
        prepareArrays();
        setStateDirty();
    }
    
    /* Constructor for existing nodes, requires valid ID */
    public BTreeNode(int id, BTree btree){
        _btree = btree;
        setID(id);
        setStateDeactivated();
    }
    
    /* Constructor to create a new root from two nodes */
    public BTreeNode(Transaction trans, BTreeNode firstChild, BTreeNode secondChild){
        this(firstChild._btree, 2, false, 0, 0, 0);
        _keys[0] = firstChild._keys[0];
        _children[0] = firstChild;
        _keys[1] = secondChild._keys[0];
        _children[1] = secondChild;
        
        write(trans.systemTransaction());
        
        firstChild.setParentID(trans, getID());
        secondChild.setParentID(trans, getID());
    }
    
    public BTree btree() {
		return _btree;
	}    
    
    /**
     * @return the split node if this node is split
     * or this if the first key has changed
     */
    public BTreeNode add(Transaction trans){
        
        Buffer reader = prepareRead(trans);
        
        Searcher s = search(reader);
        
        if(_isLeaf){
            
            prepareWrite(trans);
            
            if (wasRemoved(trans, s)) {
            	cancelRemoval(trans, s.cursor());
            	return null;
            }
            
            if(s.count() > 0  && ! s.beforeFirst()){
                s.moveForward();
            }
            
            prepareInsert(s.cursor());
            _keys[s.cursor()] = newAddPatch(trans);
            if(handlesValues()){
                _values[s.cursor()] = valueHandler().current();
            }
            
        }else{
            
            BTreeNode childNode = child(reader, s.cursor());
            BTreeNode childNodeOrSplit = childNode.add(trans);
            if(childNodeOrSplit == null){
                return null;
            }
            prepareWrite(trans);
            _keys[s.cursor()] = childNode._keys[0];
            if(childNode != childNodeOrSplit){
                int splitCursor = s.cursor() + 1;
                prepareInsert(splitCursor);
                _keys[splitCursor] = childNodeOrSplit._keys[0];
                _children[splitCursor] = childNodeOrSplit;
            }
        }
        
        if(_count >= _btree.nodeSize()){
            return split(trans);
        }
        
        if(s.cursor() == 0){
            return this;  
        }
        
        return null;
    }

    private BTreeAdd newAddPatch(Transaction trans) {
        sizeIncrement(trans);
        return new BTreeAdd(trans, currentKey());
    }

	private Object currentKey() {
		return keyHandler().current();
	}

	private void cancelRemoval(Transaction trans, int index) {
		final BTreeUpdate patch = (BTreeUpdate)keyPatch(index);
        BTreeUpdate nextPatch = patch.removeFor(trans);
        _keys[index] = newCancelledRemoval(trans, patch.getObject(), nextPatch);
        sizeIncrement(trans); 
	}

	private BTreePatch newCancelledRemoval(Transaction trans, Object originalObject, BTreeUpdate existingPatches) {
		return new BTreeCancelledRemoval(trans, originalObject, currentKey(), existingPatches);
	}

	private void sizeIncrement(Transaction trans) {
		_btree.sizeChanged(trans, 1);
	}

	private boolean wasRemoved(Transaction trans, Searcher s) {
		if (!s.foundMatch()) { 
			return false;
		}
        BTreePatch patch = keyPatch(trans, s.cursor());
        return patch != null && patch.isRemove();
	}
    
    BTreeNodeSearchResult searchLeaf(Transaction trans, SearchTarget target) {
        Buffer reader = prepareRead(trans);
        Searcher s = search(reader, target);
        if(! _isLeaf){
            return child(reader, s.cursor()).searchLeaf(trans, target);
        }
            
        if(! s.foundMatch() || target == SearchTarget.ANY || target == SearchTarget.HIGHEST){
            return new BTreeNodeSearchResult(trans, reader, btree(), s, this);
        }
        
        if(target == SearchTarget.LOWEST){
            BTreeNodeSearchResult res = findLowestLeafMatch(trans, s.cursor() - 1);
            if(res != null){
                return res;
            }
            return createMatchingSearchResult(trans, reader, s.cursor());
        }
        
        throw new IllegalStateException();
        
    }

	private BTreeNodeSearchResult findLowestLeafMatch(Transaction trans, int index){		
		return findLowestLeafMatch(trans, prepareRead(trans), index);
	}
	
	private BTreeNodeSearchResult findLowestLeafMatch(Transaction trans, Buffer reader, int index){
        
        if(index >= 0){
            if(!compareEquals(reader, index)){
                return null;
            }
            if(index > 0){
                BTreeNodeSearchResult res = findLowestLeafMatch(trans, reader, index - 1);
                if(res != null){
                    return res;
                }
                return createMatchingSearchResult(trans, reader, index);
            }
        }
        
        final BTreeNode node = previousNode();
        if(node != null){
        	final Buffer nodeReader = node.prepareRead(trans);
            BTreeNodeSearchResult res = node.findLowestLeafMatch(trans, nodeReader, node.lastIndex());
            if(res != null){
                return res;
            }
        }
        
        if(index < 0){
            return null;
        }
        
        return createMatchingSearchResult(trans, reader, index);
    }

	private boolean compareEquals(final Buffer reader, int index) {
		if(canWrite()){
			return compareInWriteMode(index) == 0;
		}
		return compareInReadMode(reader, index) == 0;
	}

    private BTreeNodeSearchResult createMatchingSearchResult(Transaction trans, Buffer reader, int index) {
        return new BTreeNodeSearchResult(trans, reader, btree(), this, index, true);
    }
    
    public boolean canWrite(){
        return _keys != null;
    }
    
    BTreeNode child(int index){
        if (_children[index] instanceof BTreeNode){
            return (BTreeNode)_children[index];
        }
        return _btree.produceNode(((Integer)_children[index]).intValue());
    }
    
    BTreeNode child(Buffer reader, int index){
        if( childLoaded(index) ){
            return (BTreeNode)_children[index];
        }
        BTreeNode child = _btree.produceNode(childID(reader, index));

        if(_children != null){
            if(_cached || child.canWrite()){
                _children[index] = child;
            }
        }
        
        return child;
    }
    
    private int childID(Buffer reader, int index){
        if(_children == null){
            seekChild(reader, index);
            return reader.readInt();
        }
        return childID(index);
    }
    
    private int childID(int index){
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
        return ((BTreeNode)_children[index]).canWrite();
    }
    
    void commit(Transaction trans){
        commitOrRollback(trans, true);
    }
    
    void commitOrRollback(Transaction trans, boolean isCommit){
    	
    	if(DTrace.enabled){
    		DTrace.BTREE_NODE_COMMIT_OR_ROLLBACK.log(getID());
    	}
        
        if(_dead){
            return;
        }
        
        _cached = false;
        
        if(! _isLeaf){
            return;
        }
        
        if(! isDirty(trans)){
            return;
        }
        
        Object keyZero = _keys[0];
        
        boolean vals = handlesValues();
        
        Object[] tempKeys = new Object[_btree.nodeSize()];
        Object[] tempValues = vals ? new Object[_btree.nodeSize()] : null; 
        
        int count = 0;
    
        for (int i = 0; i < _count; i++) {
            Object key = _keys[i];
            BTreePatch patch = keyPatch(i);
            if(patch != null){
                key = isCommit ? patch.commit(trans, _btree) : patch.rollback(trans, _btree); 
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
        
        if(freeIfEmpty(trans)){
            return;
        }
        
        // TODO: Merge nodes here on low _count value.
        
        if(_keys[0] != keyZero){
            tellParentAboutChangedKey(trans);
        }
        
    }
    
    private boolean freeIfEmpty(Transaction trans){
        return freeIfEmpty(trans, _count);
    }
    
    private boolean freeIfEmpty(Transaction trans, int count){
        if(count > 0){
            return false;
        }
        if(isRoot()){
            return false;
        }
        free(trans);
        return true;
    }

	private boolean isRoot() {
		return _btree.root() == this;
	}
    
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if(! (obj instanceof BTreeNode)){
            return false;
        }
        BTreeNode other = (BTreeNode) obj;
        return getID() == other.getID();
    }
    
    public int hashCode() {
    	return getID();
    }
    
    private void free(Transaction trans){
        _dead = true;
        if(! isRoot()){
            BTreeNode parent = _btree.produceNode(_parentID);
            parent.removeChild(trans, this);
        }
        pointPreviousTo(trans, _nextID);
        pointNextTo(trans, _previousID);
        trans.systemTransaction().slotFreePointerOnCommit(getID());
        _btree.removeNode(this);
    }
    
    void holdChildrenAsIDs(){
        if(_children == null){
            return;
        }
        for (int i = 0; i < _count; i++) {
            if(_children[i] instanceof BTreeNode){
                _children[i] = new Integer( ((BTreeNode)_children[i]).getID() );
            }
        }
    }
    
    private void removeChild(Transaction trans, BTreeNode child) {
        prepareWrite(trans);
        int id = child.getID();
        for (int i = 0; i < _count; i++) {
            if(childID(i) == id){
                if(freeIfEmpty(trans, _count -1)){
                    return;
                }
                remove(i);
                if(i <= 1){
                    tellParentAboutChangedKey(trans);
                }
                if(_count == 0){
                    // root node empty case only, have to turn it into a leaf
                    _isLeaf = true;
                    prepareValues();
                }
                return;
            }
        }
        throw new IllegalStateException("child not found");
    }
    
    private void keyChanged(Transaction trans, BTreeNode child) {
        prepareWrite(trans);
        int id = child.getID();
        for (int i = 0; i < _count; i++) {
            if(childID(i) == id){
                _keys[i] = child._keys[0];
                _children[i] = child;
                keyChanged(trans, i);
                return;
            }
        }
        throw new IllegalStateException("child not found");
    }
    
    private void tellParentAboutChangedKey(Transaction trans){
        if(! isRoot()){
            BTreeNode parent = _btree.produceNode(_parentID);
            parent.keyChanged(trans, this);
        }
    }

    private boolean isDirty(Transaction trans){
        if(! canWrite()){
            return false;
        }
        
        for (int i = 0; i < _count; i++) {
            if(keyPatch(trans, i) != null){
                return true;
            }
        }
        
        return false;
    }
    
    private int compareInWriteMode(int index){
        return keyHandler().compareTo(key(index));
    }
    
    private int compareInReadMode(Buffer reader, int index){
        seekKey(reader, index);
        return keyHandler().compareTo(keyHandler().readIndexEntry(reader));
    }
    
    public int count() {
        return _count;
    }
    
    private int entryLength(){
        int len = keyHandler().linkLength();
        if(_isLeaf){
            if(handlesValues()){
                len += valueHandler().linkLength();
            }
        }else{
            len += YapConst.ID_LENGTH;
        }
        return len;
    }
    
    public int firstKeyIndex(Transaction trans) {
    	for (int ix = 0; ix < _count; ix++) {
            if(indexIsValid(trans, ix)){
                return ix;
            }
    	}
    	return -1;
    }
    
	public int lastKeyIndex(Transaction trans) {
    	for (int ix = _count - 1; ix >= 0; ix--) {
            if(indexIsValid(trans, ix)){
                return ix;
            }
    	}
    	return -1;
	}
    
    public boolean indexIsValid(Transaction trans, int index){
        if(!canWrite()){
            return true;
        }
        BTreePatch patch = keyPatch(index);
        if(patch == null){
            return true;
        }
        return patch.key(trans) != No4.INSTANCE; 
    }
    
    private Object firstKey(Transaction trans){
    	final int index = firstKeyIndex(trans);
    	if (-1 == index) {
    		return No4.INSTANCE;
    	}
		return key(trans, index);
    }
    
    public byte getIdentifier() {
        return YapConst.BTREE_NODE;
    }
    
    private boolean handlesValues(){
        return _btree._valueHandler != Null.INSTANCE; 
    }
    
    private void prepareInsert(int pos){
        if(pos > lastIndex()){
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
    
    private void remove(int pos){
    	if(DTrace.enabled){
    		DTrace.BTREE_NODE_REMOVE.log(getID());
    	}
        int len = _count - pos;
        _count--;
        System.arraycopy(_keys, pos + 1, _keys, pos, len);
        _keys[_count] = null;
        if(_values != null){
            System.arraycopy(_values, pos + 1, _values, pos, len);
            _values[_count] = null;
        }
        if(_children != null){
            System.arraycopy(_children, pos + 1, _children, pos, len);
            _children[_count] = null;
        }
    }
    
    Object key(int index){
    	Object obj = _keys[index]; 
        if( obj instanceof BTreePatch){
            return ((BTreePatch)obj).getObject();
        }
        return obj;
    }
    
    Object key(Transaction trans, Buffer reader, int index){
        if(canWrite()){
            return key(trans, index);
        }
        seekKey(reader, index);
        return keyHandler().readIndexEntry(reader);
    }
    
    Object key(Transaction trans, int index){
        BTreePatch patch = keyPatch(index);
        if(patch == null){
            return _keys[index];
        }
        return patch.key(trans);
    }
    
    private BTreePatch keyPatch(int index){
    	Object obj = _keys[index]; 
        if( obj instanceof BTreePatch){
            return (BTreePatch)obj;
        }
        return null;
    }
    
    private BTreePatch keyPatch(Transaction trans, int index){
    	Object obj = _keys[index];
        if( obj instanceof BTreePatch){
            return ((BTreePatch)obj).forTransaction(trans);
        }
        return null;
    }
    
    private Indexable4 keyHandler(){
        return _btree.keyHandler();
    }
    
    void markAsCached(int height){
        _cached = true;
        _btree.addNode(this);
        
        if( _isLeaf || (_children == null)){
            return;
        }
        
        height --;
        
        if(height < 1){
            holdChildrenAsIDs();
            return;
        }
        
        for (int i = 0; i < _count; i++) {
            if(_children[i] instanceof BTreeNode){
                ((BTreeNode)_children[i]).markAsCached(height);
            }
        }
    }
    
    public int ownLength() {
        return SLOT_LEADING_LENGTH
          + (_count * entryLength())
          + YapConst.BRACKETS_BYTES;
    }
    
    Buffer prepareRead(Transaction trans){

        if(canWrite()){
            return null;
        }
        
        if(isNew()){
            return null;
        }
        
        if(_cached){
            read(trans.systemTransaction());
            _btree.addToProcessing(this);
            return null;
        }
        
        Buffer reader = trans.i_file.readReaderByID(trans.systemTransaction(), getID());
        
        if (Deploy.debug) {
            reader.readBegin(getIdentifier());
        }
        
        readNodeHeader(reader);
        
        return reader;
    }

    void prepareWrite(Transaction trans){
        
        if(_dead){
            return;
        }
        
        if(canWrite()){
            setStateDirty();
            return;
        }
        
        read(trans.systemTransaction());
        setStateDirty();
        _btree.addToProcessing(this);
    }
    
    private void prepareArrays(){
        if(canWrite()){
            return;
        }
        _keys = new Object[_btree.nodeSize()];
        if(_isLeaf){
            prepareValues();
        }else{
            _children = new Object[_btree.nodeSize()];
        }
    }
    
    private void prepareValues(){
        if(handlesValues()){
            _values = new Object[_btree.nodeSize()];
        }
    }
    
    private void readNodeHeader(Buffer reader){
        _count = reader.readInt();
        byte leafByte = reader.readByte();
        _isLeaf = (leafByte == 1);
        _parentID = reader.readInt();
        _previousID = reader.readInt();
        _nextID = reader.readInt();
    }
    
    public void readThis(Transaction trans, Buffer reader) {
        readNodeHeader(reader);

        prepareArrays();

        boolean isInner = ! _isLeaf;
        boolean vals = handlesValues() && _isLeaf;
        for (int i = 0; i < _count; i++) {
            _keys[i] = keyHandler().readIndexEntry(reader);
            if(vals){
                _values[i] = valueHandler().readIndexEntry(reader);
            }else{
                if(isInner){
                    _children[i] = new Integer(reader.readInt());
                }
            }
        }
    }
    
    public void remove(Transaction trans, int index){
        if(!_isLeaf){
            throw new IllegalStateException();
        }
            
        prepareWrite(trans);
        
        BTreePatch patch = keyPatch(index);
        
        // no patch, no problem, can remove
        if(patch == null){
            _keys[index] = newRemovePatch(trans);
            keyChanged(trans, index);
            return;
        }
        
        BTreePatch transPatch = patch.forTransaction(trans);
        if(transPatch != null){
            if(transPatch.isAdd()){
                cancelAdding(trans, index);
                return;
            }
        }else{
            // If the patch is a removal of a cancelled removal for another
            // transaction, we need one for this transaction also.
            if(! patch.isAdd()){
                ((BTreeUpdate)patch).append(newRemovePatch(trans));
                return;
            }
        }
        
        // now we try if removal is OK for the next element in this node
        if(index != lastIndex()){
            if(compareInWriteMode(index + 1 ) != 0){
                return;
            }
            remove(trans, index + 1);
            return;
        }
        
        // nothing else worked so far, move on to the next node, try there
        BTreeNode node = nextNode();
        
        if(node == null){
            return;
        }
        
        node.prepareWrite(trans);
        if(node.compareInWriteMode(0) != 0){
            return;
        }
        
        node.remove(trans, 0);
    }

	private void cancelAdding(Transaction trans, int index) {
		_btree.notifyRemoveListener(keyPatch(index).getObject());
		if(freeIfEmpty(trans, _count-1)){
			sizeDecrement(trans);
			return;
		}
		remove(index);
		keyChanged(trans, index);
        sizeDecrement(trans);
	}

	private void sizeDecrement(Transaction trans) {
		_btree.sizeChanged(trans, -1);
	}

	private int lastIndex() {
		return _count - 1;
	}

	private BTreeUpdate newRemovePatch(Transaction trans) {
        _btree.sizeChanged(trans, -1);
		return new BTreeRemove(trans, currentKey());
	}

	private void keyChanged(Transaction trans, int index) {
		if(index == 0){
		    tellParentAboutChangedKey(trans);
		}
	}
    
    
    void rollback(Transaction trans){
        commitOrRollback(trans, false);
    }
    
    private Searcher search(Buffer reader){
        return search(reader, SearchTarget.ANY);
    }
    
    private Searcher search(Buffer reader, SearchTarget target){
        Searcher s = new Searcher(target, _count);
        if(canWrite()){
            while(s.incomplete()){
            	s.resultIs( compareInWriteMode(s.cursor()));
            }
        }else{
            while(s.incomplete()){
            	s.resultIs( compareInReadMode(reader, s.cursor()));
            }
        }
        return s;
    }
    
    private void seekAfterKey(Buffer reader, int ix){
        seekKey(reader, ix);
        reader._offset += keyHandler().linkLength();
    }
    
    private void seekChild(Buffer reader, int ix){
        seekAfterKey(reader, ix);
    }
    
    private void seekKey(Buffer reader, int ix){
        reader._offset = SLOT_LEADING_LENGTH + (entryLength() * ix);
    }
    
    private void seekValue(Buffer reader, int ix){
        if(handlesValues()){
            seekAfterKey(reader, ix);
        }else{
            seekKey(reader, ix);
        }
    }
    
    private BTreeNode split(Transaction trans){
        
        
        BTreeNode res = new BTreeNode(_btree, _btree._halfNodeSize, _isLeaf,_parentID, getID(), _nextID);
        
        System.arraycopy(_keys, _btree._halfNodeSize, res._keys, 0, _btree._halfNodeSize);
        for (int i = _btree._halfNodeSize; i < _keys.length; i++) {
            _keys[i] = null;
        }
        if(_values != null){
            res._values = new Object[_btree.nodeSize()];
            System.arraycopy(_values, _btree._halfNodeSize, res._values, 0, _btree._halfNodeSize);
            for (int i = _btree._halfNodeSize; i < _values.length; i++) {
                _values[i] = null;
            }
        }
        if(_children != null){
            res._children = new Object[_btree.nodeSize()];
            System.arraycopy(_children, _btree._halfNodeSize, res._children, 0, _btree._halfNodeSize);
            for (int i = _btree._halfNodeSize; i < _children.length; i++) {
                _children[i] = null;
            }
        }
        
        _count = _btree._halfNodeSize;
        
        res.write(trans.systemTransaction());
        _btree.addNode(res);
        
        int splitID = res.getID();
        
        pointNextTo(trans, splitID);
        
        setNextID(trans, splitID);

        if(_children != null){
            for (int i = 0; i < _btree._halfNodeSize; i++) {
                if(res._children[i] == null){
                    break;
                }
                res.child(i).setParentID(trans, splitID );
            }
        }
        return res;
    }
    
    private void pointNextTo(Transaction trans, int id){
        if(_nextID != 0){
            nextNode().setPreviousID(trans, id);
        }
    }

    private void pointPreviousTo(Transaction trans, int id){
        if(_previousID != 0){
            previousNode().setNextID(trans, id);
        }
    }

    public BTreeNode previousNode() {
        if(_previousID == 0){
            return null;
        }
        return _btree.produceNode(_previousID);
    }
    
    public BTreeNode nextNode() {
        if(_nextID == 0){
            return null;
        }
        return _btree.produceNode(_nextID);
    }
    
	BTreePointer firstPointer(Transaction trans) {
        Buffer reader = prepareRead(trans);
		if (_isLeaf) {
            return leafFirstPointer(trans, reader);
		}
        return branchFirstPointer(trans, reader);
	}

	private BTreePointer branchFirstPointer(Transaction trans, Buffer reader) {
		for (int i = 0; i < _count; i++) {
            BTreePointer childFirstPointer = child(reader, i).firstPointer(trans);
            if(childFirstPointer != null){
                return childFirstPointer;
            }
        }
		return null;
	}

	private BTreePointer leafFirstPointer(Transaction trans, Buffer reader) {
		int index = firstKeyIndex(trans);
		if(index == -1){
			return null;
		}
		return new BTreePointer(trans, reader, this, index);
	}
	
	public BTreePointer lastPointer(Transaction trans) {
        Buffer reader = prepareRead(trans);
		if (_isLeaf) {
            return leafLastPointer(trans, reader);
		}
        return branchLastPointer(trans, reader);
	}

	private BTreePointer branchLastPointer(Transaction trans, Buffer reader) {
		for (int i = _count - 1; i >= 0; i--) {
            BTreePointer childLastPointer = child(reader, i).lastPointer(trans);
            if(childLastPointer != null){
                return childLastPointer;
            }
        }
		return null;
	}

	private BTreePointer leafLastPointer(Transaction trans, Buffer reader) {
		int index = lastKeyIndex(trans);
		if(index == -1){
			return null;
		}
		return new BTreePointer(trans, reader, this, index);
	}
    
    void purge(){
        if(_dead){
            _keys = null;
            _values = null;
            _children = null;
            return;
        }
        
        if(_cached){
            return;
        }
        
        if(!canWrite()){
            return;
        }
        
        for (int i = 0; i < _count; i++) {
            if(_keys[i] instanceof BTreePatch){
                holdChildrenAsIDs();
                _btree.addNode(this);
                return;
            }
        }
    }
    
    private void setParentID(Transaction trans, int id){
        prepareWrite(trans);
        _parentID = id;
        setStateDirty();
    }
    
    private void setPreviousID(Transaction trans, int id){
        prepareWrite(trans);
        _previousID = id;
        setStateDirty();
    }
    
    private void setNextID(Transaction trans, int id){
        prepareWrite(trans);
        _nextID = id;
        setStateDirty();
    }
    
    public void traverseKeys(Transaction trans, Visitor4 visitor){
        Buffer reader = prepareRead(trans);
        if(_isLeaf){
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
        Buffer reader = prepareRead(trans);
        if(_isLeaf){
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
    
    Object value(int index) {
    	return _values[index];
    }
    
    Object value(Buffer reader, int index){
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
        if(_dead){
            return false;
        }
        if(!canWrite()){
            return false;
        }
        return super.writeObjectBegin();
    }
    
    
    public void writeThis(Transaction trans, Buffer a_writer) {
        
        int count = 0;
        int startOffset = a_writer._offset;
        
        a_writer.incrementOffset(COUNT_LEAF_AND_3_LINK_LENGTH);

        if(_isLeaf){
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
                    keyHandler().writeIndexEntry(a_writer, key(i));
                    a_writer.writeIDOf(trans, _children[i]);
                }
            }
        }
        
        int endOffset = a_writer._offset;
        a_writer._offset = startOffset;
        a_writer.writeInt(count);
        a_writer.append( _isLeaf ? (byte) 1 : (byte) 0);
        a_writer.writeInt(_parentID);
        a_writer.writeInt(_previousID);
        a_writer.writeInt(_nextID);
        a_writer._offset = endOffset;

    }
    
    public String toString() {
        if(_count == 0){
            return "Node " + getID() + " not loaded";
        }
        String str = "\nBTreeNode";
        str += "\nid: " + getID();
        str += "\nparent: " + _parentID;
        str += "\nprevious: " + _previousID;
        str += "\nnext: " + _nextID;
        str += "\ncount:" + _count;
        str += "\nleaf:" + _isLeaf + "\n";
        
        if(canWrite()){
            
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

	public void debugLoadFully(Transaction trans) {
		prepareWrite(trans);
		if (_isLeaf) {
			return;
		}
		for (int i=0; i<_count; ++i) {
            if(_children[i] instanceof Integer){
                _children[i] = btree().produceNode(((Integer)_children[i]).intValue());
            }
            ((BTreeNode)_children[i]).debugLoadFully(trans);
		}
	}

	public static void defragIndex(ReaderPair readers,Indexable4 keyHandler,Indexable4 valueHandler) {
        if (Deploy.debug) {
            readers.readBegin(YapConst.BTREE_NODE);
        }
		// count
		int count=readers.readInt();
		// leafByte
        byte leafByte = readers.readByte();
        boolean isLeaf = (leafByte == 1);
        boolean handlesValues=(valueHandler!=null)&&isLeaf;

        readers.copyID(); // parent ID
        readers.copyID(); // previous ID
        readers.copyID(); // next ID

        for (int i = 0; i < count; i++) {
            keyHandler.defragIndexEntry(readers);
            if(handlesValues){
                valueHandler.defragIndexEntry(readers);
            }else{
                if(!isLeaf){
                	readers.copyID();
                }
            }
        }
        if (Deploy.debug) {
            readers.readEnd();
        }
	}

    public boolean isLeaf() {
        return _isLeaf;
    }

    /** This traversal goes over all nodes, not just leafs */
    void traverseAllNodes(Transaction trans, Visitor4 command) {
        Buffer reader = prepareRead(trans);
        command.visit(this);
        if(_isLeaf){
            return;
        }
        for (int childIdx=0;childIdx<_count;childIdx++) {
            child(reader, childIdx).traverseAllNodes(trans, command);
        }
    }
}
