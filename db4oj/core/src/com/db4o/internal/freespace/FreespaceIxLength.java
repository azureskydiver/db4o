/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.freespace;

import com.db4o.*;
import com.db4o.internal.*;


class FreespaceIxLength extends FreespaceIx{
    
    FreespaceIxLength(LocalObjectContainer file, MetaIndex metaIndex){
        super(file, metaIndex);
    }

    void add(int address, int length) {
        _index._handler.prepareComparison(new Integer(length));
        _indexTrans.add(address, new Integer(length));
    }

    int address() {
        return _visitor._key;
    }

    int length() {
        return _visitor._value;
    }

    void remove(int address, int length) {
        _index._handler.prepareComparison(new Integer(length));
        _indexTrans.remove(address, new Integer(length));
    }

}
