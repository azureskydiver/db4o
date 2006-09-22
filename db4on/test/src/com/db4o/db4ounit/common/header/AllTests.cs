namespace com.db4o.db4ounit.common.header
{
	public class AllTests : Db4oUnit.Extensions.Db4oTestSuite
	{
		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.header.AllTests().RunSolo();
		}

		protected override System.Type[] TestCases()
		{
			return new System.Type[] { typeof(com.db4o.db4ounit.common.header.OldHeaderTest), 
				typeof(com.db4o.db4ounit.common.header.ConfigurationSettingsTestCase), typeof(com.db4o.db4ounit.common.header.IdentityTestCase
				), typeof(com.db4o.db4ounit.common.header.SimpleTimeStampIdTestCase) };
		}
	}
}
