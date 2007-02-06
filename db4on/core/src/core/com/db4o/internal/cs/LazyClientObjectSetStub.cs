namespace com.db4o.@internal.cs
{
	/// <exclude></exclude>
	public class LazyClientObjectSetStub
	{
		private readonly com.db4o.@internal.query.result.AbstractQueryResult _queryResult;

		private com.db4o.foundation.IntIterator4 _idIterator;

		public LazyClientObjectSetStub(com.db4o.@internal.query.result.AbstractQueryResult
			 queryResult, com.db4o.foundation.IntIterator4 idIterator)
		{
			_queryResult = queryResult;
			_idIterator = idIterator;
		}

		public virtual com.db4o.foundation.IntIterator4 IdIterator()
		{
			return _idIterator;
		}

		public virtual com.db4o.@internal.query.result.AbstractQueryResult QueryResult()
		{
			return _queryResult;
		}

		public virtual void Reset()
		{
			_idIterator = _queryResult.IterateIDs();
		}
	}
}
