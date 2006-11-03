namespace com.db4o.db4ounit.common.btree
{
	public class BTreeRollbackTestCase : com.db4o.db4ounit.common.btree.BTreeTestCaseBase
	{
		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.btree.BTreeRollbackTestCase().RunSolo();
		}

		private static readonly int[] COMMITTED_VALUES = new int[] { 6, 8, 15, 45, 43, 9, 
			23, 25, 7, 3, 2 };

		private static readonly int[] ROLLED_BACK_VALUES = new int[] { 16, 18, 115, 19, 17
			, 13, 12 };

		public virtual void Test()
		{
			Add(COMMITTED_VALUES);
			CommitBTree();
			for (int i = 0; i < 5; i++)
			{
				Add(ROLLED_BACK_VALUES);
				RollbackBTree();
			}
			com.db4o.db4ounit.common.btree.BTreeAssert.AssertKeys(Trans(), _btree, COMMITTED_VALUES
				);
		}

		private void CommitBTree()
		{
			_btree.Commit(Trans());
			Trans().Commit();
		}

		private void RollbackBTree()
		{
			_btree.Rollback(Trans());
			Trans().Rollback();
		}
	}
}
