namespace com.db4o.@internal.btree
{
	public abstract class AbstractBTreeRangeIterator : System.Collections.IEnumerator
	{
		private readonly com.db4o.@internal.btree.BTreeRangeSingle _range;

		private com.db4o.@internal.btree.BTreePointer _cursor;

		private com.db4o.@internal.btree.BTreePointer _current;

		public AbstractBTreeRangeIterator(com.db4o.@internal.btree.BTreeRangeSingle range
			)
		{
			_range = range;
			_cursor = range.First();
		}

		public virtual bool MoveNext()
		{
			if (ReachedEnd(_cursor))
			{
				_current = null;
				return false;
			}
			_current = _cursor;
			_cursor = _cursor.Next();
			return true;
		}

		public virtual void Reset()
		{
			_cursor = _range.First();
		}

		protected virtual com.db4o.@internal.btree.BTreePointer CurrentPointer()
		{
			if (null == _current)
			{
				throw new System.InvalidOperationException();
			}
			return _current;
		}

		private bool ReachedEnd(com.db4o.@internal.btree.BTreePointer cursor)
		{
			if (cursor == null)
			{
				return true;
			}
			if (_range.End() == null)
			{
				return false;
			}
			return _range.End().Equals(cursor);
		}

		public abstract object Current
		{
			get;
		}
	}
}
