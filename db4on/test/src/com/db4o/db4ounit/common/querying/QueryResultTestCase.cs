namespace com.db4o.db4ounit.common.querying
{
	public abstract class QueryResultTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
		, Db4oUnit.Extensions.Fixtures.OptOutCS, Db4oUnit.Extensions.Fixtures.OptOutDefragSolo
	{
		private static readonly int[] VALUES = new int[] { 1, 5, 6, 7, 9 };

		private readonly int[] itemIds = new int[VALUES.Length];

		private int idForGetAll;

		protected override void Configure(com.db4o.config.Configuration config)
		{
			IndexField(config, typeof(com.db4o.db4ounit.common.querying.QueryResultTestCase.Item)
				, "foo");
		}

		public virtual void TestClassQuery()
		{
			AssertIDs(ClassOnlyQuery(), itemIds);
		}

		public virtual void TestGetAll()
		{
			com.db4o.inside.query.QueryResult queryResult = NewQueryResult();
			queryResult.LoadFromClassIndexes(Stream().ClassCollection().Iterator());
			int[] ids = com.db4o.db4ounit.common.foundation.IntArrays4.Concat(itemIds, new int
				[] { idForGetAll });
			AssertIDs(queryResult, ids, true);
		}

		public virtual void TestIndexedFieldQuery()
		{
			com.db4o.query.Query query = NewItemQuery();
			query.Descend("foo").Constrain(6).Smaller();
			com.db4o.inside.query.QueryResult queryResult = ExecuteQuery(query);
			AssertIDs(queryResult, new int[] { itemIds[0], itemIds[1] });
		}

		public virtual void TestNonIndexedFieldQuery()
		{
			com.db4o.query.Query query = NewItemQuery();
			query.Descend("bar").Constrain(6).Smaller();
			com.db4o.inside.query.QueryResult queryResult = ExecuteQuery(query);
			AssertIDs(queryResult, new int[] { itemIds[0], itemIds[1] });
		}

		private com.db4o.inside.query.QueryResult ClassOnlyQuery()
		{
			com.db4o.inside.query.QueryResult queryResult = NewQueryResult();
			queryResult.LoadFromClassIndex(YapClass());
			return queryResult;
		}

		private com.db4o.YapClass YapClass()
		{
			return Stream().GetYapClass(com.db4o.db4ounit.Db4oUnitPlatform.GetReflectClass(Reflector
				(), typeof(com.db4o.db4ounit.common.querying.QueryResultTestCase.Item)), false);
		}

		private com.db4o.inside.query.QueryResult ExecuteQuery(com.db4o.query.Query query
			)
		{
			com.db4o.inside.query.QueryResult queryResult = NewQueryResult();
			queryResult.LoadFromQuery((com.db4o.QQuery)query);
			return queryResult;
		}

		private void AssertIDs(com.db4o.inside.query.QueryResult queryResult, int[] expectedIDs
			)
		{
			AssertIDs(queryResult, expectedIDs, false);
		}

		private void AssertIDs(com.db4o.inside.query.QueryResult queryResult, int[] expectedIDs
			, bool ignoreUnexpected)
		{
			com.db4o.db4ounit.common.btree.ExpectingVisitor expectingVisitor = new com.db4o.db4ounit.common.btree.ExpectingVisitor
				(com.db4o.db4ounit.common.foundation.IntArrays4.ToObjectArray(expectedIDs), false
				, ignoreUnexpected);
			com.db4o.foundation.IntIterator4 i = queryResult.IterateIDs();
			while (i.MoveNext())
			{
				expectingVisitor.Visit(i.CurrentInt());
			}
			expectingVisitor.AssertExpectations();
		}

		protected virtual com.db4o.query.Query NewItemQuery()
		{
			return NewQuery(typeof(com.db4o.db4ounit.common.querying.QueryResultTestCase.Item)
				);
		}

		protected override void Store()
		{
			StoreItems(VALUES);
			com.db4o.db4ounit.common.querying.QueryResultTestCase.ItemForGetAll ifga = new com.db4o.db4ounit.common.querying.QueryResultTestCase.ItemForGetAll
				();
			Store(ifga);
			idForGetAll = (int)Db().GetID(ifga);
		}

		protected virtual void StoreItems(int[] foos)
		{
			for (int i = 0; i < foos.Length; i++)
			{
				com.db4o.db4ounit.common.querying.QueryResultTestCase.Item item = new com.db4o.db4ounit.common.querying.QueryResultTestCase.Item
					(foos[i]);
				Store(item);
				itemIds[i] = (int)Db().GetID(item);
			}
		}

		public class Item
		{
			public int foo;

			public int bar;

			public Item()
			{
			}

			public Item(int foo_)
			{
				foo = foo_;
				bar = foo;
			}
		}

		public class ItemForGetAll
		{
		}

		protected abstract com.db4o.inside.query.QueryResult NewQueryResult();
	}
}
