namespace com.db4o.inside.btree
{
	/// <exclude></exclude>
	public class Searcher
	{
		public int _lower;

		public int _upper;

		public int _cursor;

		public int _cmp;

		private int _for;

		private const int ANY = 0;

		private const int HIGHEST = 1;

		private const int LOWEST = -1;

		public Searcher(int count)
		{
			start(count);
		}

		public virtual void start(int count)
		{
			_lower = 0;
			_upper = count - 1;
			_cursor = -1;
		}

		public virtual bool incomplete()
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

		internal virtual void resultIs(int cmp)
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
			if (_for == ANY)
			{
				_lower = _cursor;
				_upper = _cursor;
				return;
			}
			if (_for == HIGHEST)
			{
				_lower = _cursor;
				return;
			}
			_upper = _cursor;
		}

		internal virtual void highest()
		{
			_for = HIGHEST;
		}

		internal virtual void lowest()
		{
			_for = LOWEST;
		}
	}
}
