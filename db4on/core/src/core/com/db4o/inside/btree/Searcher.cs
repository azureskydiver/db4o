namespace com.db4o.inside.btree
{
	/// <exclude></exclude>
	public class Searcher
	{
		public int _lower;

		public int _upper;

		public int _cursor;

		public int _cmp;

		private int _target;

		private const int ANY = 0;

		private const int HIGHEST = 1;

		private const int LOWEST = -1;

		public Searcher(int count)
		{
			Start(count);
		}

		public virtual void Start(int count)
		{
			_lower = 0;
			_upper = count - 1;
			_cursor = -1;
		}

		public virtual bool Incomplete()
		{
			if (_upper < _lower)
			{
				return false;
			}
			int oldCursor = _cursor;
			_cursor = _lower + ((_upper - _lower) / 2);
			if (_cursor == oldCursor && _cursor == _lower && _lower < _upper)
			{
				_cursor++;
			}
			return _cursor != oldCursor;
		}

		internal virtual void ResultIs(int cmp)
		{
			_cmp = cmp;
			if (cmp > 0)
			{
				_upper = _cursor - 1;
				if (_upper < _lower)
				{
					_upper = _lower;
				}
				return;
			}
			if (cmp < 0)
			{
				_lower = _cursor + 1;
				if (_lower > _upper)
				{
					_lower = _upper;
				}
				return;
			}
			if (_target == ANY)
			{
				_lower = _cursor;
				_upper = _cursor;
				return;
			}
			if (_target == HIGHEST)
			{
				_lower = _cursor;
				return;
			}
			_upper = _cursor;
		}

		internal virtual void Highest()
		{
			_target = HIGHEST;
		}

		internal virtual void Lowest()
		{
			_target = LOWEST;
		}
	}
}
