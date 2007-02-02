/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.inside;

import com.db4o.foundation.*;
import com.db4o.inside.*;


/**
 * Base class for balanced trees.
 * 
 * @exclude
 */
public class TreeInt extends Tree implements ReadWriteable {
	
	public static TreeInt add(TreeInt tree, int value) {
		return (TreeInt) Tree.add(tree, new TreeInt(value));
	}
	
	public static TreeInt removeLike(TreeInt tree, int value) {
		return (TreeInt) Tree.removeLike(tree, new TreeInt(value));
	}
	
	public static Tree addAll(Tree tree, IntIterator4 iter){
		if(! iter.moveNext()){
			return tree;
		}
		TreeInt firstAdded = new TreeInt(iter.currentInt());
		tree = Tree.add(tree, firstAdded);
		while(iter.moveNext()){
			tree = tree.add( new TreeInt(iter.currentInt()));
		}
		return tree;
	}

	public int _key;

	public TreeInt(int a_key) {
		this._key = a_key;
	}

	public int compare(Tree a_to) {
		return _key - ((TreeInt) a_to)._key;
	}

	Tree deepClone() {
		return new TreeInt(_key);
	}

	public boolean duplicates() {
		return false;
	}

	public static final TreeInt find(Tree a_in, int a_key) {
		if (a_in == null) {
			return null;
		}
		return ((TreeInt) a_in).find(a_key);
	}

	final TreeInt find(int a_key) {
		int cmp = _key - a_key;
		if (cmp < 0) {
			if (_subsequent != null) {
				return ((TreeInt) _subsequent).find(a_key);
			}
		} else {
			if (cmp > 0) {
				if (_preceding != null) {
					return ((TreeInt) _preceding).find(a_key);
				}
			} else {
				return this;
			}
		}
		return null;
	}

	public Object read(Buffer a_bytes) {
		return new TreeInt(a_bytes.readInt());
	}

	public void write(Buffer a_writer) {
		a_writer.writeInt(_key);
	}
	
	public static void write(final Buffer a_writer, TreeInt a_tree){
        write(a_writer, a_tree, a_tree == null ? 0 : a_tree.size());
	}
    
    public static void write(final Buffer a_writer, TreeInt a_tree, int size){
        if(a_tree == null){
            a_writer.writeInt(0);
            return;
        }
        a_writer.writeInt(size);
        a_tree.traverse(new Visitor4() {
            public void visit(Object a_object) {
                ((TreeInt)a_object).write(a_writer);
            }
        });
    }

	public int ownLength() {
		return Const4.INT_LENGTH;
	}

	boolean variableLength() {
		return false;
	}

	QCandidate toQCandidate(QCandidates candidates) {
		QCandidate qc = new QCandidate(candidates, null, _key, true);
		qc._preceding = toQCandidate((TreeInt) _preceding, candidates);
		qc._subsequent = toQCandidate((TreeInt) _subsequent, candidates);
		qc._size = _size;
		return qc;
	}

	public static QCandidate toQCandidate(TreeInt tree, QCandidates candidates) {
		if (tree == null) {
			return null;
		}
		return tree.toQCandidate(candidates);
	}

	public String toString() {
		return "" + _key;
	}

	protected Tree shallowCloneInternal(Tree tree) {
		TreeInt treeint=(TreeInt)super.shallowCloneInternal(tree);
		treeint._key=_key;
		return treeint;
	}

	public Object shallowClone() {
		TreeInt treeint= new TreeInt(_key);
		return shallowCloneInternal(treeint);
	}
	
	public static int byteCount(TreeInt a_tree){
		if(a_tree == null){
			return Const4.INT_LENGTH;
		}
		return a_tree.byteCount();
	}
	
	public final int byteCount(){
		if(variableLength()){
			final int[] length = new int[]{Const4.INT_LENGTH};
			traverse(new Visitor4(){
				public void visit(Object obj){
					length[0] += ((TreeInt)obj).ownLength();
				}
			});
			return length[0];
		}
		return Const4.INT_LENGTH + (size() * ownLength());
	}
	
    public Object key(){
    	return new Integer(_key);
    }


}
