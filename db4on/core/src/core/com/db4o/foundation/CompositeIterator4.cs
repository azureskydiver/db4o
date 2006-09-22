namespace com.db4o.foundation
{
	public class CompositeIterator4 : com.db4o.foundation.Iterator4
	{
		private readonly com.db4o.foundation.Iterator4 _iterators;

		private com.db4o.foundation.Iterator4 _currentIterator;

		public CompositeIterator4(com.db4o.foundation.Iterator4[] iterators) : this(new com.db4o.foundation.ArrayIterator4
			(iterators))
		{
		}

		public CompositeIterator4(com.db4o.foundation.Iterator4 iterators)
		{
			if (null == iterators)
			{
				throw new System.ArgumentNullException();
			}
			_iterators = iterators;
		}

		public virtual bool MoveNext()
		{
			if (null == _currentIterator)
			{
				if (!_iterators.MoveNext())
				{
					return false;
				}
				_currentIterator = NextIterator(_iterators.Current());
			}
			if (!_currentIterator.MoveNext())
			{
				_currentIterator = null;
				return MoveNext();
			}
			return true;
		}

		public virtual object Current()
		{
			return _currentIterator.Current();
		}

		protected virtual com.db4o.foundation.Iterator4 NextIterator(object current)
		{
			return (com.db4o.foundation.Iterator4)current;
		}
	}
}
