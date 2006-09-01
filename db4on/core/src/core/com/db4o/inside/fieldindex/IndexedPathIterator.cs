namespace com.db4o.inside.fieldindex
{
	internal sealed class IndexedPathIterator : com.db4o.foundation.KeyValueIterator
	{
		private com.db4o.inside.fieldindex.IndexedPath _path;

		private com.db4o.foundation.KeyValueIterator _iterator;

		private com.db4o.foundation.KeyValueIterator _currentRangeIterator;

		public IndexedPathIterator(com.db4o.inside.fieldindex.IndexedPath path, com.db4o.foundation.KeyValueIterator
			 iterator)
		{
			_path = path;
			_iterator = iterator;
		}

		public bool MoveNext()
		{
			if (null == _currentRangeIterator)
			{
				if (!_iterator.MoveNext())
				{
					return false;
				}
				com.db4o.inside.btree.FieldIndexKey key = (com.db4o.inside.btree.FieldIndexKey)_iterator
					.Key();
				_currentRangeIterator = _path.Search(key.ParentID()).Iterator();
				return _currentRangeIterator.MoveNext();
			}
			if (!_currentRangeIterator.MoveNext())
			{
				_currentRangeIterator = null;
				return MoveNext();
			}
			return false;
		}

		public object Key()
		{
			return _currentRangeIterator.Key();
		}

		public object Value()
		{
			return _currentRangeIterator.Value();
		}
	}
}
