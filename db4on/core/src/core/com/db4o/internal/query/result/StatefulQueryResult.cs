namespace com.db4o.@internal.query.result
{
	/// <exclude></exclude>
	public class StatefulQueryResult
	{
		private readonly com.db4o.@internal.query.result.QueryResult _delegate;

		private readonly com.db4o.foundation.Iterable4Adaptor _iterable;

		public StatefulQueryResult(com.db4o.@internal.query.result.QueryResult queryResult
			)
		{
			_delegate = queryResult;
			_iterable = new com.db4o.foundation.Iterable4Adaptor(queryResult);
		}

		public virtual object Get(int index)
		{
			lock (Lock())
			{
				return _delegate.Get(index);
			}
		}

		public virtual long[] GetIDs()
		{
			lock (Lock())
			{
				long[] ids = new long[Size()];
				int i = 0;
				com.db4o.foundation.IntIterator4 iterator = _delegate.IterateIDs();
				while (iterator.MoveNext())
				{
					ids[i++] = iterator.CurrentInt();
				}
				return ids;
			}
		}

		public virtual bool HasNext()
		{
			lock (Lock())
			{
				return _iterable.HasNext();
			}
		}

		public virtual object Next()
		{
			lock (Lock())
			{
				return _iterable.Next();
			}
		}

		public virtual void Reset()
		{
			lock (Lock())
			{
				_iterable.Reset();
			}
		}

		public virtual int Size()
		{
			lock (Lock())
			{
				return _delegate.Size();
			}
		}

		public virtual void Sort(com.db4o.query.QueryComparator cmp)
		{
			lock (Lock())
			{
				_delegate.Sort(cmp);
			}
		}

		public virtual object Lock()
		{
			return _delegate.Lock();
		}

		internal virtual com.db4o.ext.ExtObjectContainer ObjectContainer()
		{
			return _delegate.ObjectContainer();
		}

		public virtual int IndexOf(object a_object)
		{
			lock (Lock())
			{
				int id = (int)ObjectContainer().GetID(a_object);
				if (id <= 0)
				{
					return -1;
				}
				return _delegate.IndexOf(id);
			}
		}

		public virtual System.Collections.IEnumerator IterateIDs()
		{
			lock (Lock())
			{
				return _delegate.IterateIDs();
			}
		}

		public virtual System.Collections.IEnumerator Iterator()
		{
			lock (Lock())
			{
				return _delegate.GetEnumerator();
			}
		}
	}
}
