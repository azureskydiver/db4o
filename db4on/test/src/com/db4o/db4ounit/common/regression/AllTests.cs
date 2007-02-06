namespace com.db4o.db4ounit.common.regression
{
	public class AllTests : Db4oUnit.Extensions.Db4oTestSuite
	{
		protected override System.Type[] TestCases()
		{
			return new System.Type[] { typeof(com.db4o.db4ounit.common.regression.COR57TestCase)
				, typeof(com.db4o.db4ounit.common.regression.COR234TestCase) };
		}

		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.regression.AllTests().RunSolo();
		}
	}
}
