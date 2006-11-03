namespace com.db4o.db4ounit.common.btree
{
	public class AllTests : Db4oUnit.Extensions.Db4oTestSuite
	{
		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.btree.AllTests().RunSolo();
		}

		protected override System.Type[] TestCases()
		{
			return new System.Type[] { typeof(com.db4o.db4ounit.common.btree.BTreeAddRemoveTestCase)
				, typeof(com.db4o.db4ounit.common.btree.BTreeNodeTestCase), typeof(com.db4o.db4ounit.common.btree.BTreeFreeTestCase)
				, typeof(com.db4o.db4ounit.common.btree.BTreePointerTestCase), typeof(com.db4o.db4ounit.common.btree.BTreeRangeTestCase)
				, typeof(com.db4o.db4ounit.common.btree.BTreeRollbackTestCase), typeof(com.db4o.db4ounit.common.btree.BTreeSearchTestCase)
				, typeof(com.db4o.db4ounit.common.btree.BTreeSimpleTestCase), typeof(com.db4o.db4ounit.common.btree.SearcherLowestHighestTestCase)
				, typeof(com.db4o.db4ounit.common.btree.SearcherTestCase) };
		}
	}
}
