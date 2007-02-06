namespace com.db4o.db4ounit.common.assorted
{
	public class GetSingleSimpleArrayTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public virtual void Test()
		{
			com.db4o.ObjectSet result = Db().Get(new double[] { 0.6, 0.4 });
			Db4oUnit.Assert.IsFalse(result.HasNext());
			Db4oUnit.Assert.IsFalse(result.HasNext());
			Db4oUnit.Assert.Expect(typeof(System.InvalidOperationException), new _AnonymousInnerClass17
				(this, result));
		}

		private sealed class _AnonymousInnerClass17 : Db4oUnit.CodeBlock
		{
			public _AnonymousInnerClass17(GetSingleSimpleArrayTestCase _enclosing, com.db4o.ObjectSet
				 result)
			{
				this._enclosing = _enclosing;
				this.result = result;
			}

			public void Run()
			{
				result.Next();
			}

			private readonly GetSingleSimpleArrayTestCase _enclosing;

			private readonly com.db4o.ObjectSet result;
		}
	}
}
