/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.*;

/**
 * Index root holder for a field and a transaction. 
 */
class IxFieldTransaction implements Visitor4{
	
    final IxField i_index;
	final Transaction i_trans;
	int i_version;
	private Tree i_root;
	
	IxFieldTransaction(Transaction a_trans, IxField a_index){
	    i_trans = a_trans;
	    i_index = a_index;
	}
	
	public boolean equals(Object obj) {
		return i_trans == ((IxFieldTransaction)obj).i_trans;
    }
	
	void add(IxPatch a_patch){
	    i_root = Tree.add(i_root, a_patch);
	}
	
	Tree getRoot(){
	    return i_root;
	}
	
	void commit(){
	    i_index.commit(this);
	}
	
	void rollback(){
	    i_index.rollback(this);
	}
	
	void merge(IxFieldTransaction a_ft){
	    Tree otherRoot = a_ft.getRoot();
	    if(otherRoot != null){
	        otherRoot.traverseFromLeaves(this);
	    }
	}
	
	/**
	 * Visitor functionality for merge:<br>
	 * Add 
	 */
	public void visit(Object obj){
	    if(obj instanceof IxPatch){
		    IxPatch tree = (IxPatch)obj;
		    if(tree.i_queue != null){
		        Queue4 queue = tree.i_queue;
		        tree.i_queue = null;
		        while((tree = (IxPatch)queue.next()) != null){
		            tree.i_queue = null;
		            addPatchToRoot(tree);
		        }
		    }else{
		        addPatchToRoot(tree);
		    }
	    }
	}
	
	private void addPatchToRoot(IxPatch tree){
	    if(tree.i_version != i_version){
	        tree.beginMerge();
	        tree.handler().prepareComparison(tree.handler().comparableObject(i_trans, tree.i_value));
		    if(i_root == null){
		        i_root = tree;
		    } else{
		        i_root = i_root.add(tree);
		    }
	    }
	}
	
	int countLeaves(){
	    if(i_root == null){
	        return 0;
	    }
	    final int[] leaves ={0};
	    i_root.traverse(new Visitor4() {
            public void visit(Object a_object) {
                leaves[0] ++;
            }
        });
	    return leaves[0];
	}

    public void setRoot(Tree a_tree) {
        i_root = a_tree;
    }
    
    public String toString(){
        final StringBuffer sb = new StringBuffer();
        sb.append("IxFieldTransaction ");
        sb.append(System.identityHashCode(this));
        if(i_root == null){
            sb.append("\n    Empty");
        }else{
            i_root.traverse(new Visitor4() {
                public void visit(Object a_object) {
                    sb.append("\n");
                    sb.append(a_object.toString());
                }
            });
        }
        return sb.toString();
    }

	
	
	
}
