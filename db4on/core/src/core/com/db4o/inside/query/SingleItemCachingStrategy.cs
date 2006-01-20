using System;

namespace com.db4o.inside.query
{
	/// <summary>
	/// A very simple caching strategy that caches only the last added item.
	/// </summary>
	public class SingleItemCachingStrategy : ICachingStrategy
	{
		private object _lastKey;
		private object _lastItem;
		private object _monitor = new object();
		
		#region ICachingStrategy Members
		public void Add(object key, object item)
		{
			if (null == key) throw new ArgumentNullException("key");
			lock (_monitor)
			{
				_lastKey = key;
				_lastItem = item;
			}
		}

		public object Get(object key)
		{
			if (null == key) throw new ArgumentNullException("key");
			lock (_monitor)
			{
				return key.Equals(_lastKey)
					? _lastItem
					: null;
			}
		}
		#endregion
	}
}