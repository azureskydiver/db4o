namespace com.db4o.db4ounit.common.foundation
{
	public class ArrayIterator4TestCase : Db4oUnit.TestCase
	{
		public virtual void TestEmptyArray()
		{
			AssertExhausted(new com.db4o.foundation.ArrayIterator4(new object[0]));
		}

		public virtual void TestArray()
		{
			com.db4o.foundation.ArrayIterator4 i = new com.db4o.foundation.ArrayIterator4(new 
				object[] { "foo", "bar" });
			Db4oUnit.Assert.IsTrue(i.MoveNext());
			Db4oUnit.Assert.AreEqual("foo", i.Current());
			Db4oUnit.Assert.IsTrue(i.MoveNext());
			Db4oUnit.Assert.AreEqual("bar", i.Current());
			AssertExhausted(i);
		}

		private void AssertExhausted(com.db4o.foundation.ArrayIterator4 i)
		{
			Db4oUnit.Assert.IsFalse(i.MoveNext());
			Db4oUnit.Assert.Expect(typeof(System.IndexOutOfRangeException), new _AnonymousInnerClass29
				(this, i));
		}

		private sealed class _AnonymousInnerClass29 : Db4oUnit.CodeBlock
		{
			public _AnonymousInnerClass29(ArrayIterator4TestCase _enclosing, com.db4o.foundation.ArrayIterator4
				 i)
			{
				this._enclosing = _enclosing;
				this.i = i;
			}

			public void Run()
			{
				i.Current();
			}

			private readonly ArrayIterator4TestCase _enclosing;

			private readonly com.db4o.foundation.ArrayIterator4 i;
		}
	}
}
