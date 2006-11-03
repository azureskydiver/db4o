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
			return _tree.Key();
		}

		public virtual void AddAll(System.Collections.IEnumerator iterator)
		{
			while (iterator.MoveNext())
			{
				Add(iterator.Current);
			}
		}

		public virtual void Add(object element)
		{
			_tree = com.db4o.foundation.Tree.Add(_tree, new com.db4o.foundation.TreeObject(element
				, _comparison));
		}

		public virtual void Remove(object element)
		{
			_tree = com.db4o.foundation.Tree.RemoveLike(_tree, new com.db4o.foundation.TreeObject
				(element, _comparison));
		}

		public virtual object[] ToArray(object[] array)
		{
			com.db4o.foundation.Tree.Traverse(_tree, new _AnonymousInnerClass43(this, array));
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
				array[this.i++] = ((com.db4o.foundation.TreeObject)obj).Key();
			}

			private readonly SortedCollection4 _enclosing;

			private readonly object[] array;
		}

		public virtual int Size()
		{
			return com.db4o.foundation.Tree.Size(_tree);
		}
	}
}
