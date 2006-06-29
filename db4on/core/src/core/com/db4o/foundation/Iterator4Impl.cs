namespace com.db4o.foundation
{
	/// <exclude></exclude>
	public class Iterator4Impl : com.db4o.foundation.Iterator4
	{
		public static readonly com.db4o.foundation.Iterator4 EMPTY = new com.db4o.foundation.EmptyIterator
			();

		private com.db4o.foundation.List4 _next;

		public Iterator4Impl(com.db4o.foundation.List4 first)
		{
			_next = first;
		}

		public virtual bool HasNext()
		{
			return _next != null;
		}

		public virtual object Next()
		{
			object obj = _next._element;
			_next = _next._next;
			return obj;
		}
	}
}
