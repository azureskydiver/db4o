namespace com.db4o.cluster.inside
{
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
				_objectSets[i] = queries[i].execute();
				_sizes[i] = _objectSets[i].size();
				size += _sizes[i];
			}
			_size = size;
		}

		public virtual bool hasNext()
		{
			lock (_cluster)
			{
				return hasNextNoSync();
			}
		}

		private com.db4o.ObjectSet current()
		{
			return _objectSets[_current];
		}

		private bool hasNextNoSync()
		{
			if (current().hasNext())
			{
				return true;
			}
			if (_current >= _objectSets.Length)
			{
				return false;
			}
			_current++;
			return hasNextNoSync();
		}

		public virtual object next()
		{
			lock (_cluster)
			{
				if (hasNextNoSync())
				{
					return current().next();
				}
				return null;
			}
		}

		public virtual void reset()
		{
			lock (_cluster)
			{
				for (int i = 0; i < _objectSets.Length; i++)
				{
					_objectSets[i].reset();
				}
				_current = 0;
			}
		}

		public virtual int size()
		{
			return _size;
		}

		public virtual object get(int index)
		{
			lock (_cluster)
			{
				if (index < 0 || index >= size())
				{
					throw new System.IndexOutOfRangeException();
				}
				int i = 0;
				while (index >= _sizes[i])
				{
					index -= _sizes[i];
					i++;
				}
				return ((com.db4o.inside.query.ObjectSetFacade)_objectSets[i])._delegate.get(index
					);
			}
		}

		public virtual long[] getIDs()
		{
			com.db4o.inside.Exceptions4.notSupported();
			return null;
		}

		public virtual object streamLock()
		{
			return _cluster;
		}

		public virtual com.db4o.ObjectContainer objectContainer()
		{
			return _cluster._objectContainers[_current];
		}

		public virtual int indexOf(int id)
		{
			com.db4o.inside.Exceptions4.notSupported();
			return 0;
		}
	}
}
