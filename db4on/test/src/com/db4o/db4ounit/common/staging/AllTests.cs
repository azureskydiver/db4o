namespace com.db4o.db4ounit.common.staging
{
	public class AllTests : Db4oUnit.Extensions.Db4oTestSuite
	{
		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.staging.AllTests().RunSolo();
		}

		protected override System.Type[] TestCases()
		{
			return new System.Type[] { typeof(com.db4o.db4ounit.common.staging.LazyQueryDeleteTestCase)
				 };
		}
	}
}
