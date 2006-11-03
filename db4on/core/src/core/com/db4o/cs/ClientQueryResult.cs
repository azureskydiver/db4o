namespace com.db4o.cs
{
	/// <exclude></exclude>
	public class ClientQueryResult : com.db4o.inside.query.IdListQueryResult
	{
		public ClientQueryResult(com.db4o.Transaction ta) : base(ta)
		{
		}

		public ClientQueryResult(com.db4o.Transaction ta, int initialSize) : base(ta, initialSize
			)
		{
		}

		public override System.Collections.IEnumerator GetEnumerator()
		{
			return new com.db4o.cs.ClientQueryResultIterator(this);
		}
	}
}
