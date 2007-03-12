namespace com.db4o.db4ounit.common.foundation
{
	/// <exclude></exclude>
	public class IteratorsTestCase : Db4oUnit.TestCase
	{
		public virtual void TestFilter()
		{
			AssertFilter(new string[] { "bar", "baz" }, new string[] { "foo", "bar", "baz", "zong"
				 }, new _AnonymousInnerClass18(this));
			AssertFilter(new string[] { "foo", "bar" }, new string[] { "foo", "bar" }, new _AnonymousInnerClass26
				(this));
			AssertFilter(new string[0], new string[] { "foo", "bar" }, new _AnonymousInnerClass35
				(this));
		}

		private sealed class _AnonymousInnerClass18 : com.db4o.foundation.Predicate4
		{
			public _AnonymousInnerClass18(IteratorsTestCase _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public bool Match(object candidate)
			{
				return ((string)candidate).StartsWith("b");
			}

			private readonly IteratorsTestCase _enclosing;
		}

		private sealed class _AnonymousInnerClass26 : com.db4o.foundation.Predicate4
		{
			public _AnonymousInnerClass26(IteratorsTestCase _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public bool Match(object candidate)
			{
				return true;
			}

			private readonly IteratorsTestCase _enclosing;
		}

		private sealed class _AnonymousInnerClass35 : com.db4o.foundation.Predicate4
		{
			public _AnonymousInnerClass35(IteratorsTestCase _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public bool Match(object candidate)
			{
				return false;
			}

			private readonly IteratorsTestCase _enclosing;
		}

		private void AssertFilter(string[] expected, string[] actual, com.db4o.foundation.Predicate4
			 filter)
		{
			com.db4o.db4ounit.common.foundation.IteratorAssert.AreEqual(expected, com.db4o.foundation.Iterators
				.Filter(actual, filter));
		}

		public virtual void TestMap()
		{
			int[] array = new int[] { 1, 2, 3 };
			com.db4o.foundation.Collection4 args = new com.db4o.foundation.Collection4();
			System.Collections.IEnumerator iterator = com.db4o.foundation.Iterators.Map(com.db4o.db4ounit.common.foundation.IntArrays4
				.NewIterator(array), new _AnonymousInnerClass51(this, args));
			Db4oUnit.Assert.IsNotNull(iterator);
			Db4oUnit.Assert.AreEqual(0, args.Size());
			for (int i = 0; i < array.Length; ++i)
			{
				Db4oUnit.Assert.IsTrue(iterator.MoveNext());
				Db4oUnit.Assert.AreEqual(i + 1, args.Size());
				Db4oUnit.Assert.AreEqual(array[i] * 2, iterator.Current);
			}
		}

		private sealed class _AnonymousInnerClass51 : com.db4o.foundation.Function4
		{
			public _AnonymousInnerClass51(IteratorsTestCase _enclosing, com.db4o.foundation.Collection4
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
