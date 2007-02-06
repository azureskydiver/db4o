namespace com.db4o.@internal.cs
{
	/// <exclude></exclude>
	public class ClientQueryResult : com.db4o.@internal.query.result.IdListQueryResult
	{
		public ClientQueryResult(com.db4o.@internal.Transaction ta) : base(ta)
		{
		}

		public ClientQueryResult(com.db4o.@internal.Transaction ta, int initialSize) : base
			(ta, initialSize)
		{
		}

		public override System.Collections.IEnumerator GetEnumerator()
		{
			return new com.db4o.@internal.cs.ClientQueryResultIterator(this);
		}
	}
}
