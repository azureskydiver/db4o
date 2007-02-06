namespace com.db4o.@internal.query.result
{
	/// <exclude></exclude>
	public class HybridQueryResult : com.db4o.@internal.query.result.AbstractQueryResult
	{
		private com.db4o.@internal.query.result.AbstractQueryResult _delegate;

		public HybridQueryResult(com.db4o.@internal.Transaction transaction, com.db4o.config.QueryEvaluationMode
			 mode) : base(transaction)
		{
			_delegate = ForMode(transaction, mode);
		}

		private static com.db4o.@internal.query.result.AbstractQueryResult ForMode(com.db4o.@internal.Transaction
			 transaction, com.db4o.config.QueryEvaluationMode mode)
		{
			if (mode == com.db4o.config.QueryEvaluationMode.LAZY)
			{
				return new com.db4o.@internal.query.result.LazyQueryResult(transaction);
			}
			if (mode == com.db4o.config.QueryEvaluationMode.SNAPSHOT)
			{
				return new com.db4o.@internal.query.result.SnapShotQueryResult(transaction);
			}
			return new com.db4o.@internal.query.result.IdListQueryResult(transaction);
		}

		public override object Get(int index)
		{
			_delegate = _delegate.SupportElementAccess();
			return _delegate.Get(index);
		}

		public override int GetId(int index)
		{
			_delegate = _delegate.SupportElementAccess();
			return _delegate.GetId(index);
		}

		public override int IndexOf(int id)
		{
			_delegate = _delegate.SupportElementAccess();
			return _delegate.IndexOf(id);
		}

		public override com.db4o.foundation.IntIterator4 IterateIDs()
		{
			return _delegate.IterateIDs();
		}

		public override System.Collections.IEnumerator GetEnumerator()
		{
			return _delegate.GetEnumerator();
		}

		public override void LoadFromClassIndex(com.db4o.@internal.ClassMetadata clazz)
		{
			_delegate.LoadFromClassIndex(clazz);
		}

		public override void LoadFromClassIndexes(com.db4o.@internal.ClassMetadataIterator
			 iterator)
		{
			_delegate.LoadFromClassIndexes(iterator);
		}

		public override void LoadFromIdReader(com.db4o.@internal.Buffer reader)
		{
			_delegate.LoadFromIdReader(reader);
		}

		public override void LoadFromQuery(com.db4o.@internal.query.processor.QQuery query
			)
		{
			if (query.RequiresSort())
			{
				_delegate = new com.db4o.@internal.query.result.IdListQueryResult(Transaction());
			}
			_delegate.LoadFromQuery(query);
		}

		public override int Size()
		{
			_delegate = _delegate.SupportSize();
			return _delegate.Size();
		}

		public override void Sort(com.db4o.query.QueryComparator cmp)
		{
			_delegate = _delegate.SupportSort();
			_delegate.Sort(cmp);
		}
	}
}
