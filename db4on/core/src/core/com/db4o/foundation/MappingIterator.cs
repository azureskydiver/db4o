namespace com.db4o.foundation
{
	/// <exclude></exclude>
	public abstract class MappingIterator : System.Collections.IEnumerator
	{
		private readonly System.Collections.IEnumerator _iterator;

		private object _current;

		public static readonly object SKIP = new object();

		public MappingIterator(System.Collections.IEnumerator iterator)
		{
			if (null == iterator)
			{
				throw new System.ArgumentNullException();
			}
			_iterator = iterator;
			_current = com.db4o.foundation.Iterators.NO_ELEMENT;
		}

		protected abstract object Map(object current);

		public virtual bool MoveNext()
		{
			if (!_iterator.MoveNext())
			{
				_current = com.db4o.foundation.Iterators.NO_ELEMENT;
				return false;
			}
			_current = Map(_iterator.Current);
			if (_current == SKIP)
			{
				return MoveNext();
			}
			return true;
		}

		public virtual void Reset()
		{
			_current = com.db4o.foundation.Iterators.NO_ELEMENT;
			_iterator.Reset();
		}

		public virtual object Current
		{
			get
			{
				if (com.db4o.foundation.Iterators.NO_ELEMENT == _current)
				{
					throw new System.InvalidOperationException();
				}
				return _current;
			}
		}
	}
}
