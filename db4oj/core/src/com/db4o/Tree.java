/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.*;

/**
 * @exclude
 */
public abstract class Tree implements Cloneable, Readable{
    
	public Tree i_preceding;
	public int i_size = 1;
	public Tree i_subsequent;
	
	public static final Tree add(Tree a_old, Tree a_new){
		if(a_old == null){
			return a_new;
		}
		return a_old.add(a_new);
	}
	
	public Tree add(final Tree a_new){
	    return add(a_new, compare(a_new));
	}
	
	public Tree add(final Tree a_new, final int a_cmp){
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
    
    
    /**
     * On adding a node to a tree, if it already exists, 
     * #isDuplicateOf() will be called and the added node
     * can be asked for the node that prevails in the 
     * tree. This mechanism allows doing find() and add()
     * in one run.
     */
    public Tree duplicateOrThis(){
        if(i_size == 0){
            return i_preceding;
        }
        return this;
    }
	
	public final Tree balance(){
		int cmp = i_subsequent.nodes() - i_preceding.nodes(); 
		if(cmp < -2){
			return rotateRight();
		}else if(cmp > 2){
			return rotateLeft();
		}else{
            setSizeOwnPrecedingSubsequent();
		    return this;
		}
	}
	
	public Tree balanceCheckNulls(){
	    if(i_subsequent == null){
	        if(i_preceding == null){
                setSizeOwn();
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

	
	public void calculateSize(){
		if(i_preceding == null){
			if (i_subsequent == null){
				setSizeOwn();
			}else{
                setSizeOwnSubsequent();
			}
		}else{
			if(i_subsequent == null){
                setSizeOwnPreceding();
			}else{
                setSizeOwnPrecedingSubsequent();
			}
		}
	}
	
	
    /**
     * returns 0, if keys are equal
     * uses this - other  
     * returns positive if this is greater than a_to
     * returns negative if this is smaller than a_to
     */
	public abstract int compare(Tree a_to);
	
	public static Tree deepClone(Tree a_tree, Object a_param){
		if(a_tree == null){
			return null;
		}
		Tree newNode = a_tree.deepClone(a_param);
		newNode.i_size = a_tree.i_size;
        newNode.nodes( a_tree.nodes());
		newNode.i_preceding = Tree.deepClone(a_tree.i_preceding, a_param); 
		newNode.i_subsequent = Tree.deepClone(a_tree.i_subsequent, a_param); 
		return newNode;
	}
	
	
	public Tree deepClone(Object a_param){
	    try {
            return (Tree)this.clone();
        } catch (CloneNotSupportedException e) {
        }
        return null;
	}
	
	public boolean duplicates(){
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
	
	public static final Tree find(Tree a_in, Tree a_tree){
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
	
	public static final Tree findGreaterOrEqual(Tree a_in, Tree a_finder){
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
	
	
	public final static Tree findSmaller(Tree a_in, Tree a_node){
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
    
    public final Tree first(){
        if(i_preceding == null){
            return this;
        }
        return i_preceding.first();
    }
    
	void isDuplicateOf(Tree a_tree){
		i_size = 0;
        i_preceding = a_tree;
	}
	
    /**
     * @return the number of nodes in this tree for balancing
     */
    public int nodes(){
        return i_size;
    }
    
    public void nodes(int count){
        // do nothing, virtual
    }
    
	public int ownLength(){
		throw YapConst.virtualException();
	}
	
	public int ownSize(){
	    return 1;
	}
	
	static Tree read(Tree a_tree, YapReader a_bytes){
		throw YapConst.virtualException();
	}
	
	public Object read(YapReader a_bytes){
		throw YapConst.virtualException();
	}

	public Tree remove(){
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
	
	public void removeChildren(){
		i_preceding = null;
		i_subsequent = null;
		setSizeOwn();
	}
    
    public Tree removeFirst(){
        if(i_preceding == null){
            return i_subsequent;
        }
        i_preceding = i_preceding.removeFirst();
        calculateSize();
        return this;
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
	
	public final Tree removeNode(final Tree a_tree){
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
    
	public final Tree rotateLeft(){
		Tree tree = i_subsequent;
		i_subsequent = tree.i_preceding;
		calculateSize();
		tree.i_preceding = this;
		if(tree.i_subsequent == null){
            tree.setSizeOwnPlus(this);
		}else{
            tree.setSizeOwnPlus(this, tree.i_subsequent);
		}
		return tree;
	}

	public final Tree rotateRight(){
		Tree tree = i_preceding;
		i_preceding = tree.i_subsequent;
		calculateSize();
		tree.i_subsequent = this;
		if(tree.i_preceding == null){
            tree.setSizeOwnPlus(this);
		}else{
            tree.setSizeOwnPlus(this, tree.i_preceding);
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
    
    public void setSizeOwn(){
        i_size = ownSize();
    }
    
    public void setSizeOwnPrecedingSubsequent(){
        i_size = ownSize() + i_preceding.i_size + i_subsequent.i_size;
    }
    
    public void setSizeOwnPreceding(){
        i_size = ownSize() + i_preceding.i_size;
    }
    
    public void setSizeOwnSubsequent(){
        i_size = ownSize() + i_subsequent.i_size;
    }
    
    public void setSizeOwnPlus(Tree tree){
        i_size = ownSize() + tree.i_size;
    }
    
    public void setSizeOwnPlus(Tree tree1, Tree tree2){
        i_size = ownSize() + tree1.i_size + tree2.i_size;
    }
	
	public static int size(Tree a_tree){
		if(a_tree == null){
			return 0;
		}
		return a_tree.size();
	}
	
    /**
     * @return the number of objects represented.
     */
	public int size(){
		return i_size;
	}
    
    public static final void traverse(Tree tree, Visitor4 visitor){
        if(tree == null){
            return;
        }
        tree.traverse(visitor);
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
	
	public final void traverseFromLeaves(Visitor4 a_visitor){
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
	
	public static void write(final YapReader a_writer, Tree a_tree){
        write(a_writer, a_tree, a_tree == null ? 0 : a_tree.size());
	}
    
    public static void write(final YapReader a_writer, Tree a_tree, int size){
        if(a_tree == null){
            a_writer.writeInt(0);
            return;
        }
        a_writer.writeInt(size);
        a_tree.traverse(new Visitor4() {
            public void visit(Object a_object) {
                ((Tree)a_object).write(a_writer);
            }
        });
    }

	
	public void write(YapReader a_writer){
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
