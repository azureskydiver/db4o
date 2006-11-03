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

		public static void TraverseKeys(com.db4o.inside.btree.BTreeRange result, com.db4o.foundation.Visitor4
			 visitor)
		{
			System.Collections.IEnumerator i = result.Keys();
			while (i.MoveNext())
			{
				visitor.Visit(i.Current);
			}
		}

		public static void AssertKeys(com.db4o.Transaction transaction, com.db4o.inside.btree.BTree
			 btree, int[] keys)
		{
			com.db4o.db4ounit.common.btree.ExpectingVisitor visitor = CreateExpectingVisitor(
				keys);
			btree.TraverseKeys(transaction, visitor);
			visitor.AssertExpectations();
		}

		public static void AssertEmpty(com.db4o.Transaction transaction, com.db4o.inside.btree.BTree
			 tree)
		{
			com.db4o.db4ounit.common.btree.ExpectingVisitor visitor = new com.db4o.db4ounit.common.btree.ExpectingVisitor
				(new object[0]);
			tree.TraverseKeys(transaction, visitor);
			visitor.AssertExpectations();
			Db4oUnit.Assert.AreEqual(0, tree.Size(transaction));
		}

		public static void DumpKeys(com.db4o.Transaction trans, com.db4o.inside.btree.BTree
			 tree)
		{
			tree.TraverseKeys(trans, new _AnonymousInnerClass49());
		}

		private sealed class _AnonymousInnerClass49 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass49()
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

		public static int FillSize(com.db4o.inside.btree.BTree btree)
		{
			return btree.NodeSize() + 1;
		}

		public static int[] NewBTreeNodeSizedArray(com.db4o.inside.btree.BTree btree, int
			 value)
		{
			return com.db4o.db4ounit.common.foundation.IntArrays4.Fill(new int[FillSize(btree
				)], value);
		}

		public static void AssertRange(int[] expectedKeys, com.db4o.inside.btree.BTreeRange
			 range)
		{
			Db4oUnit.Assert.IsNotNull(range);
			com.db4o.db4ounit.common.btree.ExpectingVisitor visitor = CreateSortedExpectingVisitor
				(expectedKeys);
			TraverseKeys(range, visitor);
			visitor.AssertExpectations();
		}

		public static com.db4o.inside.btree.BTree CreateIntKeyBTree(com.db4o.YapStream stream
			, int id, int nodeSize)
		{
			return new com.db4o.inside.btree.BTree(stream.GetSystemTransaction(), id, new com.db4o.YInt
				(stream), null, nodeSize, stream.ConfigImpl().BTreeCacheHeight());
		}

		public static com.db4o.inside.btree.BTree CreateIntKeyBTree(com.db4o.YapStream stream
			, int id, int treeCacheHeight, int nodeSize)
		{
			return new com.db4o.inside.btree.BTree(stream.GetSystemTransaction(), id, new com.db4o.YInt
				(stream), null, nodeSize, treeCacheHeight);
		}

		public static void AssertSingleElement(com.db4o.Transaction trans, com.db4o.inside.btree.BTree
			 btree, object element)
		{
			Db4oUnit.Assert.AreEqual(1, btree.Size(trans));
			com.db4o.inside.btree.BTreeRange result = btree.Search(trans, element);
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
