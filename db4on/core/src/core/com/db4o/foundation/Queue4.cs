namespace com.db4o.foundation
{
	/// <summary>
	/// Using the CollectionElement the other way around:
	/// CollectionElement.i_next points to the previous element
	/// </summary>
	/// <exclude></exclude>
	public class Queue4
	{
		private com.db4o.foundation.List4 _first;

		private com.db4o.foundation.List4 _last;

		public void add(object obj)
		{
			com.db4o.foundation.List4 ce = new com.db4o.foundation.List4(null, obj);
			if (_first == null)
			{
				_last = ce;
			}
			else
			{
				_first._next = ce;
			}
			_first = ce;
		}

		public object next()
		{
			if (_last == null)
			{
				return null;
			}
			object ret = _last._element;
			_last = _last._next;
			if (_last == null)
			{
				_first = null;
			}
			return ret;
		}
	}
}
