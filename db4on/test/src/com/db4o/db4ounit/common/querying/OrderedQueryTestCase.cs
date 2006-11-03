namespace com.db4o.db4ounit.common.querying
{
	/// <exclude></exclude>
	public class OrderedQueryTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.querying.OrderedQueryTestCase().RunSolo();
		}

		public sealed class Item
		{
			public int value;

			public Item(int value)
			{
				this.value = value;
			}
		}

		protected override void Store()
		{
			Db().Set(new com.db4o.db4ounit.common.querying.OrderedQueryTestCase.Item(1));
			Db().Set(new com.db4o.db4ounit.common.querying.OrderedQueryTestCase.Item(3));
			Db().Set(new com.db4o.db4ounit.common.querying.OrderedQueryTestCase.Item(2));
		}

		public virtual void TestOrderAscending()
		{
			com.db4o.query.Query query = NewQuery(typeof(com.db4o.db4ounit.common.querying.OrderedQueryTestCase.Item)
				);
			query.Descend("value").OrderAscending();
			AssertQuery(new int[] { 1, 2, 3 }, query.Execute());
		}

		public virtual void TestOrderDescending()
		{
			com.db4o.query.Query query = NewQuery(typeof(com.db4o.db4ounit.common.querying.OrderedQueryTestCase.Item)
				);
			query.Descend("value").OrderDescending();
			AssertQuery(new int[] { 3, 2, 1 }, query.Execute());
		}

		private void AssertQuery(int[] expected, com.db4o.ObjectSet actual)
		{
			for (int i = 0; i < expected.Length; i++)
			{
				Db4oUnit.Assert.IsTrue(actual.HasNext());
				Db4oUnit.Assert.AreEqual(expected[i], ((com.db4o.db4ounit.common.querying.OrderedQueryTestCase.Item
					)actual.Next()).value);
			}
			Db4oUnit.Assert.IsFalse(actual.HasNext());
		}
	}
}
