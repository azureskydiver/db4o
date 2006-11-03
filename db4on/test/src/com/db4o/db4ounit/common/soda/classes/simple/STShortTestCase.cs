namespace com.db4o.db4ounit.common.soda.classes.simple
{
	public class STShortTestCase : com.db4o.db4ounit.common.soda.util.SodaBaseTestCase
	{
		internal static readonly string DESCENDANT = "i_short";

		public short i_short;

		public STShortTestCase()
		{
		}

		private STShortTestCase(short a_short)
		{
			i_short = a_short;
		}

		public override object[] CreateData()
		{
			return new object[] { new com.db4o.db4ounit.common.soda.classes.simple.STShortTestCase
				((short)0), new com.db4o.db4ounit.common.soda.classes.simple.STShortTestCase((short
				)1), new com.db4o.db4ounit.common.soda.classes.simple.STShortTestCase((short)99)
				, new com.db4o.db4ounit.common.soda.classes.simple.STShortTestCase((short)909) };
		}

		public virtual void TestEquals()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STShortTestCase((short
				)0));
			q.Descend(DESCENDANT).Constrain((short)0);
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, _array[0]);
		}

		public virtual void TestNotEquals()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(_array[0]);
			q.Descend(DESCENDANT).Constrain((short)0).Not();
			Expect(q, new int[] { 1, 2, 3 });
		}

		public virtual void TestGreater()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STShortTestCase((short
				)9));
			q.Descend(DESCENDANT).Constraints().Greater();
			Expect(q, new int[] { 2, 3 });
		}

		public virtual void TestSmaller()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STShortTestCase((short
				)1));
			q.Descend(DESCENDANT).Constraints().Smaller();
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, _array[0]);
		}

		public virtual void TestContains()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STShortTestCase((short
				)9));
			q.Descend(DESCENDANT).Constraints().Contains();
			Expect(q, new int[] { 2, 3 });
		}

		public virtual void TestNotContains()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STShortTestCase((short
				)0));
			q.Descend(DESCENDANT).Constrain((short)0).Contains().Not();
			Expect(q, new int[] { 1, 2 });
		}

		public virtual void TestLike()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STShortTestCase((short
				)90));
			q.Descend(DESCENDANT).Constraints().Like();
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, _array[3]);
			q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STShortTestCase((short
				)10));
			q.Descend(DESCENDANT).Constraints().Like();
			Expect(q, new int[] {  });
		}

		public virtual void TestNotLike()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STShortTestCase((short
				)1));
			q.Descend(DESCENDANT).Constraints().Like().Not();
			Expect(q, new int[] { 0, 2, 3 });
		}

		public virtual void TestIdentity()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STShortTestCase((short
				)1));
			com.db4o.ObjectSet set = q.Execute();
			com.db4o.db4ounit.common.soda.classes.simple.STShortTestCase identityConstraint = 
				(com.db4o.db4ounit.common.soda.classes.simple.STShortTestCase)set.Next();
			identityConstraint.i_short = 9999;
			q = NewQuery();
			q.Constrain(identityConstraint).Identity();
			identityConstraint.i_short = 1;
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, _array[1]);
		}

		public virtual void TestNotIdentity()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STShortTestCase((short
				)1));
			com.db4o.ObjectSet set = q.Execute();
			com.db4o.db4ounit.common.soda.classes.simple.STShortTestCase identityConstraint = 
				(com.db4o.db4ounit.common.soda.classes.simple.STShortTestCase)set.Next();
			identityConstraint.i_short = 9080;
			q = NewQuery();
			q.Constrain(identityConstraint).Identity().Not();
			identityConstraint.i_short = 1;
			Expect(q, new int[] { 0, 2, 3 });
		}

		public virtual void TestConstraints()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STShortTestCase((short
				)1));
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STShortTestCase((short
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
