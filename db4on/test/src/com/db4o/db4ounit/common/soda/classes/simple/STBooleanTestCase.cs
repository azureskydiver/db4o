namespace com.db4o.db4ounit.common.soda.classes.simple
{
	public class STBooleanTestCase : com.db4o.db4ounit.common.soda.util.SodaBaseTestCase
	{
		public bool i_boolean;

		public STBooleanTestCase()
		{
		}

		private STBooleanTestCase(bool a_boolean)
		{
			i_boolean = a_boolean;
		}

		public override object[] CreateData()
		{
			return new object[] { new com.db4o.db4ounit.common.soda.classes.simple.STBooleanTestCase
				(false), new com.db4o.db4ounit.common.soda.classes.simple.STBooleanTestCase(true
				), new com.db4o.db4ounit.common.soda.classes.simple.STBooleanTestCase(false), new 
				com.db4o.db4ounit.common.soda.classes.simple.STBooleanTestCase(false) };
		}

		public virtual void TestEqualsTrue()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STBooleanTestCase(true
				));
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, new com.db4o.db4ounit.common.soda.classes.simple.STBooleanTestCase
				(true));
		}

		public virtual void TestEqualsFalse()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STBooleanTestCase(false
				));
			q.Descend("i_boolean").Constrain(false);
			Expect(q, new int[] { 0, 2, 3 });
		}
	}
}
