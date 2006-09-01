namespace com.db4o.inside.btree
{
	internal class BTreeRangeIterator : com.db4o.foundation.KeyValueIterator
	{
		private readonly com.db4o.inside.btree.BTreeRangeImpl _range;

		private com.db4o.inside.btree.BTreePointer _cursor;

		private com.db4o.inside.btree.BTreeNode _lastNode = null;

		private com.db4o.inside.btree.BTreePointer _current;

		public BTreeRangeIterator(com.db4o.inside.btree.BTreeRangeImpl range)
		{
			_range = range;
			_cursor = range.First();
		}

		public virtual bool MoveNext()
		{
			while (!ReachedEnd(_cursor))
			{
				com.db4o.inside.btree.BTreeNode node = _cursor.Node();
				if (node != _lastNode)
				{
					node.PrepareWrite(Transaction());
					_lastNode = node;
				}
				object obj = _cursor.Key();
				if (obj != com.db4o.foundation.No4.INSTANCE)
				{
					_current = _cursor;
					_cursor = _cursor.Next();
					return true;
				}
				_cursor = _cursor.Next();
			}
			_current = null;
			return false;
		}

		public virtual object Key()
		{
			return Current().Key();
		}

		public virtual object Value()
		{
			return Current().Value();
		}

		private com.db4o.inside.btree.BTreePointer Current()
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

		private com.db4o.Transaction Transaction()
		{
			return _range.Transaction();
		}
	}
}
