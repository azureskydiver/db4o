/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.btree;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.inside.ix.*;
import com.db4o.inside.marshall.MarshallerFamily;

/**
 * @exclude
 */
public class BTree extends YapMeta implements TransactionParticipant {
    
    /** temporary variable for value and search coding */
    private static final boolean DEBUG = false; //MarshallerFamily.BTREE_FIELD_INDEX;
    // private static final boolean DEBUG = false;
    
    private static final byte BTREE_VERSION = (byte)1;
    
    final Indexable4 _keyHandler;
    
    final Indexable4 _valueHandler;
    
    private BTreeNode _root;
   
    /**
     * All instantiated nodes are held in this tree. 
     */
    private TreeIntObject _nodes;
    
    private int _size;
    
    private Visitor4 _removeListener;
    
    private Hashtable4 _sizesByTransaction;
    
    private Queue4 _processing;
    
    private int _nodeSize;
    
    int _halfNodeSize;
    
    private final int _cacheHeight;
    
    public BTree(Transaction trans, int id, Indexable4 keyHandler){
        this(trans, id, keyHandler, null);
    }		
    		
    public BTree(Transaction trans, int id, Indexable4 keyHandler, Indexable4 valueHandler){
    	
    	if (null == keyHandler) {
    		throw new ArgumentNullException();
    	}
        
        _nodeSize = DEBUG ? 7 : trans.stream().configImpl().bTreeNodeSize();
        _halfNodeSize = _nodeSize / 2;
        _nodeSize = _halfNodeSize * 2;
        
        _cacheHeight = trans.stream().configImpl().bTreeCacheHeight();
        
        _keyHandler = keyHandler;
        _valueHandler = (valueHandler == null) ? Null.INSTANCE : valueHandler;
        _sizesByTransaction = new Hashtable4(1);
        if(id == 0){
            setStateDirty();
            _root = new BTreeNode(this, 0, true, 0, 0, 0);
            _root.write(trans.systemTransaction());
            addNode(_root);
            write(trans.systemTransaction());
        }else{
            setID(id);
            setStateDeactivated();
        }
    } 
    
    public int nodeSize() {
		return _nodeSize;
	}

	public void add(Transaction trans, Object key){
        add(trans, key, null);
    }
    
    public void add(Transaction trans, Object key, Object value){
        _keyHandler.prepareComparison(key);
        _valueHandler.prepareComparison(value);
        ensureDirty(trans);
        BTreeNode rootOrSplit = _root.add(trans);
        if(rootOrSplit != null && rootOrSplit != _root){
            _root = new BTreeNode(trans, _root, rootOrSplit);
            _root.write(trans.systemTransaction());
            addNode(_root);
        }
    }

    public void remove(Transaction trans, Object key){
        BTreeRange range = search(trans, key);
        BTreePointer first = range.first();
        if(first == null){
            return;
        }
        ensureDirty(trans);
        BTreeNode node = first.node();
        node.remove(trans, first.index());
    }
    
    public BTreeRange search(Transaction trans, Object key) {
        
        // TODO: Optimize the following.
        //       Part of the search operates against the same nodes.
        //       As long as the bounds are on one node, the search
        //       should walk the nodes in one go.
        
        BTreeNodeSearchResult start = searchLeaf(trans, key, SearchTarget.LOWEST);
        BTreeNodeSearchResult end = searchLeaf(trans, key, SearchTarget.HIGHEST);
        return start.createIncludingRange(trans, end);
    }
    
    public BTreeNodeSearchResult searchLeaf(Transaction trans, Object key, SearchTarget target) {
        ensureActive(trans);
        _keyHandler.prepareComparison(key);
        return _root.searchLeaf(trans, target);
    }
    
    public void commit(final Transaction trans){
        
        final Transaction systemTransAction = trans.systemTransaction();
        
        Object sizeDiff = _sizesByTransaction.get(trans);
        if(sizeDiff != null){
            _size += ((Integer) sizeDiff).intValue();
        }
        _sizesByTransaction.remove(trans);
        
        if(_nodes != null){
            
            processAllNodes();
            while(_processing.hasNext()){
                ((BTreeNode)_processing.next()).commit(trans);
            }
            _processing = null;
            
            if(_nodes != null){
                
                _nodes.traverse(new Visitor4() {
                    public void visit(Object obj) {
                        BTreeNode node = (BTreeNode)((TreeIntObject)obj).getObject();
                        node.setStateDirty();
                        node.write(systemTransAction);
                    }
                });
                
            }
            
        }
        
        setStateDirty();
        write(systemTransAction);
        
        purge();
    }
    
    public void rollback(final Transaction trans){
        
        _sizesByTransaction.remove(trans);
        
        if(_nodes == null){
            return;
        }
        
        processAllNodes();
        while(_processing.hasNext()){
            ((BTreeNode)_processing.next()).rollback(trans);
        }
        _processing = null;
        
        purge();
    }
    
    private void purge(){
        if(_nodes == null){
            return;
        }
        
        Tree temp = _nodes;
        _nodes = null;
        
        if(_cacheHeight > 0){
            _root.markAsCached(_cacheHeight);
        }else{
            _root.holdChildrenAsIDs();
            addNode(_root);
        }
        
        temp.traverse(new Visitor4() {
            public void visit(Object obj) {
                BTreeNode node = (BTreeNode)((TreeIntObject)obj).getObject();
                node.purge();
            }
        });
    }
    
    private void processAllNodes(){
        _processing = new Queue4();
        _nodes.traverse(new Visitor4() {
            public void visit(Object obj) {
                _processing.add(((TreeIntObject)obj).getObject());
            }
        });
    }
    
    private void ensureActive(Transaction trans){
        if(! isActive()){
            read(trans.systemTransaction());
        }
    }
    
    private void ensureDirty(Transaction trans){
        ensureActive(trans);
        trans.enlist(this);
        setStateDirty();
    }
    
    public byte getIdentifier() {
        return YapConst.BTREE;
    }
    
    public void setRemoveListener(Visitor4 vis){
        _removeListener = vis;
    }
    
    public int ownLength() {
        return 1 + YapConst.OBJECT_LENGTH + (YapConst.INT_LENGTH * 2) + YapConst.ID_LENGTH;
    }
    
    BTreeNode produceNode(int id){
        TreeIntObject addtio = new TreeIntObject(id);
        _nodes = (TreeIntObject)Tree.add(_nodes, addtio);
        TreeIntObject tio = (TreeIntObject)addtio.duplicateOrThis();
        BTreeNode node = (BTreeNode)tio.getObject();
        if(node == null){
            node = new BTreeNode(id, this);
            tio.setObject(node);
            addToProcessing(node);
        }
        return node;
    }
    
    void addNode(BTreeNode node){
        _nodes = (TreeIntObject)Tree.add(_nodes, new TreeIntObject(node.getID(), node));
        addToProcessing(node);
    }
    
    void addToProcessing(BTreeNode node){
        if(_processing != null){
            _processing.add(node);
        }
    }
    
    void removeNode(BTreeNode node){
        _nodes = (TreeIntObject)_nodes.removeLike(new TreeInt(node.getID()));
    }
    
    void notifyRemoveListener(Object obj){
        if(_removeListener != null){
            _removeListener.visit(obj);
        }
    }

    public void readThis(Transaction a_trans, YapReader a_reader) {
        a_reader.incrementOffset(1);  // first byte is version, for possible future format changes
        _size = a_reader.readInt();
        _nodeSize = a_reader.readInt();
        _halfNodeSize = nodeSize() / 2;
        _root = produceNode(a_reader.readInt());
    }
    
    public void writeThis(Transaction trans, YapReader a_writer) {
        a_writer.append(BTREE_VERSION);
        a_writer.writeInt(_size);
        a_writer.writeInt(nodeSize());
        a_writer.writeIDOf(trans, _root);
    }
    
    public int size(Transaction trans){
        ensureActive(trans);
        Object sizeDiff = _sizesByTransaction.get(trans);
        if(sizeDiff != null){
            return _size + ((Integer) sizeDiff).intValue();
        }
        return _size;
    }
    
    public void traverseKeys(Transaction trans, Visitor4 visitor){
        ensureActive(trans);
        if(_root == null){
            return;
        }
        _root.traverseKeys(trans, visitor);
    }
    
    public void traverseValues(Transaction trans, Visitor4 visitor) {
        ensureActive(trans);
        if(_root == null){
            return;
        }
        _root.traverseValues(trans, visitor);
    }
    
    public void sizeChanged(Transaction trans, int changeBy){
        Object sizeDiff = _sizesByTransaction.get(trans);
        if(sizeDiff == null){
            _sizesByTransaction.put(trans, new Integer(changeBy));
            return;
        }
        _sizesByTransaction.put(trans, new Integer(((Integer) sizeDiff).intValue() + changeBy));
    }

	public void dispose(Transaction transaction) {
		// TODO Auto-generated method stub
	}

	public BTreePointer firstPointer(Transaction trans) {
		ensureActive(trans);
		if (null == _root) {
			return null;
		}
		return _root.firstPointer(trans);
	}
	
	public BTree debugLoadFully(Transaction trans) {
		ensureActive(trans);
		_root.debugLoadFully(trans);
		return this;
	}

	public void traverseAllSlotIDs(Transaction trans, Visitor4 command) {
		Queue4 queue=new Queue4();
		if(_root==null) {
			read(trans);
		}
		queue.add(_root);
		while(queue.hasNext()) {	
			BTreeNode curNode=(BTreeNode)queue.next();
			curNode.prepareWrite(trans);
			int childCount = curNode.childCount();
			for(int childIdx=0;childIdx<childCount;childIdx++) {
				queue.add(curNode.child(childIdx));
			}
			command.visit(new Integer(curNode.getID()));
		}
	}

	public void defragIndex(YapReader source, YapReader target, IDMapping mapping) {
		BTreeNode.defragIndex(source,target,mapping,_keyHandler);
	}
}

