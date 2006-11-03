namespace com.db4o.foundation
{
	/// <summary>elements in linked list Collection4</summary>
	/// <exclude></exclude>
	public sealed class List4 : com.db4o.types.Unversioned
	{
		/// <summary>next element in list</summary>
		public com.db4o.foundation.List4 _next;

		/// <summary>carried object</summary>
		public object _element;

		public List4()
		{
		}

		public List4(object element)
		{
			_element = element;
		}

		public List4(com.db4o.foundation.List4 next, object element)
		{
			_next = next;
			_element = element;
		}

		internal bool Holds(object obj)
		{
			if (obj == null)
			{
				return _element == null;
			}
			return obj.Equals(_element);
		}
	}
}
