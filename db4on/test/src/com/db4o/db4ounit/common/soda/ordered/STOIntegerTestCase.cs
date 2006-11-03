namespace com.db4o.db4ounit.common.soda.ordered
{
	public class STOIntegerTestCase : com.db4o.db4ounit.common.soda.util.SodaBaseTestCase
	{
		public int i_int;

		public STOIntegerTestCase()
		{
		}

		private STOIntegerTestCase(int a_int)
		{
			i_int = a_int;
		}

		public override string ToString()
		{
			return "STInteger: " + i_int;
		}

		public override object[] CreateData()
		{
			return new object[] { new com.db4o.db4ounit.common.soda.ordered.STOIntegerTestCase
				(1001), new com.db4o.db4ounit.common.soda.ordered.STOIntegerTestCase(99), new com.db4o.db4ounit.common.soda.ordered.STOIntegerTestCase
				(1), new com.db4o.db4ounit.common.soda.ordered.STOIntegerTestCase(909), new com.db4o.db4ounit.common.soda.ordered.STOIntegerTestCase
				(1001), new com.db4o.db4ounit.common.soda.ordered.STOIntegerTestCase(0), new com.db4o.db4ounit.common.soda.ordered.STOIntegerTestCase
				(1010) };
		}

		public virtual void TestAscending()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.ordered.STOIntegerTestCase));
			q.Descend("i_int").OrderAscending();
			ExpectOrdered(q, new int[] { 5, 2, 1, 3, 0, 4, 6 });
		}

		public virtual void TestDescending()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.ordered.STOIntegerTestCase));
			q.Descend("i_int").OrderDescending();
			ExpectOrdered(q, new int[] { 6, 4, 0, 3, 1, 2, 5 });
		}

		public virtual void TestAscendingGreater()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.ordered.STOIntegerTestCase));
			com.db4o.query.Query qInt = q.Descend("i_int");
			qInt.Constrain(100).Greater();
			qInt.OrderAscending();
			ExpectOrdered(q, new int[] { 3, 0, 4, 6 });
		}
	}
}
