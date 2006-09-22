namespace com.db4o.inside.btree
{
	public abstract class AbstractBTreeRangeIterator : com.db4o.foundation.Iterator4
	{
		private readonly com.db4o.inside.btree.BTreeRangeSingle _range;

		private com.db4o.inside.btree.BTreePointer _cursor;

		private com.db4o.inside.btree.BTreePointer _current;

		public AbstractBTreeRangeIterator(com.db4o.inside.btree.BTreeRangeSingle range)
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

		protected virtual com.db4o.inside.btree.BTreePointer CurrentPointer()
		{
			if (null == _current)
			{
				throw new System.InvalidOperationException();
			}
			return _current;
		}

		private bool ReachedEnd(com.db4o.inside.btree.BTreePointer cursor)
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

		public abstract object Current();
	}
}
