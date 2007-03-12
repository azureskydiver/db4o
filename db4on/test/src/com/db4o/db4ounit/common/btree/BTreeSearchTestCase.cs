namespace com.db4o.db4ounit.common.btree
{
	public class BTreeSearchTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase, Db4oUnit.Extensions.Fixtures.OptOutDefragSolo
		, Db4oUnit.Extensions.Fixtures.OptOutCS
	{
		protected const int BTREE_NODE_SIZE = 4;

		public static void Main(string[] arguments)
		{
			new com.db4o.db4ounit.common.btree.BTreeSearchTestCase().RunSolo();
		}

		public virtual void Test()
		{
			CycleIntKeys(new int[] { 3, 5, 7, 10, 11, 12, 14, 15, 17, 20, 21, 25 });
		}

		private void CycleIntKeys(int[] values)
		{
			com.db4o.@internal.btree.BTree btree = com.db4o.db4ounit.common.btree.BTreeAssert
				.CreateIntKeyBTree(Stream(), 0, BTREE_NODE_SIZE);
			for (int i = 0; i < 5; i++)
			{
				btree = CycleIntKeys(btree, values);
			}
		}

		private com.db4o.@internal.btree.BTree CycleIntKeys(com.db4o.@internal.btree.BTree
			 btree, int[] values)
		{
			for (int i = 0; i < values.Length; i++)
			{
				btree.Add(Trans(), values[i]);
			}
			ExpectKeysSearch(Trans(), btree, values);
			btree.Commit(Trans());
			int id = btree.GetID();
			Stream().Commit();
			Reopen();
			btree = com.db4o.db4ounit.common.btree.BTreeAssert.CreateIntKeyBTree(Stream(), id
				, BTREE_NODE_SIZE);
			ExpectKeysSearch(Trans(), btree, values);
			for (int i = 0; i < values.Length; i++)
			{
				btree.Remove(Trans(), values[i]);
			}
			com.db4o.db4ounit.common.btree.BTreeAssert.AssertEmpty(Trans(), btree);
			btree.Commit(Trans());
			com.db4o.db4ounit.common.btree.BTreeAssert.AssertEmpty(Trans(), btree);
			return btree;
		}

		private void ExpectKeysSearch(com.db4o.@internal.Transaction trans, com.db4o.@internal.btree.BTree
			 btree, int[] keys)
		{
			int lastValue = int.MinValue;
			for (int i = 0; i < keys.Length; i++)
			{
				if (keys[i] != lastValue)
				{
					com.db4o.db4ounit.common.btree.ExpectingVisitor expectingVisitor = com.db4o.db4ounit.common.btree.BTreeAssert
						.CreateExpectingVisitor(keys[i], com.db4o.db4ounit.common.foundation.IntArrays4.
						Occurences(keys, keys[i]));
					com.db4o.@internal.btree.BTreeRange range = btree.Search(trans, keys[i]);
					com.db4o.db4ounit.common.btree.BTreeAssert.TraverseKeys(range, expectingVisitor);
					expectingVisitor.AssertExpectations();
					lastValue = keys[i];
				}
			}
		}
	}
}
