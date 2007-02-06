namespace com.db4o.db4ounit.common.soda.wrapper.untyped
{
	[System.Serializable]
	public class STByteWUTestCase : com.db4o.db4ounit.common.soda.util.SodaBaseTestCase
	{
		internal static readonly string DESCENDANT = "i_byte";

		public object i_byte;

		public STByteWUTestCase()
		{
		}

		private STByteWUTestCase(byte a_byte)
		{
			i_byte = a_byte;
		}

		public override object[] CreateData()
		{
			return new object[] { new com.db4o.db4ounit.common.soda.wrapper.untyped.STByteWUTestCase
				((byte)0), new com.db4o.db4ounit.common.soda.wrapper.untyped.STByteWUTestCase((byte
				)1), new com.db4o.db4ounit.common.soda.wrapper.untyped.STByteWUTestCase((byte)99
				), new com.db4o.db4ounit.common.soda.wrapper.untyped.STByteWUTestCase((byte)113)
				 };
		}

		public virtual void TestEquals()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STByteWUTestCase((byte
				)0));
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, _array[0]);
		}

		public virtual void TestNotEquals()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(_array[0]);
			q.Descend(DESCENDANT).Constraints().Not();
			Expect(q, new int[] { 1, 2, 3 });
		}

		public virtual void TestGreater()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STByteWUTestCase((byte
				)9));
			q.Descend(DESCENDANT).Constraints().Greater();
			Expect(q, new int[] { 2, 3 });
		}

		public virtual void TestSmaller()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STByteWUTestCase((byte
				)1));
			q.Descend(DESCENDANT).Constraints().Smaller();
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, _array[0]);
		}

		public virtual void TestContains()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STByteWUTestCase((byte
				)9));
			q.Descend(DESCENDANT).Constraints().Contains();
			Expect(q, new int[] { 2 });
		}

		public virtual void TestNotContains()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STByteWUTestCase((byte
				)0));
			q.Descend(DESCENDANT).Constraints().Contains().Not();
			Expect(q, new int[] { 1, 2, 3 });
		}

		public virtual void TestLike()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STByteWUTestCase((byte
				)11));
			q.Descend(DESCENDANT).Constraints().Like();
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, new com.db4o.db4ounit.common.soda.wrapper.untyped.STByteWUTestCase
				((byte)113));
			q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STByteWUTestCase((byte
				)10));
			q.Descend(DESCENDANT).Constraints().Like();
			Expect(q, new int[] {  });
		}

		public virtual void TestNotLike()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STByteWUTestCase((byte
				)1));
			q.Descend(DESCENDANT).Constraints().Like().Not();
			Expect(q, new int[] { 0, 2 });
		}

		public virtual void TestIdentity()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STByteWUTestCase((byte
				)1));
			com.db4o.ObjectSet set = q.Execute();
			com.db4o.db4ounit.common.soda.wrapper.untyped.STByteWUTestCase identityConstraint
				 = (com.db4o.db4ounit.common.soda.wrapper.untyped.STByteWUTestCase)set.Next();
			identityConstraint.i_byte = (byte)102;
			q = NewQuery();
			q.Constrain(identityConstraint).Identity();
			identityConstraint.i_byte = (byte)1;
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, _array[1]);
		}

		public virtual void TestNotIdentity()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STByteWUTestCase((byte
				)1));
			com.db4o.ObjectSet set = q.Execute();
			com.db4o.db4ounit.common.soda.wrapper.untyped.STByteWUTestCase identityConstraint
				 = (com.db4o.db4ounit.common.soda.wrapper.untyped.STByteWUTestCase)set.Next();
			identityConstraint.i_byte = (byte)102;
			q = NewQuery();
			q.Constrain(identityConstraint).Identity().Not();
			identityConstraint.i_byte = (byte)1;
			Expect(q, new int[] { 0, 2, 3 });
		}

		public virtual void TestConstraints()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STByteWUTestCase((byte
				)1));
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STByteWUTestCase((byte
				)0));
			com.db4o.query.Constraints cs = q.Constraints();
			com.db4o.query.Constraint[] csa = cs.ToArray();
			if (csa.Length != 2)
			{
				Db4oUnit.Assert.Fail("Constraints not returned");
			}
		}

		public virtual void TestNull()
		{
		}

		public virtual void TestEvaluation()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STByteWUTestCase());
			q.Constrain(new _AnonymousInnerClass139(this));
			Expect(q, new int[] { 2, 3 });
		}

		private sealed class _AnonymousInnerClass139 : com.db4o.query.Evaluation
		{
			public _AnonymousInnerClass139(STByteWUTestCase _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Evaluate(com.db4o.query.Candidate candidate)
			{
				com.db4o.db4ounit.common.soda.wrapper.untyped.STByteWUTestCase sts = (com.db4o.db4ounit.common.soda.wrapper.untyped.STByteWUTestCase
					)candidate.GetObject();
				candidate.Include((((byte)sts.i_byte) + 2) > 100);
			}

			private readonly STByteWUTestCase _enclosing;
		}
	}
}
