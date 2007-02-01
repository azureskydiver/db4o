/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.freespace;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.inside.*;
import com.db4o.inside.ix.*;


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

    int freeSize(){
        final MutableInt mint = new MutableInt();
        final IntObjectVisitor freespaceVisitor = new IntObjectVisitor(){
            public void visit(int anInt, Object anObject) {
                mint.add(anInt);
            }
        };
        traverse(new Visitor4() {
            public void visit(Object obj) {
                ((IxTree)obj).visitAll(freespaceVisitor);
            }
        });
        return mint.value();
    }
}
