namespace com.db4o.db4ounit.common.btree
{
	public class BTreeAssert
	{
		public static com.db4o.db4ounit.common.btree.ExpectingVisitor CreateExpectingVisitor
			(int value, int count)
		{
			int[] values = new int[count];
			for (int i = 0; i < values.Length; i++)
			{
				values[i] = value;
			}
			return new com.db4o.db4ounit.common.btree.ExpectingVisitor(com.db4o.db4ounit.common.foundation.IntArrays4
				.ToObjectArray(values));
		}

		public static com.db4o.db4ounit.common.btree.ExpectingVisitor CreateExpectingVisitor
			(int[] keys)
		{
			return new com.db4o.db4ounit.common.btree.ExpectingVisitor(com.db4o.db4ounit.common.foundation.IntArrays4
				.ToObjectArray(keys));
		}

		private static com.db4o.db4ounit.common.btree.ExpectingVisitor CreateSortedExpectingVisitor
			(int[] keys)
		{
			return new com.db4o.db4ounit.common.btree.ExpectingVisitor(com.db4o.db4ounit.common.foundation.IntArrays4
				.ToObjectArray(keys), true, false);
		}

		public static void TraverseKeys(com.db4o.@internal.btree.BTreeRange result, com.db4o.foundation.Visitor4
			 visitor)
		{
			System.Collections.IEnumerator i = result.Keys();
			while (i.MoveNext())
			{
				visitor.Visit(i.Current);
			}
		}

		public static void AssertKeys(com.db4o.@internal.Transaction transaction, com.db4o.@internal.btree.BTree
			 btree, int[] keys)
		{
			com.db4o.db4ounit.common.btree.ExpectingVisitor visitor = CreateExpectingVisitor(
				keys);
			btree.TraverseKeys(transaction, visitor);
			visitor.AssertExpectations();
		}

		public static void AssertEmpty(com.db4o.@internal.Transaction transaction, com.db4o.@internal.btree.BTree
			 tree)
		{
			com.db4o.db4ounit.common.btree.ExpectingVisitor visitor = new com.db4o.db4ounit.common.btree.ExpectingVisitor
				(new object[0]);
			tree.TraverseKeys(transaction, visitor);
			visitor.AssertExpectations();
			Db4oUnit.Assert.AreEqual(0, tree.Size(transaction));
		}

		public static void DumpKeys(com.db4o.@internal.Transaction trans, com.db4o.@internal.btree.BTree
			 tree)
		{
			tree.TraverseKeys(trans, new _AnonymousInnerClass51());
		}

		private sealed class _AnonymousInnerClass51 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass51()
			{
			}

			public void Visit(object obj)
			{
				j4o.lang.JavaSystem.Out.WriteLine(obj);
			}
		}

		public static com.db4o.db4ounit.common.btree.ExpectingVisitor CreateExpectingVisitor
			(int expectedID)
		{
			return CreateExpectingVisitor(expectedID, 1);
		}

		public static int FillSize(com.db4o.@internal.btree.BTree btree)
		{
			return btree.NodeSize() + 1;
		}

		public static int[] NewBTreeNodeSizedArray(com.db4o.@internal.btree.BTree btree, 
			int value)
		{
			return com.db4o.db4ounit.common.foundation.IntArrays4.Fill(new int[FillSize(btree
				)], value);
		}

		public static void AssertRange(int[] expectedKeys, com.db4o.@internal.btree.BTreeRange
			 range)
		{
			Db4oUnit.Assert.IsNotNull(range);
			com.db4o.db4ounit.common.btree.ExpectingVisitor visitor = CreateSortedExpectingVisitor
				(expectedKeys);
			TraverseKeys(range, visitor);
			visitor.AssertExpectations();
		}

		public static com.db4o.@internal.btree.BTree CreateIntKeyBTree(com.db4o.@internal.ObjectContainerBase
			 stream, int id, int nodeSize)
		{
			return new com.db4o.@internal.btree.BTree(stream.GetSystemTransaction(), id, new 
				com.db4o.@internal.handlers.IntHandler(stream), null, nodeSize, stream.ConfigImpl
				().BTreeCacheHeight());
		}

		public static com.db4o.@internal.btree.BTree CreateIntKeyBTree(com.db4o.@internal.ObjectContainerBase
			 stream, int id, int treeCacheHeight, int nodeSize)
		{
			return new com.db4o.@internal.btree.BTree(stream.GetSystemTransaction(), id, new 
				com.db4o.@internal.handlers.IntHandler(stream), null, nodeSize, treeCacheHeight);
		}

		public static void AssertSingleElement(com.db4o.@internal.Transaction trans, com.db4o.@internal.btree.BTree
			 btree, object element)
		{
			Db4oUnit.Assert.AreEqual(1, btree.Size(trans));
			com.db4o.@internal.btree.BTreeRange result = btree.Search(trans, element);
			com.db4o.db4ounit.common.btree.ExpectingVisitor expectingVisitor = new com.db4o.db4ounit.common.btree.ExpectingVisitor
				(new object[] { element });
			com.db4o.db4ounit.common.btree.BTreeAssert.TraverseKeys(result, expectingVisitor);
			expectingVisitor.AssertExpectations();
			expectingVisitor = new com.db4o.db4ounit.common.btree.ExpectingVisitor(new object
				[] { element });
			btree.TraverseKeys(trans, expectingVisitor);
			expectingVisitor.AssertExpectations();
		}
	}
}
