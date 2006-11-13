/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.btree;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.inside.ix.*;
import com.db4o.inside.mapping.*;

/**
 * @exclude
 */
public class BTree extends YapMeta implements TransactionParticipant {
    
    private static final byte BTREE_VERSION = (byte)1;
    
    private static final int DEFRAGMENT_INCREMENT_OFFSET = 
    	1  // version byte
    + YapConst.INT_LENGTH * 2;  // size, node size  
    
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
    
    protected Queue4 _processing;
    
    private int _nodeSize;
    
    int _halfNodeSize;
    
    private final int _cacheHeight;
    
    public BTree(Transaction trans, int id, Indexable4 keyHandler){
        this(trans, id, keyHandler, null);
    }		
    		
    public BTree(Transaction trans, int id, Indexable4 keyHandler, Indexable4 valueHandler){
		this(trans, id, keyHandler, valueHandler, config(trans).bTreeNodeSize(), config(trans).bTreeCacheHeight());
    }	

	public BTree(Transaction trans, int id, Indexable4 keyHandler, Indexable4 valueHandler, final int treeNodeSize, final int treeCacheHeight) {
		if (null == keyHandler) {
    		throw new ArgumentNullException();
    	}
		_nodeSize = treeNodeSize;
		
        _halfNodeSize = _nodeSize / 2;
        _nodeSize = _halfNodeSize * 2;
		_cacheHeight = treeCacheHeight;
        _keyHandler = keyHandler;
        _valueHandler = (valueHandler == null) ? Null.INSTANCE : valueHandler;
        _sizesByTransaction = new Hashtable4();
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
    
	public BTreeNode root() {
		return _root;
	}
    
    public int nodeSize() {
		return _nodeSize;
	}

	public void add(Transaction trans, Object key){	
        add(trans, key, null);
    }
    
    public void add(Transaction trans, Object key, Object value){
    	keyCantBeNull(key);
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
    	keyCantBeNull(key);
    	
        final Iterator4 pointers = search(trans, key).pointers();
        if (!pointers.moveNext()) {
        	return;
        }
        BTreePointer first = (BTreePointer)pointers.current();
        ensureDirty(trans);
        BTreeNode node = first.node();
        node.remove(trans, first.index());
    }
    
    public BTreeRange search(Transaction trans, Object key) {
    	keyCantBeNull(key);
        
        // TODO: Optimize the following.
        //       Part of the search operates against the same nodes.
        //       As long as the bounds are on one node, the search
        //       should walk the nodes in one go.
        
        BTreeNodeSearchResult start = searchLeaf(trans, key, SearchTarget.LOWEST);
        BTreeNodeSearchResult end = searchLeaf(trans, key, SearchTarget.HIGHEST);
        return start.createIncludingRange(end);
    }
    
    private void keyCantBeNull(Object key) {
    	if (null == key) {
    		throw new ArgumentNullException();
    	}
	}

	public BTreeNodeSearchResult searchLeaf(Transaction trans, Object key, SearchTarget target) {
        ensureActive(trans);
        _keyHandler.prepareComparison(key);
        return _root.searchLeaf(trans, target);
    }
    
    public void commit(final Transaction trans){
        
        final Transaction systemTransaction = trans.systemTransaction();
        
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
            
            writeAllNodes(systemTransaction, true);
            
        }
        
        setStateDirty();
        write(systemTransaction);
        
        purge();
    }
    
    public void rollback(final Transaction trans){
    	
        final Transaction systemTransaction = trans.systemTransaction();
        
        _sizesByTransaction.remove(trans);
        
        if(_nodes == null){
            return;
        }
        
        processAllNodes();
        while(_processing.hasNext()){
            ((BTreeNode)_processing.next()).rollback(trans);
        }
        _processing = null;
        
        writeAllNodes(systemTransaction, false);
        
        setStateDirty();
        write(systemTransaction);
        
        purge();
    }
    
    private void writeAllNodes(final Transaction systemTransaction, final boolean setDirty){
        if(_nodes == null){
        	return;
        }
    	_nodes.traverse(new Visitor4() {
            public void visit(Object obj) {
                BTreeNode node = (BTreeNode)((TreeIntObject)obj).getObject();
                if(setDirty){
                	node.setStateDirty();
                }
                node.write(systemTransaction);
            }
        });
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
		// nothing to do here
	}

	public BTreePointer firstPointer(Transaction trans) {
		ensureActive(trans);
		if (null == _root) {
			return null;
		}
		return _root.firstPointer(trans);
	}
	
	public BTreePointer lastPointer(Transaction trans) {
		ensureActive(trans);
		if (null == _root) {
			return null;
		}
		return _root.lastPointer(trans);
	}
	
	public BTree debugLoadFully(Transaction trans) {
		ensureActive(trans);
		_root.debugLoadFully(trans);
		return this;
	}
    
    private void traverseAllNodes(Transaction trans, Visitor4 command) {
        ensureActive(trans);
        _root.traverseAllNodes(trans, command);
    }

	public void defragIndex(ReaderPair readers) {
        if (Deploy.debug) {
            readers.readBegin(YapConst.BTREE);
        }
		readers.incrementOffset(DEFRAGMENT_INCREMENT_OFFSET);
		readers.copyID();
        if (Deploy.debug) {
            readers.readEnd();
        }
	}

	public void defragIndexNode(ReaderPair readers) {
		BTreeNode.defragIndex(readers, _keyHandler, _valueHandler);
	}

	public void defragBTree(final DefragContext context) throws CorruptionException {
		ReaderPair.processCopy(context,getID(),new SlotCopyHandler() {
			public void processCopy(ReaderPair readers) throws CorruptionException {
				defragIndex(readers);
			}
		});
		final CorruptionException[] exc={null};
		try {
			context.traverseAllIndexSlots(this, new Visitor4() {
				public void visit(Object obj) {
					final int id=((Integer)obj).intValue();
					try {
						ReaderPair.processCopy(context, id, new SlotCopyHandler() {
							public void processCopy(ReaderPair readers) {
								defragIndexNode(readers);
							}
						});
					} catch (CorruptionException e) {
						exc[0]=e;
						throw new RuntimeException();
					}
				}
			});
		} catch (RuntimeException e) {
			if(exc[0]!=null) {
				throw exc[0];
			}
			throw e;
		}
	}

	public int compareKeys(Object key1, Object key2) {
		_keyHandler.prepareComparison(key2);
		return _keyHandler.compareTo(key1);
	}
	
	private static Config4Impl config(Transaction trans) {
		if (null == trans) {
			throw new ArgumentNullException();
		}
		return trans.stream().configImpl();
	}

    public void free(Transaction systemTrans) {
        freeAllNodeIds(systemTrans, allNodeIds(systemTrans));
    }

	private void freeAllNodeIds(Transaction systemTrans, final Iterator4 allNodeIDs) {
        while(allNodeIDs.moveNext()){
            int id = ((Integer)allNodeIDs.current()).intValue();
            systemTrans.slotFreePointerOnCommit(id);
        }
	}

	public Iterator4 allNodeIds(Transaction systemTrans) {
		final Collection4 allNodeIDs = new Collection4(); 
        traverseAllNodes(systemTrans, new Visitor4() {
            public void visit(Object node) {
                allNodeIDs.add(new Integer(((BTreeNode)node).getID()));
            }
        });
		return allNodeIDs.iterator();
	}
	
	public BTreeRange asRange(Transaction trans){
		return new BTreeRangeSingle(trans, this, firstPointer(trans), null);
	}

	
}

