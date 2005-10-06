/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.freespace;

import com.db4o.*;
import com.db4o.inside.ix.*;


abstract class FreespaceIx {
    
    Index4 _index;
    
    IndexTransaction _indexTrans;
    
    IxTraverser _traverser;
    
    FreespaceVisitor _visitor;
    
    FreespaceIx(YapFile file, MetaIndex metaIndex){
        _index = new Index4(file.getSystemTransaction(),new YInt(file), metaIndex);
        _indexTrans = _index.globalIndexTransaction();
    }
    
    void find (int val){
        _traverser = new IxTraverser();
        _traverser.findBoundsExactMatch(new Integer(val), (IxTree)_indexTrans.getRoot());
    }
    
    boolean preceding(){
        _visitor = new FreespaceVisitor();
        _traverser.visitPreceding(_visitor);
        return _visitor.visited();
    }
    
    boolean subsequent(){
        _visitor = new FreespaceVisitor();
        _traverser.visitSubsequent(_visitor);
        return _visitor.visited();
    }
    
    boolean match(){
        _visitor = new FreespaceVisitor();
        _traverser.visitMatch(_visitor);
        return _visitor.visited();
    }
    
    abstract void add(int address, int length);
    
    abstract void remove(int address, int length);
    
    abstract int length();
    
    abstract int address();
    


}
