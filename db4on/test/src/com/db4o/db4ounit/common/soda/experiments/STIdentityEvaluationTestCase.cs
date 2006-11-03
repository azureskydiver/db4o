namespace com.db4o.db4ounit.common.soda.experiments
{
	public class STIdentityEvaluationTestCase : com.db4o.db4ounit.common.soda.util.SodaBaseTestCase
	{
		public override object[] CreateData()
		{
			com.db4o.db4ounit.common.soda.experiments.STIdentityEvaluationTestCase.Helper helperA
				 = new com.db4o.db4ounit.common.soda.experiments.STIdentityEvaluationTestCase.Helper
				("aaa");
			return new object[] { new com.db4o.db4ounit.common.soda.experiments.STIdentityEvaluationTestCase
				(null), new com.db4o.db4ounit.common.soda.experiments.STIdentityEvaluationTestCase
				(helperA), new com.db4o.db4ounit.common.soda.experiments.STIdentityEvaluationTestCase
				(helperA), new com.db4o.db4ounit.common.soda.experiments.STIdentityEvaluationTestCase
				(helperA), new com.db4o.db4ounit.common.soda.experiments.STIdentityEvaluationTestCase
				(new com.db4o.db4ounit.common.soda.experiments.STIdentityEvaluationTestCase.HelperDerivate
				("bbb")), new com.db4o.db4ounit.common.soda.experiments.STIdentityEvaluationTestCase
				(new com.db4o.db4ounit.common.soda.experiments.STIdentityEvaluationTestCase.Helper
				("dod")) };
		}

		public com.db4o.db4ounit.common.soda.experiments.STIdentityEvaluationTestCase.Helper
			 helper;

		public STIdentityEvaluationTestCase()
		{
		}

		public STIdentityEvaluationTestCase(com.db4o.db4ounit.common.soda.experiments.STIdentityEvaluationTestCase.Helper
			 h)
		{
			this.helper = h;
		}

		public virtual void Test()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(new com.db4o.db4ounit.common.soda.experiments.STIdentityEvaluationTestCase.Helper
				("aaa"));
			com.db4o.ObjectSet os = q.Execute();
			com.db4o.db4ounit.common.soda.experiments.STIdentityEvaluationTestCase.Helper helperA
				 = (com.db4o.db4ounit.common.soda.experiments.STIdentityEvaluationTestCase.Helper
				)os.Next();
			q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.experiments.STIdentityEvaluationTestCase)
				);
			q.Descend("helper").Constrain(helperA).Identity();
			q.Constrain(new _AnonymousInnerClass42(this));
			Expect(q, new int[] { 1, 2, 3 });
		}

		private sealed class _AnonymousInnerClass42 : com.db4o.query.Evaluation
		{
			public _AnonymousInnerClass42(STIdentityEvaluationTestCase _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Evaluate(com.db4o.query.Candidate candidate)
			{
				candidate.Include(true);
			}

			private readonly STIdentityEvaluationTestCase _enclosing;
		}

		public virtual void TestMemberClassConstraint()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.experiments.STIdentityEvaluationTestCase)
				);
			q.Descend("helper").Constrain(typeof(com.db4o.db4ounit.common.soda.experiments.STIdentityEvaluationTestCase.HelperDerivate)
				);
			Expect(q, new int[] { 4 });
		}

		public class Helper
		{
			public string hString;

			public Helper()
			{
			}

			public Helper(string str)
			{
				hString = str;
			}
		}

		public class HelperDerivate : com.db4o.db4ounit.common.soda.experiments.STIdentityEvaluationTestCase.Helper
		{
			public HelperDerivate()
			{
			}

			public HelperDerivate(string str) : base(str)
			{
			}
		}
	}
}
