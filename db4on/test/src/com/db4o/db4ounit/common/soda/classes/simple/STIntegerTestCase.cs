namespace com.db4o.db4ounit.common.soda.classes.simple
{
	public class STIntegerTestCase : com.db4o.db4ounit.common.soda.util.SodaBaseTestCase
	{
		public int i_int;

		public STIntegerTestCase()
		{
		}

		private STIntegerTestCase(int a_int)
		{
			i_int = a_int;
		}

		public override object[] CreateData()
		{
			return new object[] { new com.db4o.db4ounit.common.soda.classes.simple.STIntegerTestCase
				(0), new com.db4o.db4ounit.common.soda.classes.simple.STIntegerTestCase(1), new 
				com.db4o.db4ounit.common.soda.classes.simple.STIntegerTestCase(99), new com.db4o.db4ounit.common.soda.classes.simple.STIntegerTestCase
				(909) };
		}

		public virtual void TestEquals()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STIntegerTestCase(0)
				);
			q.Descend("i_int").Constrain(0);
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, _array[0]);
		}

		public virtual void TestNotEquals()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(_array[0]);
			q.Descend("i_int").Constrain(0).Not();
			Expect(q, new int[] { 1, 2, 3 });
		}

		public virtual void TestGreater()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STIntegerTestCase(9)
				);
			q.Descend("i_int").Constraints().Greater();
			Expect(q, new int[] { 2, 3 });
		}

		public virtual void TestSmaller()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STIntegerTestCase(1)
				);
			q.Descend("i_int").Constraints().Smaller();
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, _array[0]);
		}

		public virtual void TestContains()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STIntegerTestCase(9)
				);
			q.Descend("i_int").Constraints().Contains();
			Expect(q, new int[] { 2, 3 });
		}

		public virtual void TestNotContains()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STIntegerTestCase(0)
				);
			q.Descend("i_int").Constrain(0).Contains().Not();
			Expect(q, new int[] { 1, 2 });
		}

		public virtual void TestLike()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STIntegerTestCase(90
				));
			q.Descend("i_int").Constraints().Like();
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, new com.db4o.db4ounit.common.soda.classes.simple.STIntegerTestCase
				(909));
			q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STIntegerTestCase(10
				));
			q.Descend("i_int").Constraints().Like();
			Expect(q, new int[] {  });
		}

		public virtual void TestNotLike()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STIntegerTestCase(1)
				);
			q.Descend("i_int").Constraints().Like().Not();
			Expect(q, new int[] { 0, 2, 3 });
		}

		public virtual void TestIdentity()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STIntegerTestCase(1)
				);
			com.db4o.ObjectSet set = q.Execute();
			com.db4o.db4ounit.common.soda.classes.simple.STIntegerTestCase identityConstraint
				 = (com.db4o.db4ounit.common.soda.classes.simple.STIntegerTestCase)set.Next();
			identityConstraint.i_int = 9999;
			q = NewQuery();
			q.Constrain(identityConstraint).Identity();
			identityConstraint.i_int = 1;
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, _array[1]);
		}

		public virtual void TestNotIdentity()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STIntegerTestCase(1)
				);
			com.db4o.ObjectSet set = q.Execute();
			com.db4o.db4ounit.common.soda.classes.simple.STIntegerTestCase identityConstraint
				 = (com.db4o.db4ounit.common.soda.classes.simple.STIntegerTestCase)set.Next();
			identityConstraint.i_int = 9080;
			q = NewQuery();
			q.Constrain(identityConstraint).Identity().Not();
			identityConstraint.i_int = 1;
			Expect(q, new int[] { 0, 2, 3 });
		}

		public virtual void TestConstraints()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STIntegerTestCase(1)
				);
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STIntegerTestCase(0)
				);
			com.db4o.query.Constraints cs = q.Constraints();
			com.db4o.query.Constraint[] csa = cs.ToArray();
			if (csa.Length != 2)
			{
				Db4oUnit.Assert.Fail("Constraints not returned");
			}
		}
	}
}
