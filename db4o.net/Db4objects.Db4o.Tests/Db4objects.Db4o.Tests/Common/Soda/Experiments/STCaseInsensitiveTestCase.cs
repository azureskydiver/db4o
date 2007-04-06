using Db4objects.Db4o.Query;
using Db4objects.Db4o.Tests.Common.Soda.Util;

namespace Db4objects.Db4o.Tests.Common.Soda.Experiments
{
	public class STCaseInsensitiveTestCase : SodaBaseTestCase
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
			return new object[] { new Db4objects.Db4o.Tests.Common.Soda.Experiments.STCaseInsensitiveTestCase
				("Hihoho"), new Db4objects.Db4o.Tests.Common.Soda.Experiments.STCaseInsensitiveTestCase
				("Hello"), new Db4objects.Db4o.Tests.Common.Soda.Experiments.STCaseInsensitiveTestCase
				("hello") };
		}

		public virtual void Test()
		{
			IQuery q = NewQuery();
			q.Constrain(typeof(Db4objects.Db4o.Tests.Common.Soda.Experiments.STCaseInsensitiveTestCase)
				);
			q.Descend("str").Constrain(new _AnonymousInnerClass30(this));
			Expect(q, new int[] { 1, 2 });
		}

		private sealed class _AnonymousInnerClass30 : IEvaluation
		{
			public _AnonymousInnerClass30(STCaseInsensitiveTestCase _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Evaluate(ICandidate candidate)
			{
				candidate.Include(candidate.GetObject().ToString().ToLower().StartsWith("hell"));
			}

			private readonly STCaseInsensitiveTestCase _enclosing;
		}
	}
}
