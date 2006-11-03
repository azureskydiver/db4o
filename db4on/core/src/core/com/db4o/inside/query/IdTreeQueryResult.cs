namespace com.db4o.inside.query
{
	/// <exclude></exclude>
	public class IdTreeQueryResult : com.db4o.inside.query.AbstractQueryResult
	{
		private com.db4o.TreeInt _ids;

		public IdTreeQueryResult(com.db4o.Transaction transaction, com.db4o.inside.query.QueryResult
			 queryResult) : base(transaction)
		{
			com.db4o.foundation.IntIterator4 i = queryResult.IterateIDs();
			if (!i.MoveNext())
			{
				return;
			}
			_ids = new com.db4o.TreeInt(i.CurrentInt());
			while (i.MoveNext())
			{
				_ids = (com.db4o.TreeInt)_ids.Add(new com.db4o.TreeInt(i.CurrentInt()));
			}
		}

		public override object Get(int index)
		{
			throw new System.NotImplementedException();
		}

		public override int IndexOf(int id)
		{
			throw new System.NotImplementedException();
		}

		public override com.db4o.foundation.IntIterator4 IterateIDs()
		{
			return new com.db4o.foundation.IntIterator4Adaptor(new com.db4o.foundation.TreeKeyIterator
				(_ids));
		}

		public override void LoadFromClassIndex(com.db4o.YapClass clazz)
		{
			throw new System.NotImplementedException();
		}

		public override void LoadFromClassIndexes(com.db4o.YapClassCollectionIterator iterator
			)
		{
			throw new System.NotImplementedException();
		}

		public override void LoadFromIdReader(com.db4o.YapReader reader)
		{
			throw new System.NotImplementedException();
		}

		public override void LoadFromQuery(com.db4o.QQuery query)
		{
			throw new System.NotImplementedException();
		}

		public override int Size()
		{
			if (_ids == null)
			{
				return 0;
			}
			return _ids.Size();
		}

		public override void Sort(com.db4o.query.QueryComparator cmp)
		{
			throw new System.NotImplementedException();
		}

		public override com.db4o.inside.query.AbstractQueryResult SupportSort()
		{
			return ToIdList();
		}

		public override com.db4o.inside.query.AbstractQueryResult SupportElementAccess()
		{
			return ToIdList();
		}
	}
}
