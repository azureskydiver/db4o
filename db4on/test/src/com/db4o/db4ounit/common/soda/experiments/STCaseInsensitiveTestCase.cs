namespace com.db4o.db4ounit.common.soda.experiments
{
	public class STCaseInsensitiveTestCase : com.db4o.db4ounit.common.soda.util.SodaBaseTestCase
	{
		public string str;

		public STCaseInsensitiveTestCase()
		{
		}

		public STCaseInsensitiveTestCase(string str)
		{
			this.str = str;
		}

		public override object[] CreateData()
		{
			return new object[] { new com.db4o.db4ounit.common.soda.experiments.STCaseInsensitiveTestCase
				("Hihoho"), new com.db4o.db4ounit.common.soda.experiments.STCaseInsensitiveTestCase
				("Hello"), new com.db4o.db4ounit.common.soda.experiments.STCaseInsensitiveTestCase
				("hello") };
		}

		public virtual void Test()
		{
			com.db4o.query.Query q = NewQuery();
			q.Constrain(typeof(com.db4o.db4ounit.common.soda.experiments.STCaseInsensitiveTestCase)
				);
			q.Descend("str").Constrain(new _AnonymousInnerClass30(this));
			Expect(q, new int[] { 1, 2 });
		}

		private sealed class _AnonymousInnerClass30 : com.db4o.query.Evaluation
		{
			public _AnonymousInnerClass30(STCaseInsensitiveTestCase _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Evaluate(com.db4o.query.Candidate candidate)
			{
				candidate.Include(candidate.GetObject().ToString().ToLower().StartsWith("hell"));
			}

			private readonly STCaseInsensitiveTestCase _enclosing;
		}
	}
}
