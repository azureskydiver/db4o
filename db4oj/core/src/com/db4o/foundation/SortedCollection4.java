/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.foundation;

/**
 * @exclude
 */
public class SortedCollection4 {
	
	private final Comparison4 _comparison;
	private Tree _tree;

	public SortedCollection4(Comparison4 comparison) {
		if (null == comparison) {
			throw new ArgumentNullException();
		}
		_comparison = comparison;
		_tree = null;
	}
	
	public void addAll(Iterator4 iterator) {		
		while (iterator.moveNext()) {
			add(iterator.current());
		}		
	}

	public void add(Object element) {
		_tree = Tree.add(_tree, new TreeObject(element, _comparison));
	}	

	public void remove(Object element) {
		_tree = Tree.removeLike(_tree, new TreeObject(element, _comparison));
	}

	public Object[] toArray(final Object[] array) {
		_tree.traverse(new Visitor4() {
			int i = 0;
			public void visit(Object obj) {
				array[i++] = ((TreeObject)obj).getObject();
			}
		});
		return array;
	}
	
	public int size() {
		return Tree.size(_tree);
	}
	
	static class TreeObject extends Tree {
		private Object _object;
		private Comparison4 _function;

		public TreeObject(Object object, Comparison4 function) {
			_object = object;
			_function = function;
		}

		public int compare(Tree tree) {
			return _function.compare(_object, ((TreeObject)tree).getObject());
		}

		public Object getObject() {
			return _object;
		}
	}
}
