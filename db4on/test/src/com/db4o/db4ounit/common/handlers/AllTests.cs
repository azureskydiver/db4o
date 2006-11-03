namespace com.db4o.db4ounit.common.handlers
{
	public class AllTests : Db4oUnit.Extensions.Db4oTestSuite
	{
		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.handlers.AllTests().RunSolo();
		}

		protected override System.Type[] TestCases()
		{
			return new System.Type[] { typeof(com.db4o.db4ounit.common.handlers.YapStringTestCase)
				, typeof(com.db4o.db4ounit.common.handlers.YDoubleTestCase) };
		}
	}
}
