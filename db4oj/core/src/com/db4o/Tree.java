/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

/**
 * @exclude
 */
public abstract class Tree implements Cloneable, Readable
{
	Tree i_preceding;
	int i_size = 1;
	Tree i_subsequent;
	
	static final Tree add(Tree a_old, Tree a_new){
		if(a_old == null){
			return a_new;
		}
		return a_old.add(a_new);
	}
	
	public Tree add(final Tree a_new){
	    return add(a_new, compare(a_new));
	}
	
	Tree add(final Tree a_new, final int a_cmp){
	    if(a_cmp < 0){
	        if(i_subsequent == null){
	            i_subsequent = a_new;
	            i_size ++;
	        }else{
	            i_subsequent = i_subsequent.add(a_new);
	            if(i_preceding == null){
	                return rotateLeft();
	            }else{
	                return balance();
	            }
	        }
	    }else if(a_cmp > 0 || a_new.duplicates()){
	        if(i_preceding == null){
	            i_preceding = a_new;
	            i_size ++;
	        }else{
	            i_preceding = i_preceding.add(a_new);
	            if(i_subsequent == null){
	                return rotateRight();
	            }else{
	                return balance();
	            }
	        }
	    }else{
	        a_new.isDuplicateOf(this);
	    }
	    return this;
	}
	
	final Tree balance(){
		int cmp = i_subsequent.i_size - i_preceding.i_size;
		if(cmp < -2){
			return rotateRight();
		}else if(cmp > 2){
			return rotateLeft();
		}else{
		    i_size = i_preceding.i_size + i_subsequent.i_size + ownSize();
		    return this;
		}
	}
	
	final Tree balanceCheckNulls(){
	    if(i_subsequent == null){
	        if(i_preceding == null){
	            i_size = ownSize();
	            return this;
	        }
	        return rotateRight();
	    }else if(i_preceding == null){
	        return rotateLeft();
	    }
	    return balance();
	}
	
	public static int byteCount(Tree a_tree){
		if(a_tree == null){
			return YapConst.YAPINT_LENGTH;
		}
		return a_tree.byteCount();
	}
	
	public final int byteCount(){
		if(variableLength()){
			final int[] length = new int[]{YapConst.YAPINT_LENGTH};
			traverse(new Visitor4(){
				public void visit(Object obj){
					length[0] += ((Tree)obj).ownLength();
				}
			});
			return length[0];
		}else{
			return YapConst.YAPINT_LENGTH + (size() * ownLength());
		}
	}

	
	void calculateSize(){
		if(i_preceding == null){
			if (i_subsequent == null){
				i_size = ownSize();
			}else{
				i_size = i_subsequent.i_size + ownSize();
			}
		}else{
			if(i_subsequent == null){
				i_size = i_preceding.i_size + ownSize();
			}else{
				i_size = i_preceding.i_size + i_subsequent.i_size + ownSize();
			}
		}
	}
	
	
	/**
	 * returns 0, if keys are equal  
	 * returns negative if compared key (a_to) is smaller
	 * returns positive if compared key (a_to) is greater
	 */
	abstract int compare(Tree a_to);
	
	static Tree deepClone(Tree a_tree, Object a_param){
		if(a_tree == null){
			return null;
		}
		Tree newNode = a_tree.deepClone(a_param);
		newNode.i_size = a_tree.i_size;
		newNode.i_preceding = Tree.deepClone(a_tree.i_preceding, a_param); 
		newNode.i_subsequent = Tree.deepClone(a_tree.i_subsequent, a_param); 
		return newNode;
	}
	
	
	Tree deepClone(Object a_param){
	    try {
            return (Tree)this.clone();
        } catch (CloneNotSupportedException e) {
        }
        return null;
	}
	
	boolean duplicates(){
		return true;
	}
	
	final Tree filter(final VisitorBoolean a_filter){
		if(i_preceding != null){
			i_preceding = i_preceding.filter(a_filter);
		}
		if(i_subsequent != null){
			i_subsequent = i_subsequent.filter(a_filter);
		}
		if(! a_filter.isVisit(this)){
			return remove();
		}
		return this;
	}
	
	static final Tree find(Tree a_in, Tree a_tree){
		if(a_in == null){
			return null;
		}
		return a_in.find(a_tree);
	}
	
	public final Tree find(final Tree a_tree){
		int cmp = compare(a_tree);
		if (cmp < 0){
			if(i_subsequent != null){
				return i_subsequent.find(a_tree);
			}
		}else{
			if (cmp > 0){
				if(i_preceding != null){
					return i_preceding.find(a_tree);
				}
			}else{
				return this;
			}
		}
		return null;
	}
	
	final static Tree findGreaterOrEqual(Tree a_in, Tree a_finder){
		if(a_in == null){
			return null;
		}
		int cmp = a_in.compare(a_finder);
		if(cmp == 0){
			return a_in; // the highest node in the hierarchy !!!
		}else{
			if(cmp > 0){
				Tree node = findGreaterOrEqual(a_in.i_preceding, a_finder);
				if(node != null){
					return node;
				}
				return a_in;
			}else{
				return findGreaterOrEqual(a_in.i_subsequent, a_finder);
			}
		}
	}
	
	
	final static Tree findSmaller(Tree a_in, Tree a_node){
		if(a_in == null){
			return null;
		}
		int cmp = a_in.compare(a_node);
		if(cmp < 0){
			Tree node = findSmaller(a_in.i_subsequent, a_node);
			if(node != null){
				return node;
			}
			return a_in;
		}else{
			return findSmaller(a_in.i_preceding, a_node);
		}
	}
	
	void isDuplicateOf(Tree a_tree){
		i_size = 0;
	}
	
	int ownLength(){
		throw YapConst.virtualException();
	}
	
	int ownSize(){
	    return 1;
	}
	
	static Tree read(Tree a_tree, YapReader a_bytes){
		throw YapConst.virtualException();
	}
	
	public Object read(YapReader a_bytes){
		throw YapConst.virtualException();
	}

	Tree remove(){
		if(i_subsequent != null && i_preceding != null){
			i_subsequent = i_subsequent.rotateSmallestUp();
			i_subsequent.i_preceding = i_preceding;
			i_subsequent.calculateSize();
			return i_subsequent;
		}
		if(i_subsequent != null){
			return i_subsequent;
		}
		return i_preceding;
	}
	
	void removeChildren(){
		i_preceding = null;
		i_subsequent = null;
		i_size = ownSize();
	}
	
	static Tree removeLike(Tree from, Tree a_find){
		if(from == null){
			return null;
		}
		return from.removeLike(a_find);
	}
	
	public final Tree removeLike(final Tree a_find){
		int cmp = compare(a_find);
		if(cmp == 0){
			return remove();
		}
		if (cmp > 0){
			if(i_preceding != null){
				i_preceding = i_preceding.removeLike(a_find);
			}
		}else{
			if(i_subsequent != null){
				i_subsequent = i_subsequent.removeLike(a_find);
			}
		}
		calculateSize();
		return this;
	}
	
	final Tree removeNode(final Tree a_tree){
		if (this == a_tree){
			return remove();
		}
		int cmp = compare(a_tree);
		if (cmp >= 0){
			if(i_preceding != null){
				i_preceding = i_preceding.removeNode(a_tree);
			}
		}
		if(cmp <= 0){
			if(i_subsequent != null){
				i_subsequent = i_subsequent.removeNode(a_tree);	
			}
		}
		calculateSize();
		return this;
	}
	
	final Tree rotateLeft(){
		Tree tree = i_subsequent;
		i_subsequent = tree.i_preceding;
		calculateSize();
		tree.i_preceding = this;
		if(tree.i_subsequent == null){
			tree.i_size = i_size + tree.ownSize();
		}else{
			tree.i_size = i_size + tree.i_subsequent.i_size + tree.ownSize();
		}
		return tree;
	}

	final Tree rotateRight(){
		Tree tree = i_preceding;
		i_preceding = tree.i_subsequent;
		calculateSize();
		tree.i_subsequent = this;
		if(tree.i_preceding == null){
			tree.i_size = i_size + tree.ownSize();
		}else{
			tree.i_size = i_size + tree.i_preceding.i_size + tree.ownSize();
		}
		return tree;
	}
	
	private final Tree rotateSmallestUp(){
		if(i_preceding != null){
			i_preceding = i_preceding.rotateSmallestUp();
			return rotateRight();
		}
		return this;
	}
	
	static int size(Tree a_tree){
		if(a_tree == null){
			return 0;
		}
		return a_tree.size();
	}
	
	public int size(){
		return i_size;
	}
	
	public final void traverse(final Visitor4 a_visitor){
		if(i_preceding != null){
			i_preceding.traverse(a_visitor);
		}
		a_visitor.visit(this);
		if(i_subsequent != null){
			i_subsequent.traverse(a_visitor);
		}
	}
	
	final void traverseFromLeaves(Visitor4 a_visitor){
	    if(i_preceding != null){
	        i_preceding.traverseFromLeaves(a_visitor);
	    }
	    if(i_subsequent != null){
	        i_subsequent.traverseFromLeaves(a_visitor);
	    }
	    a_visitor.visit(this);
	}
	
	boolean variableLength(){
		throw YapConst.virtualException();
	}
	
	static void write(final YapWriter a_writer, Tree a_tree){
		if(a_tree == null){
			a_writer.writeInt(0);
		}else{
			a_writer.writeInt(a_tree.size());
			a_tree.traverse(new Visitor4() {
				public void visit(Object a_object) {
					((Tree)a_object).write(a_writer);
				}
			});
		}
	}
	
	public void write(YapWriter a_writer){
		throw YapConst.virtualException();
	}
	
// Keep the debug methods to debug the depth	
	
//	final void debugDepth(){
//	    System.out.println("Tree depth: " + debugDepth(0));
//	}
//	
//	final int debugDepth(int d){
//	    int max = d + 1;
//	    if (i_preceding != null){
//	        max = i_preceding.debugDepth(d + 1);
//	    }
//	    if(i_subsequent != null){
//	        int ms = i_subsequent.debugDepth(d + 1);
//	        if(ms > max){
//	            max = ms;
//	        }
//	    }
//	    return max;
//	}
	
}
