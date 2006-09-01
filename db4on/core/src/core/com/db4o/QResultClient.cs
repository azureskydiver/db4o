namespace com.db4o
{
	/// <exclude></exclude>
	internal class QResultClient : com.db4o.QueryResultImpl
	{
		private object[] i_prefetched = new object[com.db4o.YapConst.PREFETCH_OBJECT_COUNT
			];

		private int i_remainingObjects;

		private int i_prefetchCount = com.db4o.YapConst.PREFETCH_OBJECT_COUNT;

		private int i_prefetchRight;

		internal QResultClient(com.db4o.Transaction a_ta) : base(a_ta)
		{
		}

		internal QResultClient(com.db4o.Transaction a_ta, int initialSize) : base(a_ta, initialSize
			)
		{
		}

		public override bool HasNext()
		{
			lock (StreamLock())
			{
				if (i_remainingObjects > 0)
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
				if (i_remainingObjects < 1)
				{
					if (base.HasNext())
					{
						i_remainingObjects = (stream).PrefetchObjects(this, i_prefetched, i_prefetchCount
							);
						i_prefetchRight = i_remainingObjects;
					}
				}
				i_remainingObjects--;
				if (i_remainingObjects < 0)
				{
					return null;
				}
				if (i_prefetched[i_prefetchRight - i_remainingObjects - 1] == null)
				{
					return Next();
				}
				return Activate(i_prefetched[i_prefetchRight - i_remainingObjects - 1]);
			}
		}

		public override void Reset()
		{
			lock (StreamLock())
			{
				i_remainingObjects = 0;
				i_prefetchRight = 0;
				base.Reset();
			}
		}
	}
}
