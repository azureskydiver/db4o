namespace com.db4o.db4ounit.common.querying
{
	public class IdListQueryResultTestCase : com.db4o.db4ounit.common.querying.QueryResultTestCase
	{
		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.querying.IdListQueryResultTestCase().RunSolo();
		}

		protected override com.db4o.@internal.query.result.AbstractQueryResult NewQueryResult
			()
		{
			return new com.db4o.@internal.query.result.IdListQueryResult(Trans());
		}
	}
}
