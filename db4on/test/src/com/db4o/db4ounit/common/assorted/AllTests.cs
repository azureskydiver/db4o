namespace com.db4o.db4ounit.common.assorted
{
	public class AllTests : Db4oUnit.Extensions.Db4oTestSuite
	{
		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.assorted.AllTests().RunSolo();
		}

		protected override System.Type[] TestCases()
		{
			return new System.Type[] { typeof(com.db4o.db4ounit.common.assorted.BackupStressTestCase)
				, typeof(com.db4o.db4ounit.common.assorted.CloseUnlocksFileTestCase), typeof(com.db4o.db4ounit.common.assorted.DatabaseUnicityTest)
				, typeof(com.db4o.db4ounit.common.assorted.GetByUUIDTestCase), typeof(com.db4o.db4ounit.common.assorted.IndexCreateDropTestCase)
				, typeof(com.db4o.db4ounit.common.assorted.NakedObjectTestCase), typeof(com.db4o.db4ounit.common.assorted.ReAddCascadedDeleteTestCase)
				, typeof(com.db4o.db4ounit.common.assorted.SimplestPossibleTestCase), typeof(com.db4o.db4ounit.common.assorted.MultiDeleteTestCase)
				, typeof(com.db4o.db4ounit.common.assorted.NonStaticConfigurationTestCase), typeof(com.db4o.db4ounit.common.assorted.ServerRevokeAccessTestCase)
				, typeof(com.db4o.db4ounit.common.assorted.SystemInfoTestCase), typeof(com.db4o.db4ounit.common.assorted.ObjectVersionTest)
				, typeof(com.db4o.db4ounit.common.assorted.YapClassTestCase) };
		}
	}
}
