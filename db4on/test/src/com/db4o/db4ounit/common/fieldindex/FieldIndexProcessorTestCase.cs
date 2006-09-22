namespace com.db4o.db4ounit.common.fieldindex
{
	public class FieldIndexProcessorTestCase : com.db4o.db4ounit.common.fieldindex.FieldIndexProcessorTestCaseBase
	{
		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.fieldindex.FieldIndexProcessorTestCase().RunSolo();
		}

		protected override void Configure()
		{
			base.Configure();
			IndexField(typeof(com.db4o.db4ounit.common.fieldindex.NonIndexedFieldIndexItem), 
				"indexed");
		}

		protected override void Store()
		{
			StoreItems(new int[] { 3, 4, 7, 9 });
			StoreComplexItems(new int[] { 3, 4, 7, 9 }, new int[] { 2, 2, 8, 8 });
		}

		public virtual void TestIdentity()
		{
			com.db4o.query.Query query = CreateComplexItemQuery();
			query.Descend("foo").Constrain(3);
			com.db4o.db4ounit.common.fieldindex.ComplexFieldIndexItem item = (com.db4o.db4ounit.common.fieldindex.ComplexFieldIndexItem
				)query.Execute().Next();
			query = CreateComplexItemQuery();
			query.Descend("child").Constrain(item).Identity();
			AssertExpectedFoos(typeof(com.db4o.db4ounit.common.fieldindex.ComplexFieldIndexItem
				), new int[] { 4 }, query);
		}

		public virtual void TestSingleIndexNotSmaller()
		{
			com.db4o.query.Query query = CreateItemQuery();
			query.Descend("foo").Constrain(5).Smaller().Not();
			AssertExpectedFoos(typeof(com.db4o.db4ounit.common.fieldindex.FieldIndexItem), new 
				int[] { 7, 9 }, query);
		}

		public virtual void TestSingleIndexNotGreater()
		{
			com.db4o.query.Query query = CreateItemQuery();
			query.Descend("foo").Constrain(4).Greater().Not();
			AssertExpectedFoos(typeof(com.db4o.db4ounit.common.fieldindex.FieldIndexItem), new 
				int[] { 3, 4 }, query);
		}

		public virtual void TestSingleIndexSmallerOrEqual()
		{
			com.db4o.query.Query query = CreateItemQuery();
			query.Descend("foo").Constrain(7).Smaller().Equal();
			AssertExpectedFoos(typeof(com.db4o.db4ounit.common.fieldindex.FieldIndexItem), new 
				int[] { 3, 4, 7 }, query);
		}

		public virtual void TestSingleIndexGreaterOrEqual()
		{
			com.db4o.query.Query query = CreateItemQuery();
			query.Descend("foo").Constrain(7).Greater().Equal();
			AssertExpectedFoos(typeof(com.db4o.db4ounit.common.fieldindex.FieldIndexItem), new 
				int[] { 7, 9 }, query);
		}

		public virtual void TestSingleIndexRange()
		{
			com.db4o.query.Query query = CreateItemQuery();
			query.Descend("foo").Constrain(3).Greater();
			query.Descend("foo").Constrain(9).Smaller();
			AssertExpectedFoos(typeof(com.db4o.db4ounit.common.fieldindex.FieldIndexItem), new 
				int[] { 4, 7 }, query);
		}

		public virtual void TestSingleIndexAndRange()
		{
			com.db4o.query.Query query = CreateItemQuery();
			com.db4o.query.Constraint c1 = query.Descend("foo").Constrain(3).Greater();
			com.db4o.query.Constraint c2 = query.Descend("foo").Constrain(9).Smaller();
			c1.And(c2);
			AssertExpectedFoos(typeof(com.db4o.db4ounit.common.fieldindex.FieldIndexItem), new 
				int[] { 4, 7 }, query);
		}

		public virtual void TestSingleIndexOr()
		{
			com.db4o.query.Query query = CreateItemQuery();
			com.db4o.query.Constraint c1 = query.Descend("foo").Constrain(4).Smaller();
			com.db4o.query.Constraint c2 = query.Descend("foo").Constrain(7).Greater();
			c1.Or(c2);
			AssertExpectedFoos(typeof(com.db4o.db4ounit.common.fieldindex.FieldIndexItem), new 
				int[] { 3, 9 }, query);
		}

		public virtual void TestExplicitAndOverOr()
		{
			AssertAndOverOrQuery(true);
		}

		public virtual void TestImplicitAndOverOr()
		{
			AssertAndOverOrQuery(false);
		}

		private void AssertAndOverOrQuery(bool explicitAnd)
		{
			com.db4o.query.Query query = CreateItemQuery();
			com.db4o.query.Constraint c1 = query.Descend("foo").Constrain(3);
			com.db4o.query.Constraint c2 = query.Descend("foo").Constrain(9);
			com.db4o.query.Constraint c3 = query.Descend("foo").Constrain(3);
			com.db4o.query.Constraint c4 = query.Descend("foo").Constrain(7);
			com.db4o.query.Constraint cc1 = c1.Or(c2);
			com.db4o.query.Constraint cc2 = c3.Or(c4);
			if (explicitAnd)
			{
				cc1.And(cc2);
			}
			AssertExpectedFoos(typeof(com.db4o.db4ounit.common.fieldindex.FieldIndexItem), new 
				int[] { 3 }, query);
		}

		public virtual void TestSingleIndexOrRange()
		{
			com.db4o.query.Query query = CreateItemQuery();
			com.db4o.query.Constraint c1 = query.Descend("foo").Constrain(1).Greater();
			com.db4o.query.Constraint c2 = query.Descend("foo").Constrain(4).Smaller();
			com.db4o.query.Constraint c3 = query.Descend("foo").Constrain(4).Greater();
			com.db4o.query.Constraint c4 = query.Descend("foo").Constrain(10).Smaller();
			com.db4o.query.Constraint cc1 = c1.And(c2);
			com.db4o.query.Constraint cc2 = c3.And(c4);
			cc1.Or(cc2);
			AssertExpectedFoos(typeof(com.db4o.db4ounit.common.fieldindex.FieldIndexItem), new 
				int[] { 3, 7, 9 }, query);
		}

		public virtual void TestImplicitAndOnOrs()
		{
			com.db4o.query.Query query = CreateItemQuery();
			com.db4o.query.Constraint c1 = query.Descend("foo").Constrain(4).Smaller();
			com.db4o.query.Constraint c2 = query.Descend("foo").Constrain(3).Greater();
			com.db4o.query.Constraint c3 = query.Descend("foo").Constrain(4).Greater();
			c1.Or(c2);
			c1.Or(c3);
			AssertExpectedFoos(typeof(com.db4o.db4ounit.common.fieldindex.FieldIndexItem), new 
				int[] { 3, 4, 7, 9 }, query);
		}

		public virtual void TestTwoLevelDescendOr()
		{
			com.db4o.query.Query query = CreateComplexItemQuery();
			com.db4o.query.Constraint c1 = query.Descend("child").Descend("foo").Constrain(4)
				.Smaller();
			com.db4o.query.Constraint c2 = query.Descend("child").Descend("foo").Constrain(4)
				.Greater();
			c1.Or(c2);
			AssertExpectedFoos(typeof(com.db4o.db4ounit.common.fieldindex.ComplexFieldIndexItem
				), new int[] { 4, 9 }, query);
		}

		public virtual void _testOrOnDifferentFields()
		{
			com.db4o.query.Query query = CreateComplexItemQuery();
			com.db4o.query.Constraint c1 = query.Descend("foo").Constrain(3);
			com.db4o.query.Constraint c2 = query.Descend("bar").Constrain(8);
			c1.Or(c2);
			AssertExpectedFoos(typeof(com.db4o.db4ounit.common.fieldindex.ComplexFieldIndexItem
				), new int[] { 3, 7, 9 }, query);
		}

		public virtual void TestCantOptimizeOrInvolvingNonIndexedField()
		{
			com.db4o.query.Query query = CreateQuery(typeof(com.db4o.db4ounit.common.fieldindex.NonIndexedFieldIndexItem
				));
			com.db4o.query.Constraint c1 = query.Descend("indexed").Constrain(1);
			com.db4o.query.Constraint c2 = query.Descend("foo").Constrain(2);
			c1.Or(c2);
			AssertCantOptimize(query);
		}

		public virtual void TestCantOptimizeDifferentLevels()
		{
			com.db4o.query.Query query = CreateComplexItemQuery();
			com.db4o.query.Constraint c1 = query.Descend("child").Descend("foo").Constrain(4)
				.Smaller();
			com.db4o.query.Constraint c2 = query.Descend("foo").Constrain(7).Greater();
			c1.Or(c2);
			AssertCantOptimize(query);
		}

		public virtual void TestCantOptimizeJoinOnNonIndexedFields()
		{
			com.db4o.query.Query query = CreateQuery(typeof(com.db4o.db4ounit.common.fieldindex.NonIndexedFieldIndexItem
				));
			com.db4o.query.Constraint c1 = query.Descend("foo").Constrain(1);
			com.db4o.query.Constraint c2 = query.Descend("foo").Constrain(2);
			c1.Or(c2);
			AssertCantOptimize(query);
		}

		private void AssertCantOptimize(com.db4o.query.Query query)
		{
			com.db4o.inside.fieldindex.FieldIndexProcessorResult result = ExecuteProcessor(query
				);
			Db4oUnit.Assert.IsNull(result.found);
		}

		public virtual void TestIndexSelection()
		{
			com.db4o.query.Query query = CreateComplexItemQuery();
			query.Descend("bar").Constrain(2);
			query.Descend("foo").Constrain(3);
			AssertBestIndex("foo", query);
			query = CreateComplexItemQuery();
			query.Descend("foo").Constrain(3);
			query.Descend("bar").Constrain(2);
			AssertBestIndex("foo", query);
		}

		private void AssertBestIndex(string expectedFieldIndex, com.db4o.query.Query query
			)
		{
			com.db4o.inside.fieldindex.IndexedNode node = SelectBestIndex(query);
			AssertComplexItemIndex(expectedFieldIndex, node);
		}

		public virtual void TestDoubleDescendingOnQuery()
		{
			com.db4o.query.Query query = CreateComplexItemQuery();
			query.Descend("child").Descend("foo").Constrain(3);
			AssertExpectedFoos(typeof(com.db4o.db4ounit.common.fieldindex.ComplexFieldIndexItem
				), new int[] { 4 }, query);
		}

		public virtual void TestTripleDescendingOnQuery()
		{
			com.db4o.query.Query query = CreateComplexItemQuery();
			query.Descend("child").Descend("child").Descend("foo").Constrain(3);
			AssertExpectedFoos(typeof(com.db4o.db4ounit.common.fieldindex.ComplexFieldIndexItem
				), new int[] { 7 }, query);
		}

		public virtual void TestMultiTransactionSmallerWithCommit()
		{
			com.db4o.Transaction transaction = NewTransaction();
			FillTransactionWith(transaction, 0);
			int[] expectedZeros = NewBTreeNodeSizedArray(0);
			AssertSmaller(transaction, expectedZeros, 3);
			transaction.Commit();
			FillTransactionWith(transaction, 5);
			AssertSmaller(com.db4o.db4ounit.common.foundation.IntArrays4.Concat(expectedZeros
				, new int[] { 3, 4 }), 7);
		}

		public virtual void TestMultiTransactionWithRollback()
		{
			com.db4o.Transaction transaction = NewTransaction();
			FillTransactionWith(transaction, 0);
			int[] expectedZeros = NewBTreeNodeSizedArray(0);
			AssertSmaller(transaction, expectedZeros, 3);
			transaction.Rollback();
			AssertSmaller(transaction, new int[0], 3);
			FillTransactionWith(transaction, 5);
			AssertSmaller(new int[] { 3, 4 }, 7);
		}

		public virtual void TestMultiTransactionSmaller()
		{
			com.db4o.Transaction transaction = NewTransaction();
			FillTransactionWith(transaction, 0);
			int[] expected = NewBTreeNodeSizedArray(0);
			AssertSmaller(transaction, expected, 3);
			FillTransactionWith(transaction, 5);
			AssertSmaller(new int[] { 3, 4 }, 7);
		}

		public virtual void TestMultiTransactionGreater()
		{
			FillTransactionWith(SystemTrans(), 10);
			FillTransactionWith(SystemTrans(), 5);
			AssertGreater(new int[] { 4, 7, 9 }, 3);
			RemoveFromTransaction(SystemTrans(), 5);
			AssertGreater(new int[] { 4, 7, 9 }, 3);
			RemoveFromTransaction(SystemTrans(), 10);
			AssertGreater(new int[] { 4, 7, 9 }, 3);
		}

		public virtual void TestSingleIndexEquals()
		{
			int expectedBar = 3;
			AssertExpectedFoos(typeof(com.db4o.db4ounit.common.fieldindex.FieldIndexItem), new 
				int[] { expectedBar }, CreateQuery(expectedBar));
		}

		public virtual void TestSingleIndexSmaller()
		{
			AssertSmaller(new int[] { 3, 4 }, 7);
		}

		public virtual void TestSingleIndexGreater()
		{
			AssertGreater(new int[] { 4, 7, 9 }, 3);
		}

		private void AssertGreater(int[] expectedFoos, int greaterThan)
		{
			com.db4o.query.Query query = CreateItemQuery();
			query.Descend("foo").Constrain(greaterThan).Greater();
			AssertExpectedFoos(typeof(com.db4o.db4ounit.common.fieldindex.FieldIndexItem), expectedFoos
				, query);
		}

		private void AssertExpectedFoos(System.Type itemClass, int[] expectedFoos, com.db4o.query.Query
			 query)
		{
			com.db4o.Transaction trans = TransactionFromQuery(query);
			int[] expectedIds = MapToObjectIds(CreateQuery(trans, itemClass), expectedFoos);
			AssertExpectedIDs(expectedIds, query);
		}

		private void AssertExpectedIDs(int[] expectedIds, com.db4o.query.Query query)
		{
			com.db4o.inside.fieldindex.FieldIndexProcessorResult result = ExecuteProcessor(query
				);
			if (expectedIds.Length == 0)
			{
				Db4oUnit.Assert.AreSame(com.db4o.inside.fieldindex.FieldIndexProcessorResult.FOUND_INDEX_BUT_NO_MATCH
					, result);
				return;
			}
			Db4oUnit.Assert.IsNotNull(result.found);
			AssertTreeInt(expectedIds, result.found);
		}

		private com.db4o.inside.fieldindex.FieldIndexProcessorResult ExecuteProcessor(com.db4o.query.Query
			 query)
		{
			return CreateProcessor(query).Run();
		}

		private com.db4o.Transaction TransactionFromQuery(com.db4o.query.Query query)
		{
			return ((com.db4o.QQuery)query).GetTransaction();
		}

		private com.db4o.inside.btree.BTree Btree()
		{
			return FieldIndexBTree(typeof(com.db4o.db4ounit.common.fieldindex.FieldIndexItem)
				, "foo");
		}

		private void Store(com.db4o.Transaction trans, com.db4o.db4ounit.common.fieldindex.FieldIndexItem
			 item)
		{
			Stream().Set(trans, item);
		}

		private void FillTransactionWith(com.db4o.Transaction trans, int bar)
		{
			for (int i = 0; i < com.db4o.db4ounit.common.btree.BTreeAssert.FillSize(Btree()); 
				++i)
			{
				Store(trans, new com.db4o.db4ounit.common.fieldindex.FieldIndexItem(bar));
			}
		}

		private int[] NewBTreeNodeSizedArray(int value)
		{
			com.db4o.inside.btree.BTree btree = Btree();
			return com.db4o.db4ounit.common.btree.BTreeAssert.NewBTreeNodeSizedArray(btree, value
				);
		}

		private void RemoveFromTransaction(com.db4o.Transaction trans, int foo)
		{
			com.db4o.ObjectSet found = CreateItemQuery(trans).Execute();
			while (found.HasNext())
			{
				com.db4o.db4ounit.common.fieldindex.FieldIndexItem item = (com.db4o.db4ounit.common.fieldindex.FieldIndexItem
					)found.Next();
				if (item.foo == foo)
				{
					Stream().Delete(trans, item);
				}
			}
		}

		private void AssertSmaller(int[] expectedFoos, int smallerThan)
		{
			AssertSmaller(Trans(), expectedFoos, smallerThan);
		}

		private void AssertSmaller(com.db4o.Transaction transaction, int[] expectedFoos, 
			int smallerThan)
		{
			com.db4o.query.Query query = CreateItemQuery(transaction);
			query.Descend("foo").Constrain(smallerThan).Smaller();
			AssertExpectedFoos(typeof(com.db4o.db4ounit.common.fieldindex.FieldIndexItem), expectedFoos
				, query);
		}
	}
}
