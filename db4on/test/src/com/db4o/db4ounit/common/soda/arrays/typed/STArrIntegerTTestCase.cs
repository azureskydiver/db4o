namespace com.db4o.db4ounit.common.soda.arrays.typed
{
	public class STArrIntegerTTestCase : com.db4o.db4ounit.common.soda.util.SodaBaseTestCase
	{
		public int[] intArr;

		public STArrIntegerTTestCase()
		{
		}

		public STArrIntegerTTestCase(int[] arr)
		{
			intArr = arr;
		}

		public override object[] CreateData()
		{
			return new object[] { new com.db4o.db4ounit.common.soda.arrays.typed.STArrIntegerTTestCase
				(), new com.db4o.db4ounit.common.soda.arrays.typed.STArrIntegerTTestCase(new int
				[0]), new com.db4o.db4ounit.common.soda.arrays.typed.STArrIntegerTTestCase(new int
				[] { 0, 0 }), new com.db4o.db4ounit.common.soda.arrays.typed.STArrIntegerTTestCase
				(new int[] { 1, 17, int.MaxValue - 1 }), new com.db4o.db4ounit.common.soda.arrays.typed.STArrIntegerTTestCase
				(new int[] { 3, 17, 25, int.MaxValue - 2 }) };
		}

		public virtual void TestDefaultContainsOne()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.arrays.typed.STArrIntegerTTestCase(
				new int[] { 17 }));
			Expect(q, new int[] { 3, 4 });
		}

		public virtual void TestDefaultContainsTwo()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.arrays.typed.STArrIntegerTTestCase(
				new int[] { 17, 25 }));
			Expect(q, new int[] { 4 });
		}

		public virtual void TestDescendOne()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.arrays.typed.STArrIntegerTTestCase)
				);
			q.Descend("intArr").Constrain(17);
			Expect(q, new int[] { 3, 4 });
		}

		public virtual void TestDescendTwo()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.arrays.typed.STArrIntegerTTestCase)
				);
			com.db4o.query.Query qElements = q.Descend("intArr");
			qElements.Constrain(17);
			qElements.Constrain(25);
			Expect(q, new int[] { 4 });
		}

		public virtual void TestDescendSmaller()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.arrays.typed.STArrIntegerTTestCase)
				);
			com.db4o.query.Query qElements = q.Descend("intArr");
			qElements.Constrain(3).Smaller();
			Expect(q, new int[] { 2, 3 });
		}

		public virtual void TestDescendNotSmaller()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.arrays.typed.STArrIntegerTTestCase)
				);
			com.db4o.query.Query qElements = q.Descend("intArr");
			qElements.Constrain(3).Smaller();
			Expect(q, new int[] { 2, 3 });
		}
	}
}
