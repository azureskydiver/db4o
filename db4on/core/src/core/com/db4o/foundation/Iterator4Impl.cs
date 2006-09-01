namespace com.db4o.foundation
{
	/// <exclude></exclude>
	public class Iterator4Impl : com.db4o.foundation.Iterator4
	{
		public static readonly com.db4o.foundation.Iterator4 EMPTY = new com.db4o.foundation.EmptyIterator
			();

		private static readonly object NO_ELEMENT = new object();

		private com.db4o.foundation.List4 _next;

		private object _current;

		public Iterator4Impl(com.db4o.foundation.List4 first)
		{
			_next = first;
			_current = NO_ELEMENT;
		}

		public virtual bool MoveNext()
		{
			if (_next == null)
			{
				_current = NO_ELEMENT;
				return false;
			}
			_current = _next._element;
			_next = _next._next;
			return true;
		}

		public virtual object Current()
		{
			if (NO_ELEMENT == _current)
			{
				throw new System.InvalidOperationException();
			}
			return _current;
		}
	}
}
