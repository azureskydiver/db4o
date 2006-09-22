namespace com.db4o
{
	/// <exclude></exclude>
	internal class QResultClient : com.db4o.QueryResultImpl
	{
		private object[] _prefetchedObjects;

		private int _remainingObjects;

		private int _prefetchRight;

		internal QResultClient(com.db4o.Transaction ta) : base(ta)
		{
		}

		internal QResultClient(com.db4o.Transaction ta, int initialSize) : base(ta, initialSize
			)
		{
		}

		public override bool HasNext()
		{
			lock (StreamLock())
			{
				if (_remainingObjects > 0)
				{
					return true;
				}
				return base.HasNext();
			}
		}

		public override object Next()
		{
			lock (StreamLock())
			{
				com.db4o.YapClient stream = (com.db4o.YapClient)i_trans.Stream();
				stream.CheckClosed();
				int prefetchCount = stream.Config().PrefetchObjectCount();
				EnsureObjectCacheAllocated(prefetchCount);
				if (_remainingObjects < 1)
				{
					if (base.HasNext())
					{
						_remainingObjects = (stream).PrefetchObjects(this, _prefetchedObjects, prefetchCount
							);
						_prefetchRight = _remainingObjects;
					}
				}
				_remainingObjects--;
				if (_remainingObjects < 0)
				{
					return null;
				}
				if (_prefetchedObjects[_prefetchRight - _remainingObjects - 1] == null)
				{
					return Next();
				}
				return Activate(_prefetchedObjects[_prefetchRight - _remainingObjects - 1]);
			}
		}

		public override void Reset()
		{
			lock (StreamLock())
			{
				_remainingObjects = 0;
				_prefetchRight = 0;
				base.Reset();
			}
		}

		private void EnsureObjectCacheAllocated(int prefetchObjectCount)
		{
			if (_prefetchedObjects == null)
			{
				_prefetchedObjects = new object[prefetchObjectCount];
				return;
			}
			if (prefetchObjectCount > _prefetchedObjects.Length)
			{
				object[] newPrefetchedObjects = new object[prefetchObjectCount];
				System.Array.Copy(_prefetchedObjects, 0, newPrefetchedObjects, 0, _prefetchedObjects
					.Length);
				_prefetchedObjects = newPrefetchedObjects;
			}
		}
	}
}
