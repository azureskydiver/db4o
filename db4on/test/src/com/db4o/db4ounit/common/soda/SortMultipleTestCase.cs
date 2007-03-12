namespace com.db4o.db4ounit.common.soda
{
	public class SortMultipleTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public static void Main(string[] arguments)
		{
			new com.db4o.db4ounit.common.soda.SortMultipleTestCase().RunSolo();
		}

		public class IntHolder
		{
			public int _value;

			public IntHolder(int value)
			{
				this._value = value;
			}

			public override bool Equals(object obj)
			{
				if (this == obj)
				{
					return true;
				}
				if (obj == null || GetType() != obj.GetType())
				{
					return false;
				}
				com.db4o.db4ounit.common.soda.SortMultipleTestCase.IntHolder intHolder = (com.db4o.db4ounit.common.soda.SortMultipleTestCase.IntHolder
					)obj;
				return _value == intHolder._value;
			}

			public override int GetHashCode()
			{
				return _value;
			}

			public override string ToString()
			{
				return _value.ToString();
			}
		}

		public class Data
		{
			public int _first;

			public int _second;

			public com.db4o.db4ounit.common.soda.SortMultipleTestCase.IntHolder _third;

			public Data(int first, int second, int third)
			{
				this._first = first;
				this._second = second;
				this._third = new com.db4o.db4ounit.common.soda.SortMultipleTestCase.IntHolder(third
					);
			}

			public override bool Equals(object obj)
			{
				if (this == obj)
				{
					return true;
				}
				if (obj == null || GetType() != obj.GetType())
				{
					return false;
				}
				com.db4o.db4ounit.common.soda.SortMultipleTestCase.Data data = (com.db4o.db4ounit.common.soda.SortMultipleTestCase.Data
					)obj;
				return _first == data._first && _second == data._second && _third.Equals(data._third
					);
			}

			public override int GetHashCode()
			{
				int hc = _first;
				hc *= 29 + _second;
				hc *= 29 + _third.GetHashCode();
				return hc;
			}

			public override string ToString()
			{
				return _first + "/" + _second + "/" + _third;
			}
		}

		private static readonly com.db4o.db4ounit.common.soda.SortMultipleTestCase.Data[]
			 DATA = new com.db4o.db4ounit.common.soda.SortMultipleTestCase.Data[] { new com.db4o.db4ounit.common.soda.SortMultipleTestCase.Data
			(1, 2, 4), new com.db4o.db4ounit.common.soda.SortMultipleTestCase.Data(1, 4, 3), 
			new com.db4o.db4ounit.common.soda.SortMultipleTestCase.Data(2, 4, 2), new com.db4o.db4ounit.common.soda.SortMultipleTestCase.Data
			(3, 1, 4), new com.db4o.db4ounit.common.soda.SortMultipleTestCase.Data(4, 3, 1), 
			new com.db4o.db4ounit.common.soda.SortMultipleTestCase.Data(4, 1, 3) };

		protected override void Store()
		{
			for (int dataIdx = 0; dataIdx < DATA.Length; dataIdx++)
			{
				Store(DATA[dataIdx]);
			}
		}

		public virtual void TestSortFirstThenSecond()
		{
			com.db4o.query.Query query = NewQuery(typeof(com.db4o.db4ounit.common.soda.SortMultipleTestCase.Data)
				);
			query.Descend("_first").OrderAscending();
			query.Descend("_second").OrderAscending();
			AssertSortOrder(query, new int[] { 0, 1, 2, 3, 5, 4 });
		}

		public virtual void TestSortSecondThenFirst()
		{
			com.db4o.query.Query query = NewQuery(typeof(com.db4o.db4ounit.common.soda.SortMultipleTestCase.Data)
				);
			query.Descend("_second").OrderAscending();
			query.Descend("_first").OrderAscending();
			AssertSortOrder(query, new int[] { 3, 5, 0, 4, 1, 2 });
		}

		public virtual void TestSortThirdThenFirst()
		{
			com.db4o.query.Query query = NewQuery(typeof(com.db4o.db4ounit.common.soda.SortMultipleTestCase.Data)
				);
			query.Descend("_third").Descend("_value").OrderAscending();
			query.Descend("_first").OrderAscending();
			AssertSortOrder(query, new int[] { 4, 2, 1, 5, 0, 3 });
		}

		public virtual void TestSortThirdThenSecond()
		{
			com.db4o.query.Query query = NewQuery(typeof(com.db4o.db4ounit.common.soda.SortMultipleTestCase.Data)
				);
			query.Descend("_third").Descend("_value").OrderAscending();
			query.Descend("_second").OrderAscending();
			AssertSortOrder(query, new int[] { 4, 2, 5, 1, 3, 0 });
		}

		public virtual void TestSortSecondThenThird()
		{
			com.db4o.query.Query query = NewQuery(typeof(com.db4o.db4ounit.common.soda.SortMultipleTestCase.Data)
				);
			query.Descend("_second").OrderAscending();
			query.Descend("_third").Descend("_value").OrderAscending();
			AssertSortOrder(query, new int[] { 5, 3, 0, 4, 2, 1 });
		}

		private void AssertSortOrder(com.db4o.query.Query query, int[] expectedIndexes)
		{
			com.db4o.ObjectSet result = query.Execute();
			Db4oUnit.Assert.AreEqual(expectedIndexes.Length, result.Size());
			for (int i = 0; i < expectedIndexes.Length; i++)
			{
				Db4oUnit.Assert.AreEqual(DATA[expectedIndexes[i]], result.Next());
			}
		}
	}
}
