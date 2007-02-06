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
			return new System.Type[] { typeof(com.db4o.db4ounit.common.assorted.AliasesTestCase)
				, typeof(com.db4o.db4ounit.common.assorted.BackupStressTestCase), typeof(com.db4o.db4ounit.common.assorted.CanUpdateFalseRefreshTestCase)
				, typeof(com.db4o.db4ounit.common.assorted.CascadedDeleteReaddTestCase), typeof(com.db4o.db4ounit.common.assorted.ChangeIdentity)
				, typeof(com.db4o.db4ounit.common.assorted.CloseUnlocksFileTestCase), typeof(com.db4o.db4ounit.common.assorted.ComparatorSortTestCase)
				, typeof(com.db4o.db4ounit.common.assorted.DatabaseUnicityTest), typeof(com.db4o.db4ounit.common.assorted.DescendToNullFieldTestCase)
				, typeof(com.db4o.db4ounit.common.assorted.GetByUUIDTestCase), typeof(com.db4o.db4ounit.common.assorted.GetSingleSimpleArrayTestCase)
				, typeof(com.db4o.db4ounit.common.assorted.IndexCreateDropTestCase), typeof(com.db4o.db4ounit.common.assorted.LongLinkedListTestCase)
				, typeof(com.db4o.db4ounit.common.assorted.NakedObjectTestCase), typeof(com.db4o.db4ounit.common.assorted.ReAddCascadedDeleteTestCase)
				, typeof(com.db4o.db4ounit.common.assorted.SimplestPossibleTestCase), typeof(com.db4o.db4ounit.common.assorted.MultiDeleteTestCase)
				, typeof(com.db4o.db4ounit.common.assorted.PersistStaticFieldValuesTestCase), typeof(com.db4o.db4ounit.common.assorted.PersistTypeTestCase)
				, typeof(com.db4o.db4ounit.common.assorted.ServerRevokeAccessTestCase), typeof(com.db4o.db4ounit.common.assorted.SystemInfoTestCase)
				, typeof(com.db4o.db4ounit.common.assorted.ObjectVersionTest), typeof(com.db4o.db4ounit.common.assorted.HandlerRegistryTestCase)
				, typeof(com.db4o.db4ounit.common.assorted.ClassMetadataTestCase) };
		}
	}
}
