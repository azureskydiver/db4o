namespace com.db4o.db4ounit.common.soda.classes.simple
{
	public class STFloatTestCase : com.db4o.db4ounit.common.soda.util.SodaBaseTestCase
	{
		public float i_float;

		public STFloatTestCase()
		{
		}

		private STFloatTestCase(float a_float)
		{
			i_float = a_float;
		}

		public override object[] CreateData()
		{
			return new object[] { new com.db4o.db4ounit.common.soda.classes.simple.STFloatTestCase
				(float.MinValue), new com.db4o.db4ounit.common.soda.classes.simple.STFloatTestCase
				((float)0.0000123), new com.db4o.db4ounit.common.soda.classes.simple.STFloatTestCase
				((float)1.345), new com.db4o.db4ounit.common.soda.classes.simple.STFloatTestCase
				(float.MaxValue) };
		}

		public virtual void TestEquals()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(_array[0]);
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, _array[0]);
		}

		public virtual void TestGreater()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STFloatTestCase((float
				)0.1));
			q.Descend("i_float").Constraints().Greater();
			Expect(q, new int[] { 2, 3 });
		}

		public virtual void TestSmaller()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STFloatTestCase((float
				)1.5));
			q.Descend("i_float").Constraints().Smaller();
			Expect(q, new int[] { 0, 1, 2 });
		}
	}
}
