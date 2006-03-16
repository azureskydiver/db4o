/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.btree;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.inside.ix.*;

/**
 * @exclude
 */
public class BTree extends YapMeta{
    
    final Indexable4 _keyHandler;
    
    final Indexable4 _valueHandler;
    
    BTreeNode _root;
    
    /**
     * All instantiated nodes are held in this tree. 
     */
    private Tree _nodes;  
    
    private int _size;
    
    public BTree(int id, Indexable4 keyHandler, Indexable4 valueHandler){
        _keyHandler = keyHandler;
        _valueHandler = valueHandler;
        if(id > 0){
            setID(id);
            setStateDeactivated();
        }else{
            _root = new BTreeNode(this);
            setStateDirty();
        }
    }
    
    public void add(Transaction trans, Object value){
        _keyHandler.prepareComparison(value);
        ensureActive(trans);
        BTreeNode split = _root.add(trans);
        if(split != null){
            _root = _root.newRoot(trans, split);
            setStateDirty();
        }
        _size ++;
    }
    
    public void commit(final Transaction trans){
        if(_nodes != null){
            _nodes.traverse(new Visitor4() {
                public void visit(Object obj) {
                    BTreeNode node = (BTreeNode) ((TreeIntObject)obj).i_object;
                    node.commit(trans);
                }
            });
        }
        write(trans);
    }
    
    public void rollback(final Transaction trans){
        if(_nodes != null){
            _nodes.traverse(new Visitor4() {
                public void visit(Object obj) {
                    BTreeNode node = (BTreeNode) ((TreeIntObject)obj).i_object;
                    node.rollback(trans);
                }
            });
        }
    }
    
    private void ensureActive(Transaction trans){
        if(! isActive()){
            read(trans.systemTransaction());
        }
    }

    public byte getIdentifier() {
        return YapConst.BTREE;
    }
    
    public int ownLength() {
        return YapConst.YAPINT_LENGTH + YapConst.YAPID_LENGTH;
    }
    
    BTreeNode produceNode(int id){
        TreeIntObject tio = new TreeIntObject(id);
        _nodes = Tree.add(_nodes, tio);
        tio = (TreeIntObject)tio.duplicateOrThis();
        if(tio.i_object != null){
            Object obj = Platform4.weakReferenceTarget(tio.i_object);
            if(obj != null){
                return (BTreeNode)obj;
            }
        }
        BTreeNode node = new BTreeNode(this, id);
        tio.i_object = Platform4.createWeakReference(node);
        return node;
    }
    
    void addNode(int id, BTreeNode node){
        _nodes = Tree.add(_nodes, new TreeIntObject(id, node));
    }

    public void readThis(Transaction a_trans, YapReader a_reader) {
        _size = a_reader.readInt();
        _root = produceNode(a_reader.readInt());
    }
    
    public void writeThis(Transaction trans, YapReader a_writer) {
        a_writer.writeInt(_size);
        a_writer.writeIDOf(trans, _root);
    }
    
    public int size(){
        return _size;
    }
    
    public void traverseKeys(Transaction trans, Visitor4 visitor){
        if(_root != null){
            _root.traverseKeys(trans, visitor);
        }
    }


}

