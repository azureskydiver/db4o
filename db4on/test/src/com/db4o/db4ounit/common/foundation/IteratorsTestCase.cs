namespace com.db4o.db4ounit.common.foundation
{
	/// <exclude></exclude>
	public class IteratorsTestCase : Db4oUnit.TestCase
	{
		public virtual void TestMap()
		{
			int[] array = new int[] { 1, 2, 3 };
			com.db4o.foundation.Collection4 args = new com.db4o.foundation.Collection4();
			System.Collections.IEnumerator iterator = com.db4o.foundation.Iterators.Map(com.db4o.db4ounit.common.foundation.IntArrays4
				.NewIterator(array), new _AnonymousInnerClass19(this, args));
			Db4oUnit.Assert.IsNotNull(iterator);
			Db4oUnit.Assert.AreEqual(0, args.Size());
			for (int i = 0; i < array.Length; ++i)
			{
				Db4oUnit.Assert.IsTrue(iterator.MoveNext());
				Db4oUnit.Assert.AreEqual(i + 1, args.Size());
				Db4oUnit.Assert.AreEqual(array[i] * 2, iterator.Current);
			}
		}

		private sealed class _AnonymousInnerClass19 : com.db4o.foundation.Function4
		{
			public _AnonymousInnerClass19(IteratorsTestCase _enclosing, com.db4o.foundation.Collection4
				 args)
			{
				this._enclosing = _enclosing;
				this.args = args;
			}

			public object Apply(object arg)
			{
				args.Add(arg);
				return ((int)arg) * 2;
			}

			private readonly IteratorsTestCase _enclosing;

			private readonly com.db4o.foundation.Collection4 args;
		}
	}
}
