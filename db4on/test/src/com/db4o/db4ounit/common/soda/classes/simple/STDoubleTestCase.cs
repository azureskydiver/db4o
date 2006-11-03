namespace com.db4o.db4ounit.common.soda.classes.simple
{
	public class STDoubleTestCase : com.db4o.db4ounit.common.soda.util.SodaBaseTestCase
	{
		public double i_double;

		public STDoubleTestCase()
		{
		}

		private STDoubleTestCase(double a_double)
		{
			i_double = a_double;
		}

		public override object[] CreateData()
		{
			return new object[] { new com.db4o.db4ounit.common.soda.classes.simple.STDoubleTestCase
				(0), new com.db4o.db4ounit.common.soda.classes.simple.STDoubleTestCase(0), new com.db4o.db4ounit.common.soda.classes.simple.STDoubleTestCase
				(1.01), new com.db4o.db4ounit.common.soda.classes.simple.STDoubleTestCase(99.99)
				, new com.db4o.db4ounit.common.soda.classes.simple.STDoubleTestCase(909.00) };
		}

		public virtual void TestEquals()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STDoubleTestCase(0));
			q.Descend("i_double").Constrain(System.Convert.ToDouble(0));
			Expect(q, new int[] { 0, 1 });
		}

		public virtual void TestGreater()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STDoubleTestCase(1));
			q.Descend("i_double").Constraints().Greater();
			Expect(q, new int[] { 2, 3, 4 });
		}

		public virtual void TestSmaller()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STDoubleTestCase(1));
			q.Descend("i_double").Constraints().Smaller();
			Expect(q, new int[] { 0, 1 });
		}

		public virtual void TestGreaterOrEqual()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(_array[2]);
			q.Descend("i_double").Constraints().Greater().Equal();
			Expect(q, new int[] { 2, 3, 4 });
		}

		public virtual void TestGreaterAndNot()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STDoubleTestCase());
			com.db4o.query.Query val = q.Descend("i_double");
			val.Constrain(System.Convert.ToDouble(0)).Greater();
			val.Constrain(99.99).Not();
			Expect(q, new int[] { 2, 4 });
		}
	}
}
