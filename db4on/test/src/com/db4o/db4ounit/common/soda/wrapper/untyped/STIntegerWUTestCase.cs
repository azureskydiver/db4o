namespace com.db4o.db4ounit.common.soda.wrapper.untyped
{
	[System.Serializable]
	public class STIntegerWUTestCase : com.db4o.db4ounit.common.soda.util.SodaBaseTestCase
	{
		public object i_int;

		public STIntegerWUTestCase()
		{
		}

		private STIntegerWUTestCase(int a_int)
		{
			i_int = a_int;
		}

		public override object[] CreateData()
		{
			return new object[] { new com.db4o.db4ounit.common.soda.wrapper.untyped.STIntegerWUTestCase
				(0), new com.db4o.db4ounit.common.soda.wrapper.untyped.STIntegerWUTestCase(1), new 
				com.db4o.db4ounit.common.soda.wrapper.untyped.STIntegerWUTestCase(99), new com.db4o.db4ounit.common.soda.wrapper.untyped.STIntegerWUTestCase
				(909) };
		}

		public virtual void TestEquals()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STIntegerWUTestCase
				(0));
			q.Descend("i_int").Constrain(0);
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, _array[0]);
		}

		public virtual void TestNotEquals()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STIntegerWUTestCase
				());
			q.Descend("i_int").Constrain(0).Not();
			Expect(q, new int[] { 1, 2, 3 });
		}

		public virtual void TestGreater()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STIntegerWUTestCase
				(9));
			q.Descend("i_int").Constraints().Greater();
			Expect(q, new int[] { 2, 3 });
		}

		public virtual void TestSmaller()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STIntegerWUTestCase
				(1));
			q.Descend("i_int").Constraints().Smaller();
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, _array[0]);
		}

		public virtual void TestContains()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STIntegerWUTestCase
				(9));
			q.Descend("i_int").Constraints().Contains();
			Expect(q, new int[] { 2, 3 });
		}

		public virtual void TestNotContains()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STIntegerWUTestCase
				());
			q.Descend("i_int").Constrain(0).Contains().Not();
			Expect(q, new int[] { 1, 2 });
		}

		public virtual void TestLike()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STIntegerWUTestCase
				(90));
			q.Descend("i_int").Constraints().Like();
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, new com.db4o.db4ounit.common.soda.wrapper.untyped.STIntegerWUTestCase
				(909));
			q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STIntegerWUTestCase
				(10));
			q.Descend("i_int").Constraints().Like();
			Expect(q, new int[] {  });
		}

		public virtual void TestNotLike()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STIntegerWUTestCase
				(1));
			q.Descend("i_int").Constraints().Like().Not();
			Expect(q, new int[] { 0, 2, 3 });
		}

		public virtual void TestIdentity()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STIntegerWUTestCase
				(1));
			com.db4o.ObjectSet set = q.Execute();
			com.db4o.db4ounit.common.soda.wrapper.untyped.STIntegerWUTestCase identityConstraint
				 = (com.db4o.db4ounit.common.soda.wrapper.untyped.STIntegerWUTestCase)set.Next();
			identityConstraint.i_int = 9999;
			q = NewQuery();
			q.Constrain(identityConstraint).Identity();
			identityConstraint.i_int = 1;
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.ExpectOne(q, _array[1]);
		}

		public virtual void TestNotIdentity()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STIntegerWUTestCase
				(1));
			com.db4o.ObjectSet set = q.Execute();
			com.db4o.db4ounit.common.soda.wrapper.untyped.STIntegerWUTestCase identityConstraint
				 = (com.db4o.db4ounit.common.soda.wrapper.untyped.STIntegerWUTestCase)set.Next();
			identityConstraint.i_int = 9080;
			q = NewQuery();
			q.Constrain(identityConstraint).Identity().Not();
			identityConstraint.i_int = 1;
			Expect(q, new int[] { 0, 2, 3 });
		}

		public virtual void TestConstraints()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STIntegerWUTestCase
				(1));
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STIntegerWUTestCase
				(0));
			com.db4o.query.Constraints cs = q.Constraints();
			com.db4o.query.Constraint[] csa = cs.ToArray();
			if (csa.Length != 2)
			{
				Db4oUnit.Assert.Fail("Constraints not returned");
			}
		}

		public virtual void TestEvaluation()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.wrapper.untyped.STIntegerWUTestCase
				());
			q.Constrain(new _AnonymousInnerClass137(this));
			Expect(q, new int[] { 2, 3 });
		}

		private sealed class _AnonymousInnerClass137 : com.db4o.query.Evaluation
		{
			public _AnonymousInnerClass137(STIntegerWUTestCase _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Evaluate(com.db4o.query.Candidate candidate)
			{
				com.db4o.db4ounit.common.soda.wrapper.untyped.STIntegerWUTestCase sti = (com.db4o.db4ounit.common.soda.wrapper.untyped.STIntegerWUTestCase
					)candidate.GetObject();
				candidate.Include((((int)sti.i_int) + 2) > 100);
			}

			private readonly STIntegerWUTestCase _enclosing;
		}
	}
}
