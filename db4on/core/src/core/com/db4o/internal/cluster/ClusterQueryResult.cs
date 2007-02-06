namespace com.db4o.@internal.cluster
{
	/// <exclude></exclude>
	public class ClusterQueryResult : com.db4o.@internal.query.result.QueryResult
	{
		private readonly com.db4o.cluster.Cluster _cluster;

		private readonly com.db4o.ObjectSet[] _objectSets;

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

		private sealed class ClusterQueryResultIntIterator : com.db4o.foundation.IntIterator4
		{
			private readonly com.db4o.foundation.CompositeIterator4 _delegate;

			public ClusterQueryResultIntIterator(System.Collections.IEnumerator[] iterators)
			{
				_delegate = new com.db4o.foundation.CompositeIterator4(iterators);
			}

			public bool MoveNext()
			{
				return _delegate.MoveNext();
			}

			public object Current
			{
				get
				{
					return _delegate.Current;
				}
			}

			public void Reset()
			{
				_delegate.Reset();
			}

			public int CurrentInt()
			{
				return ((com.db4o.foundation.IntIterator4)_delegate.CurrentIterator()).CurrentInt
					();
			}
		}

		public virtual com.db4o.foundation.IntIterator4 IterateIDs()
		{
			lock (_cluster)
			{
				System.Collections.IEnumerator[] iterators = new System.Collections.IEnumerator[_objectSets
					.Length];
				for (int i = 0; i < _objectSets.Length; i++)
				{
					iterators[i] = ((com.db4o.@internal.query.ObjectSetFacade)_objectSets[i])._delegate
						.IterateIDs();
				}
				return new com.db4o.@internal.cluster.ClusterQueryResult.ClusterQueryResultIntIterator
					(iterators);
			}
		}

		public virtual System.Collections.IEnumerator GetEnumerator()
		{
			lock (_cluster)
			{
				System.Collections.IEnumerator[] iterators = new System.Collections.IEnumerator[_objectSets
					.Length];
				for (int i = 0; i < _objectSets.Length; i++)
				{
					iterators[i] = ((com.db4o.@internal.query.ObjectSetFacade)_objectSets[i])._delegate
						.Iterator();
				}
				return new com.db4o.foundation.CompositeIterator4(iterators);
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
				return ((com.db4o.@internal.query.ObjectSetFacade)_objectSets[i]).Get(index);
			}
		}

		public virtual object StreamLock()
		{
			return _cluster;
		}

		public virtual com.db4o.ext.ExtObjectContainer ObjectContainer()
		{
			throw new System.NotSupportedException();
		}

		public virtual int IndexOf(int id)
		{
			throw new System.NotSupportedException();
		}

		public virtual void Sort(com.db4o.query.QueryComparator cmp)
		{
			throw new System.NotSupportedException();
		}

		public virtual void LoadFromClassIndex(com.db4o.@internal.ClassMetadata c)
		{
			throw new System.NotSupportedException();
		}

		public virtual void LoadFromQuery(com.db4o.@internal.query.processor.QQuery q)
		{
			throw new System.NotSupportedException();
		}

		public virtual void LoadFromClassIndexes(com.db4o.@internal.ClassMetadataIterator
			 i)
		{
			throw new System.NotSupportedException();
		}

		public virtual void LoadFromIdReader(com.db4o.@internal.Buffer r)
		{
			throw new System.NotSupportedException();
		}
	}
}
