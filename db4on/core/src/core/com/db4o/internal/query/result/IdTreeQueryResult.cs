namespace com.db4o.@internal.query.result
{
	/// <exclude></exclude>
	public class IdTreeQueryResult : com.db4o.@internal.query.result.AbstractQueryResult
	{
		private com.db4o.foundation.Tree _ids;

		public IdTreeQueryResult(com.db4o.@internal.Transaction transaction, com.db4o.foundation.IntIterator4
			 ids) : base(transaction)
		{
			_ids = com.db4o.@internal.TreeInt.AddAll(null, ids);
		}

		public override com.db4o.foundation.IntIterator4 IterateIDs()
		{
			return new com.db4o.foundation.IntIterator4Adaptor(new com.db4o.foundation.TreeKeyIterator
				(_ids));
		}

		public override int Size()
		{
			if (_ids == null)
			{
				return 0;
			}
			return _ids.Size();
		}

		public override com.db4o.@internal.query.result.AbstractQueryResult SupportSort()
		{
			return ToIdList();
		}

		public override com.db4o.@internal.query.result.AbstractQueryResult SupportElementAccess
			()
		{
			return ToIdList();
		}
	}
}
