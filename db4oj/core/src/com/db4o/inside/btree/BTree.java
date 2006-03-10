/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.btree;

import com.db4o.*;
import com.db4o.inside.ix.*;

/**
 * @exclude
 */
public class BTree extends YapMeta{
    
    Indexable4 _handler;
    
    BTreeNode _root;
    
    private int _size;
    
    public BTree(Indexable4 handler, int id){
        _handler = handler;
        if(id > 0){
            setStateDeactivated();
        }else{
            _root = new BTreeNode(_handler);
            setStateClean();
        }
    }
    
    public void add(Transaction trans, Object value){
        _handler.prepareComparison(value);
        ensureActive(trans);
        BTreeNode split = _root.add(trans);
        if(split != null){
            _root = _root.newRoot(trans, split);
        }
        _size ++;
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

    public void readThis(Transaction a_trans, YapReader a_reader) {
        _size = a_reader.readInt();
        _root = new BTreeNode(_handler, a_reader.readInt());
    }
    
    public void writeThis(Transaction trans, YapReader a_writer) {
        a_writer.writeInt(_size);
        writeIDOf(trans, _root, a_writer);
    }


}

