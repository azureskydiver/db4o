namespace com.db4o.db4ounit.common.assorted
{
	public class IndexedQueriesTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public static void Main(string[] arguments)
		{
			new com.db4o.db4ounit.common.assorted.IndexedQueriesTestCase().RunSolo();
		}

		public class IndexedQueriesItem
		{
			public string _name;

			public int _int;

			public int _integer;

			public IndexedQueriesItem()
			{
			}

			public IndexedQueriesItem(string name)
			{
				_name = name;
			}

			public IndexedQueriesItem(int int_)
			{
				_int = int_;
				_integer = int_;
			}
		}

		protected override void Configure()
		{
			IndexField("_name");
			IndexField("_int");
			IndexField("_integer");
		}

		private void IndexField(string fieldName)
		{
			IndexField(typeof(com.db4o.db4ounit.common.assorted.IndexedQueriesTestCase.IndexedQueriesItem
				), fieldName);
		}

		protected override void Store()
		{
			string[] strings = new string[] { "a", "c", "b", "f", "e" };
			for (int i = 0; i < strings.Length; i++)
			{
				Db().Set(new com.db4o.db4ounit.common.assorted.IndexedQueriesTestCase.IndexedQueriesItem
					(strings[i]));
			}
			int[] ints = new int[] { 1, 5, 7, 3, 2, 3 };
			for (int i = 0; i < ints.Length; i++)
			{
				Db().Set(new com.db4o.db4ounit.common.assorted.IndexedQueriesTestCase.IndexedQueriesItem
					(ints[i]));
			}
		}

		public virtual void TestIntQuery()
		{
			AssertInts(5);
		}

		public virtual void TestStringQuery()
		{
			AssertNullNameCount(6);
			Db().Set(new com.db4o.db4ounit.common.assorted.IndexedQueriesTestCase.IndexedQueriesItem
				("d"));
			AssertQuery(1, "b");
			UpdateB();
			Db().Set(new com.db4o.db4ounit.common.assorted.IndexedQueriesTestCase.IndexedQueriesItem
				("z"));
			Db().Set(new com.db4o.db4ounit.common.assorted.IndexedQueriesTestCase.IndexedQueriesItem
				("y"));
			Reopen();
			AssertQuery(1, "b");
			AssertInts(8);
		}

		private void AssertIntegers()
		{
			com.db4o.query.Query q = NewQuery();
			q.Descend("_integer").Constrain(4).Greater().Equal();
			AssertIntsFound(new int[] { 5, 7 }, q);
			q = NewQuery();
			q.Descend("_integer").Constrain(4).Smaller();
			AssertIntsFound(new int[] { 1, 2, 3, 3 }, q);
		}

		private void AssertInts(int expectedZeroSize)
		{
			com.db4o.query.Query q = NewQuery();
			q.Descend("_int").Constrain(0);
			int zeroSize = q.Execute().Size();
			Db4oUnit.Assert.AreEqual(expectedZeroSize, zeroSize);
			q = NewQuery();
			q.Descend("_int").Constrain(4).Greater().Equal();
			AssertIntsFound(new int[] { 5, 7 }, q);
			q = NewQuery();
			q.Descend("_int").Constrain(4).Greater();
			AssertIntsFound(new int[] { 5, 7 }, q);
			q = NewQuery();
			q.Descend("_int").Constrain(3).Greater();
			AssertIntsFound(new int[] { 5, 7 }, q);
			q = NewQuery();
			q.Descend("_int").Constrain(3).Greater().Equal();
			AssertIntsFound(new int[] { 3, 3, 5, 7 }, q);
			q = NewQuery();
			q.Descend("_int").Constrain(2).Greater().Equal();
			AssertIntsFound(new int[] { 2, 3, 3, 5, 7 }, q);
			q = NewQuery();
			q.Descend("_int").Constrain(2).Greater();
			AssertIntsFound(new int[] { 3, 3, 5, 7 }, q);
			q = NewQuery();
			q.Descend("_int").Constrain(1).Greater().Equal();
			AssertIntsFound(new int[] { 1, 2, 3, 3, 5, 7 }, q);
			q = NewQuery();
			q.Descend("_int").Constrain(1).Greater();
			AssertIntsFound(new int[] { 2, 3, 3, 5, 7 }, q);
			q = NewQuery();
			q.Descend("_int").Constrain(4).Smaller();
			AssertIntsFound(new int[] { 1, 2, 3, 3 }, expectedZeroSize, q);
			q = NewQuery();
			q.Descend("_int").Constrain(4).Smaller().Equal();
			AssertIntsFound(new int[] { 1, 2, 3, 3 }, expectedZeroSize, q);
			q = NewQuery();
			q.Descend("_int").Constrain(3).Smaller();
			AssertIntsFound(new int[] { 1, 2 }, expectedZeroSize, q);
			q = NewQuery();
			q.Descend("_int").Constrain(3).Smaller().Equal();
			AssertIntsFound(new int[] { 1, 2, 3, 3 }, expectedZeroSize, q);
			q = NewQuery();
			q.Descend("_int").Constrain(2).Smaller().Equal();
			AssertIntsFound(new int[] { 1, 2 }, expectedZeroSize, q);
			q = NewQuery();
			q.Descend("_int").Constrain(2).Smaller();
			AssertIntsFound(new int[] { 1 }, expectedZeroSize, q);
			q = NewQuery();
			q.Descend("_int").Constrain(1).Smaller().Equal();
			AssertIntsFound(new int[] { 1 }, expectedZeroSize, q);
			q = NewQuery();
			q.Descend("_int").Constrain(1).Smaller();
			AssertIntsFound(new int[] {  }, expectedZeroSize, q);
		}

		private void AssertIntsFound(int[] ints, int zeroSize, com.db4o.query.Query q)
		{
			com.db4o.ObjectSet res = q.Execute();
			Db4oUnit.Assert.AreEqual((ints.Length + zeroSize), res.Size());
			while (res.HasNext())
			{
				com.db4o.db4ounit.common.assorted.IndexedQueriesTestCase.IndexedQueriesItem ci = 
					(com.db4o.db4ounit.common.assorted.IndexedQueriesTestCase.IndexedQueriesItem)res
					.Next();
				for (int i = 0; i < ints.Length; i++)
				{
					if (ints[i] == ci._int)
					{
						ints[i] = 0;
						break;
					}
				}
			}
			for (int i = 0; i < ints.Length; i++)
			{
				Db4oUnit.Assert.AreEqual(0, ints[i]);
			}
		}

		private void AssertIntsFound(int[] ints, com.db4o.query.Query q)
		{
			AssertIntsFound(ints, 0, q);
		}

		private void AssertQuery(int count, string @string)
		{
			com.db4o.ObjectSet res = QueryForName(@string);
			Db4oUnit.Assert.AreEqual(count, res.Size());
			com.db4o.db4ounit.common.assorted.IndexedQueriesTestCase.IndexedQueriesItem item = 
				(com.db4o.db4ounit.common.assorted.IndexedQueriesTestCase.IndexedQueriesItem)res
				.Next();
			Db4oUnit.Assert.AreEqual("b", item._name);
		}

		private void AssertNullNameCount(int count)
		{
			com.db4o.ObjectSet res = QueryForName(null);
			Db4oUnit.Assert.AreEqual(count, res.Size());
			while (res.HasNext())
			{
				com.db4o.db4ounit.common.assorted.IndexedQueriesTestCase.IndexedQueriesItem ci = 
					(com.db4o.db4ounit.common.assorted.IndexedQueriesTestCase.IndexedQueriesItem)res
					.Next();
				Db4oUnit.Assert.IsNull(ci._name);
			}
		}

		private void UpdateB()
		{
			com.db4o.ObjectSet res = QueryForName("b");
			com.db4o.db4ounit.common.assorted.IndexedQueriesTestCase.IndexedQueriesItem ci = 
				(com.db4o.db4ounit.common.assorted.IndexedQueriesTestCase.IndexedQueriesItem)res
				.Next();
			ci._name = "j";
			Db().Set(ci);
			res = QueryForName("b");
			Db4oUnit.Assert.AreEqual(0, res.Size());
			res = QueryForName("j");
			Db4oUnit.Assert.AreEqual(1, res.Size());
			ci._name = "b";
			Db().Set(ci);
			AssertQuery(1, "b");
		}

		private com.db4o.ObjectSet QueryForName(string n)
		{
			com.db4o.query.Query q = NewQuery();
			q.Descend("_name").Constrain(n);
			return q.Execute();
		}

		protected override com.db4o.query.Query NewQuery()
		{
			com.db4o.query.Query q = base.NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.assorted.IndexedQueriesTestCase.IndexedQueriesItem
				));
			return q;
		}
	}
}
