namespace com.db4o.db4ounit.common.assorted
{
	public class ComparatorSortTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		[System.Serializable]
		public class AscendingIdComparator : com.db4o.query.QueryComparator
		{
			public virtual int Compare(object first, object second)
			{
				return ((com.db4o.db4ounit.common.assorted.ComparatorSortTestCase.Item)first)._id
					 - ((com.db4o.db4ounit.common.assorted.ComparatorSortTestCase.Item)second)._id;
			}
		}

		[System.Serializable]
		public class DescendingIdComparator : com.db4o.query.QueryComparator
		{
			public virtual int Compare(object first, object second)
			{
				return ((com.db4o.db4ounit.common.assorted.ComparatorSortTestCase.Item)second)._id
					 - ((com.db4o.db4ounit.common.assorted.ComparatorSortTestCase.Item)first)._id;
			}
		}

		[System.Serializable]
		public class OddEvenIdComparator : com.db4o.query.QueryComparator
		{
			public virtual int Compare(object first, object second)
			{
				int idA = ((com.db4o.db4ounit.common.assorted.ComparatorSortTestCase.Item)first).
					_id;
				int idB = ((com.db4o.db4ounit.common.assorted.ComparatorSortTestCase.Item)second)
					._id;
				int modA = idA % 2;
				int modB = idB % 2;
				if (modA != modB)
				{
					return modA - modB;
				}
				return idA - idB;
			}
		}

		[System.Serializable]
		public class AscendingNameComparator : com.db4o.query.QueryComparator
		{
			public virtual int Compare(object first, object second)
			{
				return ((com.db4o.db4ounit.common.assorted.ComparatorSortTestCase.Item)first)._name
					.CompareTo(((com.db4o.db4ounit.common.assorted.ComparatorSortTestCase.Item)second
					)._name);
			}
		}

		[System.Serializable]
		public class SmallerThanThreePredicate : com.db4o.query.Predicate
		{
			public virtual bool Match(com.db4o.db4ounit.common.assorted.ComparatorSortTestCase.Item
				 candidate)
			{
				return candidate._id < 3;
			}
		}

		public class Item
		{
			public int _id;

			public string _name;

			public Item() : this(0, null)
			{
			}

			public Item(int id, string name)
			{
				this._id = id;
				this._name = name;
			}
		}

		protected override void Configure(com.db4o.config.Configuration config)
		{
			config.ExceptionsOnNotStorable(true);
		}

		protected override void Store()
		{
			for (int i = 0; i < 4; i++)
			{
				Store(new com.db4o.db4ounit.common.assorted.ComparatorSortTestCase.Item(i, (3 - i
					).ToString()));
			}
		}

		public virtual void TestByIdAscending()
		{
			AssertIdOrder(new com.db4o.db4ounit.common.assorted.ComparatorSortTestCase.AscendingIdComparator
				(), new int[] { 0, 1, 2, 3 });
		}

		public virtual void TestByIdAscendingConstrained()
		{
			com.db4o.query.Query query = NewItemQuery();
			query.Descend("_id").Constrain(3).Smaller();
			AssertIdOrder(query, new com.db4o.db4ounit.common.assorted.ComparatorSortTestCase.AscendingIdComparator
				(), new int[] { 0, 1, 2 });
		}

		public virtual void TestByIdAscendingNQ()
		{
			com.db4o.ObjectSet result = Db().Query(new com.db4o.db4ounit.common.assorted.ComparatorSortTestCase.SmallerThanThreePredicate
				(), new com.db4o.db4ounit.common.assorted.ComparatorSortTestCase.AscendingIdComparator
				());
			AssertIdOrder(result, new int[] { 0, 1, 2 });
		}

		public virtual void TestByIdDescending()
		{
			AssertIdOrder(new com.db4o.db4ounit.common.assorted.ComparatorSortTestCase.DescendingIdComparator
				(), new int[] { 3, 2, 1, 0 });
		}

		public virtual void TestByIdDescendingConstrained()
		{
			com.db4o.query.Query query = NewItemQuery();
			query.Descend("_id").Constrain(3).Smaller();
			AssertIdOrder(query, new com.db4o.db4ounit.common.assorted.ComparatorSortTestCase.DescendingIdComparator
				(), new int[] { 2, 1, 0 });
		}

		public virtual void TestByIdDescendingNQ()
		{
			com.db4o.ObjectSet result = Db().Query(new com.db4o.db4ounit.common.assorted.ComparatorSortTestCase.SmallerThanThreePredicate
				(), new com.db4o.db4ounit.common.assorted.ComparatorSortTestCase.DescendingIdComparator
				());
			AssertIdOrder(result, new int[] { 2, 1, 0 });
		}

		public virtual void TestByIdOddEven()
		{
			AssertIdOrder(new com.db4o.db4ounit.common.assorted.ComparatorSortTestCase.OddEvenIdComparator
				(), new int[] { 0, 2, 1, 3 });
		}

		public virtual void TestByIdOddEvenConstrained()
		{
			com.db4o.query.Query query = NewItemQuery();
			query.Descend("_id").Constrain(3).Smaller();
			AssertIdOrder(query, new com.db4o.db4ounit.common.assorted.ComparatorSortTestCase.OddEvenIdComparator
				(), new int[] { 0, 2, 1 });
		}

		public virtual void TestByIdOddEvenNQ()
		{
			com.db4o.ObjectSet result = Db().Query(new com.db4o.db4ounit.common.assorted.ComparatorSortTestCase.SmallerThanThreePredicate
				(), new com.db4o.db4ounit.common.assorted.ComparatorSortTestCase.OddEvenIdComparator
				());
			AssertIdOrder(result, new int[] { 0, 2, 1 });
		}

		public virtual void TestByNameAscending()
		{
			AssertIdOrder(new com.db4o.db4ounit.common.assorted.ComparatorSortTestCase.AscendingNameComparator
				(), new int[] { 3, 2, 1, 0 });
		}

		public virtual void TestByNameAscendingConstrained()
		{
			com.db4o.query.Query query = NewItemQuery();
			query.Descend("_id").Constrain(3).Smaller();
			AssertIdOrder(query, new com.db4o.db4ounit.common.assorted.ComparatorSortTestCase.AscendingNameComparator
				(), new int[] { 2, 1, 0 });
		}

		public virtual void TestByNameAscendingNQ()
		{
			com.db4o.ObjectSet result = Db().Query(new com.db4o.db4ounit.common.assorted.ComparatorSortTestCase.SmallerThanThreePredicate
				(), new com.db4o.db4ounit.common.assorted.ComparatorSortTestCase.AscendingNameComparator
				());
			AssertIdOrder(result, new int[] { 2, 1, 0 });
		}

		private void AssertIdOrder(com.db4o.query.QueryComparator comparator, int[] ids)
		{
			com.db4o.query.Query query = NewItemQuery();
			AssertIdOrder(query, comparator, ids);
		}

		private com.db4o.query.Query NewItemQuery()
		{
			return NewQuery(typeof(com.db4o.db4ounit.common.assorted.ComparatorSortTestCase.Item)
				);
		}

		private void AssertIdOrder(com.db4o.query.Query query, com.db4o.query.QueryComparator
			 comparator, int[] ids)
		{
			query.SortBy(comparator);
			com.db4o.ObjectSet result = query.Execute();
			AssertIdOrder(result, ids);
		}

		private void AssertIdOrder(com.db4o.ObjectSet result, int[] ids)
		{
			Db4oUnit.Assert.AreEqual(ids.Length, result.Size());
			for (int idx = 0; idx < ids.Length; idx++)
			{
				Db4oUnit.Assert.AreEqual(ids[idx], ((com.db4o.db4ounit.common.assorted.ComparatorSortTestCase.Item
					)result.Next())._id);
			}
		}
	}
}
