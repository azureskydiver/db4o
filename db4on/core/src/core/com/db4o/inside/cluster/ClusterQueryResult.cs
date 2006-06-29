namespace com.db4o.inside.cluster
{
	/// <exclude></exclude>
	public class ClusterQueryResult : com.db4o.inside.query.QueryResult
	{
		private readonly com.db4o.cluster.Cluster _cluster;

		private readonly com.db4o.ObjectSet[] _objectSets;

		private int _current;

		private readonly int[] _sizes;

		private readonly int _size;

		public ClusterQueryResult(com.db4o.cluster.Cluster cluster, com.db4o.query.Query[]
			 queries)
		{
			_cluster = cluster;
			_objectSets = new com.db4o.ObjectSet[queries.Length];
			_sizes = new int[queries.Length];
			int size = 0;
			for (int i = 0; i < queries.Length; i++)
			{
				_objectSets[i] = queries[i].Execute();
				_sizes[i] = _objectSets[i].Size();
				size += _sizes[i];
			}
			_size = size;
		}

		public virtual bool HasNext()
		{
			lock (_cluster)
			{
				return HasNextNoSync();
			}
		}

		private com.db4o.ObjectSet Current()
		{
			return _objectSets[_current];
		}

		private bool HasNextNoSync()
		{
			if (Current().HasNext())
			{
				return true;
			}
			if (_current >= _objectSets.Length - 1)
			{
				return false;
			}
			_current++;
			return HasNextNoSync();
		}

		public virtual object Next()
		{
			lock (_cluster)
			{
				if (HasNextNoSync())
				{
					return Current().Next();
				}
				return null;
			}
		}

		public virtual void Reset()
		{
			lock (_cluster)
			{
				for (int i = 0; i < _objectSets.Length; i++)
				{
					_objectSets[i].Reset();
				}
				_current = 0;
			}
		}

		public virtual int Size()
		{
			return _size;
		}

		public virtual object Get(int index)
		{
			lock (_cluster)
			{
				if (index < 0 || index >= Size())
				{
					throw new System.IndexOutOfRangeException();
				}
				int i = 0;
				while (index >= _sizes[i])
				{
					index -= _sizes[i];
					i++;
				}
				return ((com.db4o.inside.query.ObjectSetFacade)_objectSets[i])._delegate.Get(index
					);
			}
		}

		public virtual long[] GetIDs()
		{
			com.db4o.inside.Exceptions4.NotSupported();
			return null;
		}

		public virtual object StreamLock()
		{
			return _cluster;
		}

		public virtual com.db4o.ObjectContainer ObjectContainer()
		{
			return _cluster._objectContainers[_current];
		}

		public virtual int IndexOf(int id)
		{
			com.db4o.inside.Exceptions4.NotSupported();
			return 0;
		}

		public virtual void Sort(com.db4o.query.QueryComparator cmp)
		{
			com.db4o.inside.Exceptions4.NotSupported();
		}
	}
}
