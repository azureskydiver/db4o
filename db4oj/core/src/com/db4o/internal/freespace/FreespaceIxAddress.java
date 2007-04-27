/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.freespace;

import com.db4o.*;
import com.db4o.internal.*;

class FreespaceIxAddress extends FreespaceIx{
    
    FreespaceIxAddress(LocalObjectContainer file, MetaIndex metaIndex){
        super(file, metaIndex);
    }

    void add(int address, int length) {
        _index._handler.prepareComparison(new Integer(address));
        _indexTrans.add(length, new Integer(address));
    }

    int address() {
        return _visitor._value;
    }

    int length() {
        return _visitor._key;
    }

    void remove(int address, int length) {
        _index._handler.prepareComparison(new Integer(address));
        _indexTrans.remove(length, new Integer(address));
    }

}
