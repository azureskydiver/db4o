namespace com.db4o.db4ounit.common.soda.classes.simple
{
	public class STCharTestCase : com.db4o.db4ounit.common.soda.util.SodaBaseTestCase
	{
		internal static readonly string DESCENDANT = "i_char";

		public char i_char;

		public STCharTestCase()
		{
		}

		private STCharTestCase(char a_char)
		{
			i_char = a_char;
		}

		public override object[] CreateData()
		{
			return new object[] { new com.db4o.db4ounit.common.soda.classes.simple.STCharTestCase
				((char)0), new com.db4o.db4ounit.common.soda.classes.simple.STCharTestCase((char
				)1), new com.db4o.db4ounit.common.soda.classes.simple.STCharTestCase((char)99), 
				new com.db4o.db4ounit.common.soda.classes.simple.STCharTestCase((char)909) };
		}

		public virtual void TestEquals()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STCharTestCase((char
				)0));
			q.Descend(DESCENDANT).Constrain((char)0);
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, _array[0]);
		}

		public virtual void TestNotEquals()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(_array[0]);
			q.Descend(DESCENDANT).Constrain((char)0).Not();
			Expect(q, new int[] { 1, 2, 3 });
		}

		public virtual void TestGreater()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STCharTestCase((char
				)9));
			q.Descend(DESCENDANT).Constraints().Greater();
			Expect(q, new int[] { 2, 3 });
		}

		public virtual void TestSmaller()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STCharTestCase((char
				)1));
			q.Descend(DESCENDANT).Constraints().Smaller();
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, _array[0]);
		}

		public virtual void TestIdentity()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STCharTestCase((char
				)1));
			com.db4o.ObjectSet set = q.Execute();
			com.db4o.db4ounit.common.soda.classes.simple.STCharTestCase identityConstraint = 
				(com.db4o.db4ounit.common.soda.classes.simple.STCharTestCase)set.Next();
			identityConstraint.i_char = (char)9999;
			q = NewQuery();
			q.Constrain(identityConstraint).Identity();
			identityConstraint.i_char = (char)1;
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, _array[1]);
		}

		public virtual void TestNotIdentity()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STCharTestCase((char
				)1));
			com.db4o.ObjectSet set = q.Execute();
			com.db4o.db4ounit.common.soda.classes.simple.STCharTestCase identityConstraint = 
				(com.db4o.db4ounit.common.soda.classes.simple.STCharTestCase)set.Next();
			identityConstraint.i_char = (char)9080;
			q = NewQuery();
			q.Constrain(identityConstraint).Identity().Not();
			identityConstraint.i_char = (char)1;
			Expect(q, new int[] { 0, 2, 3 });
		}

		public virtual void TestConstraints()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STCharTestCase((char
				)1));
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STCharTestCase((char
				)0));
			com.db4o.query.Constraints cs = q.Constraints();
			com.db4o.query.Constraint[] csa = cs.ToArray();
			if (csa.Length != 2)
			{
				Db4oUnit.Assert.Fail("Constraints not returned");
			}
		}
	}
}
