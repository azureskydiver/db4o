namespace com.db4o.db4ounit.common.btree
{
	/// <exclude></exclude>
	public class BTreePointerTestCase : com.db4o.db4ounit.common.btree.BTreeTestCaseBase
	{
		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.btree.BTreePointerTestCase().RunSolo();
		}

		private readonly int[] keys = new int[] { -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 7, 9
			 };

		protected override void Db4oSetupAfterStore()
		{
			base.Db4oSetupAfterStore();
			Add(keys);
			Commit();
		}

		public virtual void TestLastPointer()
		{
			com.db4o.inside.btree.BTreePointer pointer = _btree.LastPointer(Trans());
			AssertPointerKey(9, pointer);
		}

		public virtual void TestPrevious()
		{
			com.db4o.inside.btree.BTreePointer pointer = GetPointerForKey(3);
			com.db4o.inside.btree.BTreePointer previousPointer = pointer.Previous();
			AssertPointerKey(2, previousPointer);
		}

		public virtual void TestNextOperatesInReadMode()
		{
			com.db4o.inside.btree.BTreePointer pointer = _btree.FirstPointer(Trans());
			AssertReadModePointerIteration(keys, pointer);
		}

		public virtual void TestSearchOperatesInReadMode()
		{
			com.db4o.inside.btree.BTreePointer pointer = GetPointerForKey(3);
			AssertReadModePointerIteration(new int[] { 3, 4, 7, 9 }, pointer);
		}

		private com.db4o.inside.btree.BTreePointer GetPointerForKey(int key)
		{
			com.db4o.inside.btree.BTreeRange range = Search(key);
			System.Collections.IEnumerator pointers = range.Pointers();
			Db4oUnit.Assert.IsTrue(pointers.MoveNext());
			com.db4o.inside.btree.BTreePointer pointer = (com.db4o.inside.btree.BTreePointer)
				pointers.Current;
			return pointer;
		}

		private void AssertReadModePointerIteration(int[] expectedKeys, com.db4o.inside.btree.BTreePointer
			 pointer)
		{
			object[] expected = com.db4o.db4ounit.common.foundation.IntArrays4.ToObjectArray(
				expectedKeys);
			for (int i = 0; i < expected.Length; i++)
			{
				Db4oUnit.Assert.IsNotNull(pointer, "Expected '" + expected[i] + "'");
				Db4oUnit.Assert.AreNotSame(_btree.Root(), pointer.Node());
				AssertInReadMode(pointer.Node());
				Db4oUnit.Assert.AreEqual(expected[i], pointer.Key());
				AssertInReadMode(pointer.Node());
				pointer = pointer.Next();
			}
		}

		private void AssertInReadMode(com.db4o.inside.btree.BTreeNode node)
		{
			Db4oUnit.Assert.IsFalse(node.CanWrite());
		}

		protected override com.db4o.inside.btree.BTree NewBTree()
		{
			return NewBTreeWithNoNodeCaching();
		}

		private com.db4o.inside.btree.BTree NewBTreeWithNoNodeCaching()
		{
			return com.db4o.db4ounit.common.btree.BTreeAssert.CreateIntKeyBTree(Stream(), 0, 
				0, BTREE_NODE_SIZE);
		}
	}
}
