namespace com.db4o.db4ounit.common.fieldindex
{
	public class IndexedNodeTestCase : com.db4o.db4ounit.common.fieldindex.FieldIndexProcessorTestCaseBase
	{
		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.fieldindex.IndexedNodeTestCase().RunSolo();
		}

		protected override void Store()
		{
			StoreItems(new int[] { 3, 4, 7, 9 });
			StoreComplexItems(new int[] { 3, 4, 7, 9 }, new int[] { 2, 2, 8, 8 });
		}

		public virtual void TestTwoLevelDescendOr()
		{
			com.db4o.query.Query query = CreateComplexItemQuery();
			com.db4o.query.Constraint c1 = query.Descend("child").Descend("foo").Constrain(4)
				.Smaller();
			com.db4o.query.Constraint c2 = query.Descend("child").Descend("foo").Constrain(4)
				.Greater();
			c1.Or(c2);
			AssertSingleOrNode(query);
		}

		public virtual void TestMultipleOrs()
		{
			com.db4o.query.Query query = CreateComplexItemQuery();
			com.db4o.query.Constraint c1 = query.Descend("foo").Constrain(4).Smaller();
			for (int i = 0; i < 5; i++)
			{
				com.db4o.query.Constraint c2 = query.Descend("foo").Constrain(4).Greater();
				c1 = c1.Or(c2);
			}
			AssertSingleOrNode(query);
		}

		public virtual void TestDoubleDescendingOnIndexedNodes()
		{
			com.db4o.query.Query query = CreateComplexItemQuery();
			query.Descend("child").Descend("foo").Constrain(3);
			query.Descend("bar").Constrain(2);
			com.db4o.inside.fieldindex.IndexedNode index = SelectBestIndex(query);
			AssertComplexItemIndex("foo", index);
			Db4oUnit.Assert.IsFalse(index.IsResolved());
			com.db4o.inside.fieldindex.IndexedNode result = index.Resolve();
			Db4oUnit.Assert.IsNotNull(result);
			AssertComplexItemIndex("child", result);
			Db4oUnit.Assert.IsTrue(result.IsResolved());
			Db4oUnit.Assert.IsNull(result.Resolve());
			AssertComplexItems(new int[] { 4 }, result.ToTreeInt());
		}

		public virtual void TestTripleDescendingOnQuery()
		{
			com.db4o.query.Query query = CreateComplexItemQuery();
			query.Descend("child").Descend("child").Descend("foo").Constrain(3);
			com.db4o.inside.fieldindex.IndexedNode index = SelectBestIndex(query);
			AssertComplexItemIndex("foo", index);
			Db4oUnit.Assert.IsFalse(index.IsResolved());
			com.db4o.inside.fieldindex.IndexedNode result = index.Resolve();
			Db4oUnit.Assert.IsNotNull(result);
			AssertComplexItemIndex("child", result);
			Db4oUnit.Assert.IsFalse(result.IsResolved());
			result = result.Resolve();
			Db4oUnit.Assert.IsNotNull(result);
			AssertComplexItemIndex("child", result);
			AssertComplexItems(new int[] { 7 }, result.ToTreeInt());
		}

		private void AssertComplexItems(int[] expectedFoos, com.db4o.TreeInt found)
		{
			Db4oUnit.Assert.IsNotNull(found);
			AssertTreeInt(MapToObjectIds(CreateComplexItemQuery(), expectedFoos), found);
		}

		private void AssertSingleOrNode(com.db4o.query.Query query)
		{
			System.Collections.IEnumerator nodes = CreateProcessor(query).CollectIndexedNodes
				();
			Db4oUnit.Assert.IsTrue(nodes.MoveNext());
			com.db4o.inside.fieldindex.OrIndexedLeaf node = (com.db4o.inside.fieldindex.OrIndexedLeaf
				)nodes.Current;
			Db4oUnit.Assert.IsNotNull(node);
			Db4oUnit.Assert.IsFalse(nodes.MoveNext());
		}
	}
}
