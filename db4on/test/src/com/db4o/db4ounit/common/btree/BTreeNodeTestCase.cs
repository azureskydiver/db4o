namespace com.db4o.db4ounit.common.btree
{
	public class BTreeNodeTestCase : com.db4o.db4ounit.common.btree.BTreeTestCaseBase
	{
		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.btree.BTreeNodeTestCase().RunSolo();
		}

		private readonly int[] keys = new int[] { -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 7, 9
			 };

		protected override void Db4oSetupAfterStore()
		{
			base.Db4oSetupAfterStore();
			Add(keys);
			Commit();
		}

		public virtual void TestLastKeyIndex()
		{
			com.db4o.@internal.btree.BTreeNode node = Node(3);
			Db4oUnit.Assert.AreEqual(1, node.LastKeyIndex(Trans()));
			com.db4o.@internal.Transaction trans = NewTransaction();
			_btree.Add(trans, 5);
			Db4oUnit.Assert.AreEqual(1, node.LastKeyIndex(Trans()));
			_btree.Commit(trans);
			Db4oUnit.Assert.AreEqual(2, node.LastKeyIndex(Trans()));
		}

		private com.db4o.@internal.btree.BTreeNode Node(int value)
		{
			com.db4o.@internal.btree.BTreeRange range = Search(value);
			System.Collections.IEnumerator i = range.Pointers();
			i.MoveNext();
			com.db4o.@internal.btree.BTreePointer firstPointer = (com.db4o.@internal.btree.BTreePointer
				)i.Current;
			com.db4o.@internal.btree.BTreeNode node = firstPointer.Node();
			node.DebugLoadFully(SystemTrans());
			return node;
		}

		public virtual void TestLastPointer()
		{
			com.db4o.@internal.btree.BTreeNode node = Node(3);
			com.db4o.@internal.btree.BTreePointer lastPointer = node.LastPointer(Trans());
			AssertPointerKey(4, lastPointer);
		}
	}
}
