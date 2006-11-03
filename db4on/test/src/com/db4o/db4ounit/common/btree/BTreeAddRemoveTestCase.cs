namespace com.db4o.db4ounit.common.btree
{
	public class BTreeAddRemoveTestCase : com.db4o.db4ounit.common.btree.BTreeTestCaseBase
	{
		public virtual void TestFirstPointerMultiTransactional()
		{
			int count = BTREE_NODE_SIZE + 1;
			for (int i = 0; i < count; i++)
			{
				Add(count + i + 1);
			}
			int smallest = count + 1;
			com.db4o.Transaction trans = NewTransaction();
			for (int i = 0; i < count; i++)
			{
				Add(trans, i);
			}
			com.db4o.inside.btree.BTreePointer firstPointer = _btree.FirstPointer(Trans());
			AssertPointerKey(smallest, firstPointer);
		}

		public virtual void TestSingleRemoveAdd()
		{
			int element = 1;
			Add(element);
			AssertSize(1);
			Remove(element);
			AssertSize(0);
			Add(element);
			AssertSingleElement(element);
		}

		public virtual void TestSearchingRemoved()
		{
			int[] keys = new int[] { 3, 4, 7, 9 };
			Add(keys);
			Remove(4);
			com.db4o.inside.btree.BTreeRange result = Search(4);
			Db4oUnit.Assert.IsTrue(result.IsEmpty());
			com.db4o.inside.btree.BTreeRange range = result.Greater();
			com.db4o.db4ounit.common.btree.BTreeAssert.AssertRange(new int[] { 7, 9 }, range);
		}

		public virtual void TestMultipleRemoveAdds()
		{
			int element = 1;
			Add(element);
			Remove(element);
			Remove(element);
			Add(element);
			AssertSingleElement(element);
		}

		public virtual void TestMultiTransactionCancelledRemoval()
		{
			int element = 1;
			Add(element);
			Commit();
			com.db4o.Transaction trans1 = NewTransaction();
			com.db4o.Transaction trans2 = NewTransaction();
			Remove(trans1, element);
			AssertSingleElement(trans2, element);
			Add(trans1, element);
			AssertSingleElement(trans1, element);
			AssertSingleElement(trans2, element);
			trans1.Commit();
			AssertSingleElement(element);
		}

		public virtual void TestMultiTransactionSearch()
		{
			int[] keys = new int[] { 3, 4, 7, 9 };
			Add(Trans(), keys);
			Commit(Trans());
			int[] assorted = new int[] { 1, 2, 11, 13, 21, 52, 51, 66, 89, 10 };
			Add(SystemTrans(), assorted);
			AssertKeys(keys);
			Remove(SystemTrans(), assorted);
			AssertKeys(keys);
			com.db4o.db4ounit.common.btree.BTreeAssert.AssertRange(new int[] { 7, 9 }, Search
				(Trans(), 4).Greater());
		}

		private void AssertKeys(int[] keys)
		{
			com.db4o.db4ounit.common.btree.BTreeAssert.AssertKeys(Trans(), _btree, keys);
		}

		public virtual void TestAddRemoveInDifferentTransactions()
		{
			int element = 1;
			Add(Trans(), element);
			Add(SystemTrans(), element);
			Remove(SystemTrans(), element);
			Remove(Trans(), element);
			AssertEmpty(SystemTrans());
			AssertEmpty(Trans());
		}

		public virtual void TestRemoveAddInDifferentTransactions()
		{
			int element = 1;
			Add(element);
			Db().Commit();
			Remove(Trans(), element);
			Remove(SystemTrans(), element);
			AssertEmpty(SystemTrans());
			AssertEmpty(Trans());
			Add(Trans(), element);
			AssertSingleElement(Trans(), element);
			Add(SystemTrans(), element);
			AssertSingleElement(SystemTrans(), element);
		}

		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.btree.BTreeAddRemoveTestCase().RunSolo();
		}
	}
}
