namespace com.db4o.db4ounit.common.querying
{
	public class AllTests : Db4oUnit.Extensions.Db4oTestSuite
	{
		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.querying.AllTests().RunSoloAndClientServer();
		}

		protected override System.Type[] TestCases()
		{
			return new System.Type[] { typeof(com.db4o.db4ounit.common.querying.IdListQueryResultTestCase)
				, typeof(com.db4o.db4ounit.common.querying.IndexedQueriesTestCase), typeof(com.db4o.db4ounit.common.querying.LazyQueryResultTestCase)
				, typeof(com.db4o.db4ounit.common.querying.MultiFieldIndexQueryTestCase), typeof(com.db4o.db4ounit.common.querying.ObjectSetTestCase)
				, typeof(com.db4o.db4ounit.common.querying.OrderedQueryTestCase) };
		}
	}
}
