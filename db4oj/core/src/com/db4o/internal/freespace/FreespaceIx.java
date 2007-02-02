/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.freespace;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.ix.*;


abstract class FreespaceIx {
    
    Index4 _index;
    
    IndexTransaction _indexTrans;
    
    IxTraverser _traverser;
    
    FreespaceVisitor _visitor;
    
    FreespaceIx(LocalObjectContainer file, MetaIndex metaIndex){
        _index = new Index4(file.getSystemTransaction(),new IntHandler(file), metaIndex, false);
        _indexTrans = _index.globalIndexTransaction();
    }
    
    abstract void add(int address, int length);
    
    abstract int address();
    
    public void debug(){
        if(Debug.freespace){
            traverse(new Visitor4(){
                public void visit(Object obj) {
                    System.out.println(obj);
                }
            });
        }
    }
    
    public int entryCount() {
        return Tree.size(_indexTrans.getRoot());
    }
    
    void find (int val){
        _traverser = new IxTraverser();
        _traverser.findBoundsExactMatch(new Integer(val), (IxTree)_indexTrans.getRoot());
    }
    
    abstract int length();
    
    boolean match(){
        _visitor = new FreespaceVisitor();
        _traverser.visitMatch(_visitor);
        return _visitor.visited();
    }
    
    boolean preceding(){
        _visitor = new FreespaceVisitor();
        _traverser.visitPreceding(_visitor);
        return _visitor.visited();
    }
    
    abstract void remove(int address, int length);
    
    boolean subsequent(){
        _visitor = new FreespaceVisitor();
        _traverser.visitSubsequent(_visitor);
        return _visitor.visited();
    }
    
    public void traverse(Visitor4 visitor){
        Tree.traverse(_indexTrans.getRoot(), visitor);
    }
    

}
