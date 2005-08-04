namespace com.db4o
{
	/// <exclude></exclude>
	internal class QResultClient : com.db4o.QResult
	{
		private object[] i_prefetched = new object[com.db4o.YapConst.PREFETCH_OBJECT_COUNT
			];

		private int i_remainingObjects;

		private int i_prefetchCount = com.db4o.YapConst.PREFETCH_OBJECT_COUNT;

		internal QResultClient(com.db4o.Transaction a_ta) : base(a_ta)
		{
		}

		internal QResultClient(com.db4o.Transaction a_ta, int initialSize) : base(a_ta, initialSize
			)
		{
		}

		public override bool hasNext()
		{
			lock (streamLock())
			{
				if (i_remainingObjects > 0)
				{
					return true;
				}
				return base.hasNext();
			}
		}

		public override object next()
		{
			lock (streamLock())
			{
				com.db4o.YapClient stream = (com.db4o.YapClient)i_trans.i_stream;
				stream.checkClosed();
				if (i_remainingObjects < 1)
				{
					if (base.hasNext())
					{
						i_remainingObjects = (stream).prefetchObjects(this, i_prefetched, i_prefetchCount
							);
					}
				}
				i_remainingObjects--;
				if (i_remainingObjects < 0)
				{
					return null;
				}
				if (i_prefetched[i_remainingObjects] == null)
				{
					return next();
				}
				return activate(i_prefetched[i_remainingObjects]);
			}
		}

		public override void reset()
		{
			lock (streamLock())
			{
				i_remainingObjects = 0;
				base.reset();
			}
		}
	}
}
