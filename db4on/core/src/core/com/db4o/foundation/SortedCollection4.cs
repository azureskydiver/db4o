namespace com.db4o.foundation
{
	/// <exclude></exclude>
	public class SortedCollection4
	{
		private readonly com.db4o.foundation.Comparison4 _comparison;

		private com.db4o.foundation.Tree _tree;

		public SortedCollection4(com.db4o.foundation.Comparison4 comparison)
		{
			if (null == comparison)
			{
				throw new System.ArgumentNullException();
			}
			_comparison = comparison;
			_tree = null;
		}

		public virtual object SingleElement()
		{
			if (1 != Size())
			{
				throw new System.InvalidOperationException();
			}
			return ((com.db4o.foundation.SortedCollection4.TreeObject)_tree).GetObject();
		}

		public virtual void AddAll(com.db4o.foundation.Iterator4 iterator)
		{
			while (iterator.MoveNext())
			{
				Add(iterator.Current());
			}
		}

		public virtual void Add(object element)
		{
			_tree = com.db4o.foundation.Tree.Add(_tree, new com.db4o.foundation.SortedCollection4.TreeObject
				(element, _comparison));
		}

		public virtual void Remove(object element)
		{
			_tree = com.db4o.foundation.Tree.RemoveLike(_tree, new com.db4o.foundation.SortedCollection4.TreeObject
				(element, _comparison));
		}

		public virtual object[] ToArray(object[] array)
		{
			_tree.Traverse(new _AnonymousInnerClass43(this, array));
			return array;
		}

		private sealed class _AnonymousInnerClass43 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass43(SortedCollection4 _enclosing, object[] array)
			{
				this._enclosing = _enclosing;
				this.array = array;
			}

			internal int i = 0;

			public void Visit(object obj)
			{
				array[this.i++] = ((com.db4o.foundation.SortedCollection4.TreeObject)obj).GetObject
					();
			}

			private readonly SortedCollection4 _enclosing;

			private readonly object[] array;
		}

		public virtual int Size()
		{
			return com.db4o.foundation.Tree.Size(_tree);
		}

		internal class TreeObject : com.db4o.foundation.Tree
		{
			private object _object;

			private com.db4o.foundation.Comparison4 _function;

			public TreeObject(object @object, com.db4o.foundation.Comparison4 function)
			{
				_object = @object;
				_function = function;
			}

			public override int Compare(com.db4o.foundation.Tree tree)
			{
				return _function.Compare(_object, ((com.db4o.foundation.SortedCollection4.TreeObject
					)tree).GetObject());
			}

			public virtual object GetObject()
			{
				return _object;
			}
		}
	}
}
