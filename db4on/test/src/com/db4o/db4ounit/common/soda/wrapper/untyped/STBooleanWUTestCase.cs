namespace com.db4o.db4ounit.common.soda.wrapper.untyped
{
	public class STBooleanWUTestCase : com.db4o.db4ounit.common.soda.util.SodaBaseTestCase
	{
		internal static readonly string DESCENDANT = "i_boolean";

		public object i_boolean;

		public STBooleanWUTestCase()
		{
		}

		private STBooleanWUTestCase(bool a_boolean)
		{
			i_boolean = a_boolean;
		}

		public override object[] CreateData()
		{
			return new object[] { new com.db4o.db4ounit.common.soda.wrapper.untyped.STBooleanWUTestCase
				(false), new com.db4o.db4ounit.common.soda.wrapper.untyped.STBooleanWUTestCase(true
				), new com.db4o.db4ounit.common.soda.wrapper.untyped.STBooleanWUTestCase(false), 
				new com.db4o.db4ounit.common.soda.wrapper.untyped.STBooleanWUTestCase(false), new 
				com.db4o.db4ounit.common.soda.wrapper.untyped.STBooleanWUTestCase() };
		}

		public virtual void TestEqualsTrue()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STBooleanWUTestCase
				(true));
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, new com.db4o.db4ounit.common.soda.wrapper.untyped.STBooleanWUTestCase
				(true));
		}

		public virtual void TestEqualsFalse()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STBooleanWUTestCase
				(false));
			q.Descend(DESCENDANT).Constrain(false);
			Expect(q, new int[] { 0, 2, 3 });
		}

		public virtual void TestNull()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STBooleanWUTestCase
				());
			q.Descend(DESCENDANT).Constrain(null);
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, new com.db4o.db4ounit.common.soda.wrapper.untyped.STBooleanWUTestCase
				());
		}

		public virtual void TestNullOrTrue()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STBooleanWUTestCase
				());
			com.db4o.query.Query qd = q.Descend(DESCENDANT);
			qd.Constrain(null).Or(qd.Constrain(true));
			Expect(q, new int[] { 1, 4 });
		}

		public virtual void TestNotNullAndFalse()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STBooleanWUTestCase
				());
			com.db4o.query.Query qd = q.Descend(DESCENDANT);
			qd.Constrain(null).Not().And(qd.Constrain(false));
			Expect(q, new int[] { 0, 2, 3 });
		}
	}
}
