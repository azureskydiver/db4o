/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

abstract class IxTree extends Tree{
    
    IxFieldTransaction i_fieldTransaction;
    
    int i_version;
    
    IxTree(IxFieldTransaction a_ft){
        i_fieldTransaction = a_ft;
        i_version = a_ft.i_version;
    }
    
    Tree add(final Tree a_new, final int a_cmp){
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
    
    abstract Tree addToCandidatesTree(Tree a_tree, QCandidates a_candidates, int[] a_lowerAndUpperMatch);
    
    Tree deepClone(Object a_param){
        try {
            IxTree tree = (IxTree)this.clone();
            tree.i_fieldTransaction = (IxFieldTransaction)a_param;
            return tree;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    final YapDataType handler(){
        return i_fieldTransaction.i_index.i_field.getHandler();
    }
    
    int slotLength(){
        return handler().linkLength() + YapConst.YAPINT_LENGTH;
    }
    
    final Transaction trans(){
        return i_fieldTransaction.i_trans;
    }
    
    abstract void write(YapDataType a_handler, YapWriter a_writer);
    
    final YapFile stream(){
        return trans().i_file;
    }
    
    void beginMerge(){
        i_preceding = null;
        i_subsequent = null;
        i_size = ownSize();
    }
}
