namespace com.db4o.db4ounit.common.soda
{
	public class CollectionIndexedJoinTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		private static readonly string COLLECTIONFIELDNAME = "_data";

		private static readonly string IDFIELDNAME = "_id";

		private const int NUMENTRIES = 3;

		public class DataHolder
		{
			public System.Collections.ArrayList _data;

			public DataHolder(int id)
			{
				_data = new System.Collections.ArrayList();
				_data.Add(new com.db4o.db4ounit.common.soda.CollectionIndexedJoinTestCase.Data(id
					));
			}
		}

		public class Data
		{
			public int _id;

			public Data(int id)
			{
				this._id = id;
			}
		}

		protected override void Configure(com.db4o.config.Configuration config)
		{
			config.ObjectClass(typeof(com.db4o.db4ounit.common.soda.CollectionIndexedJoinTestCase.Data)
				).ObjectField(IDFIELDNAME).Indexed(true);
		}

		protected override void Store()
		{
			for (int i = 0; i < NUMENTRIES; i++)
			{
				Store(new com.db4o.db4ounit.common.soda.CollectionIndexedJoinTestCase.DataHolder(
					i));
			}
		}

		public virtual void TestIndexedOrTwo()
		{
			AssertIndexedOr(new int[] { 0, 1, -1 }, 2);
		}

		private void AssertIndexedOr(int[] values, int expectedResultCount)
		{
			com.db4o.db4ounit.common.soda.CollectionIndexedJoinTestCase.TestConfig config = new 
				com.db4o.db4ounit.common.soda.CollectionIndexedJoinTestCase.TestConfig(values.Length
				);
			while (config.MoveNext())
			{
				AssertIndexedOr(values, expectedResultCount, config.RootIndex(), config.ConnectLeft
					());
			}
		}

		public virtual void TestIndexedOrAll()
		{
			AssertIndexedOr(new int[] { 0, 1, 2 }, 3);
		}

		public virtual void TestTwoJoinLegs()
		{
			com.db4o.query.Query query = NewQuery(typeof(com.db4o.db4ounit.common.soda.CollectionIndexedJoinTestCase.DataHolder)
				).Descend(COLLECTIONFIELDNAME);
			com.db4o.query.Constraint left = query.Descend(IDFIELDNAME).Constrain(0);
			left.Or(query.Descend(IDFIELDNAME).Constrain(1));
			com.db4o.query.Constraint right = query.Descend(IDFIELDNAME).Constrain(2);
			right.Or(query.Descend(IDFIELDNAME).Constrain(-1));
			left.Or(right);
			com.db4o.ObjectSet result = query.Execute();
			Db4oUnit.Assert.AreEqual(3, result.Size());
		}

		public virtual void AssertIndexedOr(int[] values, int expectedResultCount, int rootIdx
			, bool connectLeft)
		{
			com.db4o.query.Query query = NewQuery(typeof(com.db4o.db4ounit.common.soda.CollectionIndexedJoinTestCase.DataHolder)
				).Descend(COLLECTIONFIELDNAME);
			com.db4o.query.Constraint constraint = query.Descend(IDFIELDNAME).Constrain(values
				[rootIdx]);
			for (int idx = 0; idx < values.Length; idx++)
			{
				if (idx != rootIdx)
				{
					com.db4o.query.Constraint curConstraint = query.Descend(IDFIELDNAME).Constrain(values
						[idx]);
					if (connectLeft)
					{
						constraint.Or(curConstraint);
					}
					else
					{
						curConstraint.Or(constraint);
					}
				}
			}
			com.db4o.ObjectSet result = query.Execute();
			Db4oUnit.Assert.AreEqual(expectedResultCount, result.Size());
		}

		private class TestConfig : com.db4o.db4ounit.util.PermutingTestConfig
		{
			public TestConfig(int numValues) : base(new object[][] { new object[] { 0, numValues
				 - 1 }, new object[] { false, true } })
			{
			}

			public virtual int RootIndex()
			{
				return ((int)Current(0));
			}

			public virtual bool ConnectLeft()
			{
				return ((bool)Current(1));
			}
		}
	}
}
