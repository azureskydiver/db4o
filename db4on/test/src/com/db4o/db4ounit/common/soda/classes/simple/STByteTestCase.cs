namespace com.db4o.db4ounit.common.soda.classes.simple
{
	public class STByteTestCase : com.db4o.db4ounit.common.soda.util.SodaBaseTestCase
	{
		internal static readonly string DESCENDANT = "i_byte";

		public byte i_byte;

		public STByteTestCase()
		{
		}

		private STByteTestCase(byte a_byte)
		{
			i_byte = a_byte;
		}

		public override object[] CreateData()
		{
			return new object[] { new com.db4o.db4ounit.common.soda.classes.simple.STByteTestCase
				((byte)0), new com.db4o.db4ounit.common.soda.classes.simple.STByteTestCase((byte
				)1), new com.db4o.db4ounit.common.soda.classes.simple.STByteTestCase((byte)99), 
				new com.db4o.db4ounit.common.soda.classes.simple.STByteTestCase((byte)113) };
		}

		public virtual void TestEquals()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STByteTestCase((byte
				)0));
			q.Descend(DESCENDANT).Constrain((byte)0);
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, _array[0]);
		}

		public virtual void TestNotEquals()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(_array[0]);
			q.Descend(DESCENDANT).Constrain((byte)0).Not();
			Expect(q, new int[] { 1, 2, 3 });
		}

		public virtual void TestGreater()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STByteTestCase((byte
				)9));
			q.Descend(DESCENDANT).Constraints().Greater();
			Expect(q, new int[] { 2, 3 });
		}

		public virtual void TestSmaller()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STByteTestCase((byte
				)1));
			q.Descend(DESCENDANT).Constraints().Smaller();
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, _array[0]);
		}

		public virtual void TestContains()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STByteTestCase((byte
				)9));
			q.Descend(DESCENDANT).Constraints().Contains();
			Expect(q, new int[] { 2 });
		}

		public virtual void TestNotContains()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STByteTestCase((byte
				)0));
			q.Descend(DESCENDANT).Constrain((byte)0).Contains().Not();
			Expect(q, new int[] { 1, 2, 3 });
		}

		public virtual void TestLike()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STByteTestCase((byte
				)11));
			q.Descend(DESCENDANT).Constraints().Like();
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, new com.db4o.db4ounit.common.soda.classes.simple.STByteTestCase
				((byte)113));
			q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STByteTestCase((byte
				)10));
			q.Descend(DESCENDANT).Constraints().Like();
			Expect(q, new int[] {  });
		}

		public virtual void TestNotLike()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STByteTestCase((byte
				)1));
			q.Descend(DESCENDANT).Constraints().Like().Not();
			Expect(q, new int[] { 0, 2 });
		}

		public virtual void TestIdentity()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STByteTestCase((byte
				)1));
			com.db4o.ObjectSet set = q.Execute();
			com.db4o.db4ounit.common.soda.classes.simple.STByteTestCase identityConstraint = 
				(com.db4o.db4ounit.common.soda.classes.simple.STByteTestCase)set.Next();
			identityConstraint.i_byte = 102;
			q = NewQuery();
			q.Constrain(identityConstraint).Identity();
			identityConstraint.i_byte = 1;
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, _array[1]);
		}

		public virtual void TestNotIdentity()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STByteTestCase((byte
				)1));
			com.db4o.ObjectSet set = q.Execute();
			com.db4o.db4ounit.common.soda.classes.simple.STByteTestCase identityConstraint = 
				(com.db4o.db4ounit.common.soda.classes.simple.STByteTestCase)set.Next();
			identityConstraint.i_byte = 102;
			q = NewQuery();
			q.Constrain(identityConstraint).Identity().Not();
			identityConstraint.i_byte = 1;
			Expect(q, new int[] { 0, 2, 3 });
		}

		public virtual void TestConstraints()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STByteTestCase((byte
				)1));
			q.Constrain(new com.db4o.db4ounit.common.soda.classes.simple.STByteTestCase((byte
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
