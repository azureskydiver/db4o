/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.inside.ix;

import com.db4o.*;
import com.db4o.foundation.*;

/**
 * @exclude
 */
public abstract class IxTree extends Tree{
    
    IxFieldTransaction i_fieldTransaction;
    
    int i_version;
    
    int _nodes = 1;
    
    IxTree(IxFieldTransaction a_ft){
        i_fieldTransaction = a_ft;
        i_version = a_ft.i_version;
    }
    
    public Tree add(final Tree a_new, final int a_cmp){
        if(a_cmp < 0){
            if(i_subsequent == null){
                i_subsequent = a_new;
            }else{
                i_subsequent = i_subsequent.add(a_new);
            }
        }else {
            if(i_preceding == null){
                i_preceding = a_new;
            }else{
                i_preceding = i_preceding.add(a_new);
            }
        }
        return balanceCheckNulls();
    }
    
    void beginMerge(){
        i_preceding = null;
        i_subsequent = null;
        setSizeOwn();
    }
    
    public Tree deepClone(Object a_param){
        try {
            IxTree tree = (IxTree)this.clone();
            tree.i_fieldTransaction = (IxFieldTransaction)a_param;
            return tree;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    final Indexable4 handler(){
        return i_fieldTransaction.i_index._handler;
    }
    
    /**
     * Overridden in IxFileRange
     * Only call directly after compare() 
     */
    int[] lowerAndUpperMatch(){
        return null;
    }
    
    public final int nodes(){
        return _nodes;
    }
    
    public final void nodes(int count){
       _nodes = count;
    }
    
    public void setSizeOwn(){
        super.setSizeOwn();
        _nodes = 1;
    }
    
    public void setSizeOwnPrecedingSubsequent(){
        super.setSizeOwnPrecedingSubsequent();
        _nodes = 1 + i_preceding.nodes() + i_subsequent.nodes();
    }
    
    public void setSizeOwnPreceding(){
        super.setSizeOwnPreceding();
        _nodes = 1 + i_preceding.nodes();
    }
    
    public void setSizeOwnSubsequent(){
        super.setSizeOwnSubsequent();
        _nodes = 1 + i_subsequent.nodes();
    }
    
    public final void setSizeOwnPlus(Tree tree){
        super.setSizeOwnPlus(tree);
        _nodes = 1 + tree.nodes();
    }
    
    public final void setSizeOwnPlus(Tree tree1, Tree tree2){
        super.setSizeOwnPlus(tree1, tree2);
        _nodes = 1 + tree1.nodes() + tree2.nodes();
    }
    
    int slotLength(){
        return handler().linkLength() + YapConst.YAPINT_LENGTH;
    }
    
    final YapFile stream(){
        return trans().i_file;
    }
    
    final Transaction trans(){
        return i_fieldTransaction.i_trans;
    }
    
    public abstract void visit(Visitor4 visitor, int[] a_lowerAndUpperMatch);
    
    public abstract void write(Indexable4 a_handler, YapWriter a_writer);
    

}
