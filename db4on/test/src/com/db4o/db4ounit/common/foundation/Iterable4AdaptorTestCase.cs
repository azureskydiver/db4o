namespace com.db4o.db4ounit.common.foundation
{
	/// <exclude></exclude>
	public class Iterable4AdaptorTestCase : Db4oUnit.TestCase
	{
		public virtual void TestEmptyIterator()
		{
			com.db4o.foundation.Iterable4Adaptor adaptor = NewAdaptor(new int[] {  });
			Db4oUnit.Assert.IsFalse(adaptor.HasNext());
			Db4oUnit.Assert.IsFalse(adaptor.HasNext());
			Db4oUnit.Assert.Expect(typeof(System.InvalidOperationException), new _AnonymousInnerClass20
				(this, adaptor));
		}

		private sealed class _AnonymousInnerClass20 : Db4oUnit.CodeBlock
		{
			public _AnonymousInnerClass20(Iterable4AdaptorTestCase _enclosing, com.db4o.foundation.Iterable4Adaptor
				 adaptor)
			{
				this._enclosing = _enclosing;
				this.adaptor = adaptor;
			}

			public void Run()
			{
				adaptor.Next();
			}

			private readonly Iterable4AdaptorTestCase _enclosing;

			private readonly com.db4o.foundation.Iterable4Adaptor adaptor;
		}

		public virtual void TestHasNext()
		{
			int[] expected = new int[] { 1, 2, 3 };
			com.db4o.foundation.Iterable4Adaptor adaptor = NewAdaptor(expected);
			for (int i = 0; i < expected.Length; i++)
			{
				AssertHasNext(adaptor);
				Db4oUnit.Assert.AreEqual(expected[i], adaptor.Next());
			}
			Db4oUnit.Assert.IsFalse(adaptor.HasNext());
		}

		public virtual void TestNext()
		{
			int[] expected = new int[] { 1, 2, 3 };
			com.db4o.foundation.Iterable4Adaptor adaptor = NewAdaptor(expected);
			for (int i = 0; i < expected.Length; i++)
			{
				Db4oUnit.Assert.AreEqual(expected[i], adaptor.Next());
			}
			Db4oUnit.Assert.IsFalse(adaptor.HasNext());
		}

		private com.db4o.foundation.Iterable4Adaptor NewAdaptor(int[] expected)
		{
			return new com.db4o.foundation.Iterable4Adaptor(NewIterable(expected));
		}

		private void AssertHasNext(com.db4o.foundation.Iterable4Adaptor adaptor)
		{
			for (int i = 0; i < 10; ++i)
			{
				Db4oUnit.Assert.IsTrue(adaptor.HasNext());
			}
		}

		private System.Collections.IEnumerable NewIterable(int[] values)
		{
			com.db4o.foundation.Collection4 collection = new com.db4o.foundation.Collection4(
				);
			collection.AddAll(com.db4o.db4ounit.common.foundation.IntArrays4.ToObjectArray(values
				));
			return collection;
		}
	}
}
