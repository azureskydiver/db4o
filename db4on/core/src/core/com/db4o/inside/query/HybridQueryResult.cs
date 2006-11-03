namespace com.db4o.inside.query
{
	/// <exclude></exclude>
	public class HybridQueryResult : com.db4o.inside.query.AbstractQueryResult
	{
		private com.db4o.inside.query.AbstractQueryResult _delegate;

		public HybridQueryResult(com.db4o.Transaction transaction) : base(transaction)
		{
			_delegate = new com.db4o.inside.query.LazyQueryResult(transaction);
		}

		public override object Get(int index)
		{
			_delegate = _delegate.SupportElementAccess();
			return _delegate.Get(index);
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

		public override void LoadFromClassIndex(com.db4o.YapClass clazz)
		{
			_delegate.LoadFromClassIndex(clazz);
		}

		public override void LoadFromClassIndexes(com.db4o.YapClassCollectionIterator iterator
			)
		{
			_delegate.LoadFromClassIndexes(iterator);
		}

		public override void LoadFromIdReader(com.db4o.YapReader reader)
		{
			_delegate.LoadFromIdReader(reader);
		}

		public override void LoadFromQuery(com.db4o.QQuery query)
		{
			if (query.RequiresSort())
			{
				_delegate = new com.db4o.inside.query.IdListQueryResult(Transaction());
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
