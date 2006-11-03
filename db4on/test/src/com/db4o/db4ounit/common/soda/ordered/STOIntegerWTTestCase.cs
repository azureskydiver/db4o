namespace com.db4o.db4ounit.common.soda.ordered
{
	public class STOIntegerWTTestCase : com.db4o.db4ounit.common.soda.util.SodaBaseTestCase
	{
		public int i_int;

		public STOIntegerWTTestCase()
		{
		}

		private STOIntegerWTTestCase(int a_int)
		{
			i_int = a_int;
		}

		public override object[] CreateData()
		{
			return new object[] { new com.db4o.db4ounit.common.soda.ordered.STOIntegerWTTestCase
				(1001), new com.db4o.db4ounit.common.soda.ordered.STOIntegerWTTestCase(99), new 
				com.db4o.db4ounit.common.soda.ordered.STOIntegerWTTestCase(1), new com.db4o.db4ounit.common.soda.ordered.STOIntegerWTTestCase
				(909), new com.db4o.db4ounit.common.soda.ordered.STOIntegerWTTestCase(1001), new 
				com.db4o.db4ounit.common.soda.ordered.STOIntegerWTTestCase(0), new com.db4o.db4ounit.common.soda.ordered.STOIntegerWTTestCase
				(1010), new com.db4o.db4ounit.common.soda.ordered.STOIntegerWTTestCase() };
		}

		public virtual void TestDescending()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.ordered.STOIntegerWTTestCase));
			q.Descend("i_int").OrderDescending();
			ExpectOrdered(q, new int[] { 6, 4, 0, 3, 1, 2, 5, 7 });
		}

		public virtual void TestAscendingGreater()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.ordered.STOIntegerWTTestCase));
			com.db4o.query.Query qInt = q.Descend("i_int");
			qInt.Constrain(100).Greater();
			qInt.OrderAscending();
			ExpectOrdered(q, new int[] { 3, 0, 4, 6 });
		}
	}
}
