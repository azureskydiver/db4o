/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

/**
 * Base class for balanced trees.
 * 
 * @exclude
 */
public class TreeInt extends Tree implements ReadWriteable{
	
	public int i_key;
	
	public TreeInt(int a_key){
		this.i_key = a_key;
	}

	int compare(Tree a_to) {
		return i_key - ((TreeInt)a_to).i_key;
	}
	
	Tree deepClone(){
		return new TreeInt(i_key);
	}
	
	public boolean duplicates(){
		return false;
	}
	
	static final TreeInt find(Tree a_in, int a_key){
		if(a_in == null){
			return null;
		}
		return ((TreeInt)a_in).find(a_key);
	}
	
	final TreeInt find(int a_key){
		int cmp = i_key - a_key;
		if (cmp < 0){
			if(i_subsequent != null){
				return ((TreeInt)i_subsequent).find(a_key);
			}
		}else{
			if (cmp > 0){
				if(i_preceding != null){
					return ((TreeInt)i_preceding).find(a_key);
				}
			}else{
				return this;
			}
		}
		return null;
	}

	
	public Object read(YapReader a_bytes){
		return new TreeInt(a_bytes.readInt());
	}
	
	public void write(YapWriter a_writer){
		a_writer.writeInt(i_key);
	}

	public int ownLength(){
		return YapConst.YAPINT_LENGTH;
	}
	
	boolean variableLength(){
		return false;
	}
	
	QCandidate toQCandidate(QCandidates candidates){
		QCandidate qc = new QCandidate(candidates, null, i_key, true);
		qc.i_preceding = toQCandidate((TreeInt)i_preceding, candidates); 
		qc.i_subsequent = toQCandidate((TreeInt)i_subsequent, candidates);
		qc.i_size = i_size; 
		return qc; 
	}
	
	public static QCandidate toQCandidate(TreeInt tree, QCandidates candidates){
		if(tree == null){
			return null;
		}
		return tree.toQCandidate(candidates);
	}
	
	
	
}
