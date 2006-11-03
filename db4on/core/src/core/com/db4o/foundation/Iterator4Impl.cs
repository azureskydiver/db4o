namespace com.db4o.foundation
{
	/// <exclude></exclude>
	public class Iterator4Impl : System.Collections.IEnumerator
	{
		public static readonly System.Collections.IEnumerator EMPTY = new com.db4o.foundation.Iterator4Impl
			(null);

		private com.db4o.foundation.List4 _first;

		private com.db4o.foundation.List4 _next;

		private object _current;

		public Iterator4Impl(com.db4o.foundation.List4 first)
		{
			_first = first;
			_next = first;
			_current = com.db4o.foundation.Iterators.NO_ELEMENT;
		}

		public virtual bool MoveNext()
		{
			if (_next == null)
			{
				_current = com.db4o.foundation.Iterators.NO_ELEMENT;
				return false;
			}
			_current = _next._element;
			_next = _next._next;
			return true;
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

		public virtual void Reset()
		{
			_next = _first;
			_current = com.db4o.foundation.Iterators.NO_ELEMENT;
		}
	}
}
