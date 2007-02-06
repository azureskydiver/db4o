namespace com.db4o.db4ounit.common.soda.arrays.@object
{
	public class STArrIntegerOTestCase : com.db4o.db4ounit.common.soda.util.SodaBaseTestCase
	{
		public object intArr;

		public STArrIntegerOTestCase()
		{
		}

		public STArrIntegerOTestCase(object[] arr)
		{
			intArr = arr;
		}

		public override object[] CreateData()
		{
			return new object[] { new com.db4o.db4ounit.common.soda.arrays.@object.STArrIntegerOTestCase
				(), new com.db4o.db4ounit.common.soda.arrays.@object.STArrIntegerOTestCase(new object
				[0]), new com.db4o.db4ounit.common.soda.arrays.@object.STArrIntegerOTestCase(new 
				object[] { 0, 0 }), new com.db4o.db4ounit.common.soda.arrays.@object.STArrIntegerOTestCase
				(new object[] { 1, 17, int.MaxValue - 1 }), new com.db4o.db4ounit.common.soda.arrays.@object.STArrIntegerOTestCase
				(new object[] { 3, 17, 25, int.MaxValue - 2 }) };
		}

		public virtual void TestDefaultContainsOne()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.arrays.@object.STArrIntegerOTestCase
				(new object[] { 17 }));
			Expect(q, new int[] { 3, 4 });
		}

		public virtual void TestDefaultContainsTwo()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.arrays.@object.STArrIntegerOTestCase
				(new object[] { 17, 25 }));
			Expect(q, new int[] { 4 });
		}

		public virtual void TestDescendOne()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.arrays.@object.STArrIntegerOTestCase)
				);
			q.Descend("intArr").Constrain(17);
			Expect(q, new int[] { 3, 4 });
		}

		public virtual void TestDescendTwo()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.arrays.@object.STArrIntegerOTestCase)
				);
			com.db4o.query.Query qElements = q.Descend("intArr");
			qElements.Constrain(17);
			qElements.Constrain(25);
			Expect(q, new int[] { 4 });
		}

		public virtual void TestDescendSmaller()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.arrays.@object.STArrIntegerOTestCase)
				);
			com.db4o.query.Query qElements = q.Descend("intArr");
			qElements.Constrain(3).Smaller();
			Expect(q, new int[] { 2, 3 });
		}
	}
}
