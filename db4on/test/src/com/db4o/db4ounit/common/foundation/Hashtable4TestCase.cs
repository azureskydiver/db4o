namespace com.db4o.db4ounit.common.foundation
{
	public class Hashtable4TestCase : Db4oUnit.TestCase
	{
		public virtual void TestContainsKey()
		{
			com.db4o.foundation.Hashtable4 table = new com.db4o.foundation.Hashtable4();
			Db4oUnit.Assert.IsFalse(table.ContainsKey(null));
			Db4oUnit.Assert.IsFalse(table.ContainsKey("foo"));
			table.Put("foo", null);
			Db4oUnit.Assert.IsTrue(table.ContainsKey("foo"));
			table.Put("bar", "baz");
			Db4oUnit.Assert.IsTrue(table.ContainsKey("bar"));
			Db4oUnit.Assert.IsFalse(table.ContainsKey("baz"));
			Db4oUnit.Assert.IsTrue(table.ContainsKey("foo"));
			table.Remove("foo");
			Db4oUnit.Assert.IsTrue(table.ContainsKey("bar"));
			Db4oUnit.Assert.IsFalse(table.ContainsKey("foo"));
		}

		public virtual void TestByteArrayKeys()
		{
			byte[] key1 = new byte[] { 1, 2, 3 };
			byte[] key2 = new byte[] { 3, 2, 1 };
			byte[] key3 = new byte[] { 3, 2, 1 };
			com.db4o.foundation.Hashtable4 table = new com.db4o.foundation.Hashtable4(2);
			table.Put(key1, "foo");
			table.Put(key2, "bar");
			Db4oUnit.Assert.AreEqual("foo", table.Get(key1));
			Db4oUnit.Assert.AreEqual("bar", table.Get(key2));
			Db4oUnit.Assert.AreEqual(2, CountKeys(table));
			Db4oUnit.Assert.AreEqual(2, table.Size());
			table.Put(key3, "baz");
			Db4oUnit.Assert.AreEqual("foo", table.Get(key1));
			Db4oUnit.Assert.AreEqual("baz", table.Get(key2));
			Db4oUnit.Assert.AreEqual(2, CountKeys(table));
			Db4oUnit.Assert.AreEqual(2, table.Size());
			Db4oUnit.Assert.AreEqual("baz", table.Remove(key2));
			Db4oUnit.Assert.AreEqual(1, CountKeys(table));
			Db4oUnit.Assert.AreEqual(1, table.Size());
			Db4oUnit.Assert.AreEqual("foo", table.Remove(key1));
			Db4oUnit.Assert.AreEqual(0, CountKeys(table));
			Db4oUnit.Assert.AreEqual(0, table.Size());
		}

		public virtual void TestSameKeyTwice()
		{
			int key = 1;
			com.db4o.foundation.Hashtable4 table = new com.db4o.foundation.Hashtable4();
			table.Put(key, "foo");
			table.Put(key, "bar");
			Db4oUnit.Assert.AreEqual("bar", table.Get(key));
			Db4oUnit.Assert.AreEqual(1, CountKeys(table));
		}

		internal class Key
		{
			private int _hashCode;

			public Key(int hashCode)
			{
				_hashCode = hashCode;
			}

			public override int GetHashCode()
			{
				return _hashCode;
			}
		}

		public virtual void TestDifferentKeysSameHashCode()
		{
			com.db4o.db4ounit.common.foundation.Hashtable4TestCase.Key key1 = new com.db4o.db4ounit.common.foundation.Hashtable4TestCase.Key
				(1);
			com.db4o.db4ounit.common.foundation.Hashtable4TestCase.Key key2 = new com.db4o.db4ounit.common.foundation.Hashtable4TestCase.Key
				(1);
			com.db4o.db4ounit.common.foundation.Hashtable4TestCase.Key key3 = new com.db4o.db4ounit.common.foundation.Hashtable4TestCase.Key
				(2);
			com.db4o.foundation.Hashtable4 table = new com.db4o.foundation.Hashtable4(2);
			table.Put(key1, "foo");
			table.Put(key2, "bar");
			Db4oUnit.Assert.AreEqual("foo", table.Get(key1));
			Db4oUnit.Assert.AreEqual("bar", table.Get(key2));
			Db4oUnit.Assert.AreEqual(2, CountKeys(table));
			table.Put(key2, "baz");
			Db4oUnit.Assert.AreEqual("foo", table.Get(key1));
			Db4oUnit.Assert.AreEqual("baz", table.Get(key2));
			Db4oUnit.Assert.AreEqual(2, CountKeys(table));
			table.Put(key1, "spam");
			Db4oUnit.Assert.AreEqual("spam", table.Get(key1));
			Db4oUnit.Assert.AreEqual("baz", table.Get(key2));
			Db4oUnit.Assert.AreEqual(2, CountKeys(table));
			table.Put(key3, "eggs");
			Db4oUnit.Assert.AreEqual("spam", table.Get(key1));
			Db4oUnit.Assert.AreEqual("baz", table.Get(key2));
			Db4oUnit.Assert.AreEqual("eggs", table.Get(key3));
			Db4oUnit.Assert.AreEqual(3, CountKeys(table));
			table.Put(key2, "mice");
			Db4oUnit.Assert.AreEqual("spam", table.Get(key1));
			Db4oUnit.Assert.AreEqual("mice", table.Get(key2));
			Db4oUnit.Assert.AreEqual("eggs", table.Get(key3));
			Db4oUnit.Assert.AreEqual(3, CountKeys(table));
		}

		internal class KeyCount
		{
			public int keys;
		}

		private int CountKeys(com.db4o.foundation.Hashtable4 table)
		{
			com.db4o.db4ounit.common.foundation.Hashtable4TestCase.KeyCount count = new com.db4o.db4ounit.common.foundation.Hashtable4TestCase.KeyCount
				();
			table.ForEachKey(new _AnonymousInnerClass126(this, count));
			return count.keys;
		}

		private sealed class _AnonymousInnerClass126 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass126(Hashtable4TestCase _enclosing, com.db4o.db4ounit.common.foundation.Hashtable4TestCase.KeyCount
				 count)
			{
				this._enclosing = _enclosing;
				this.count = count;
			}

			public void Visit(object key)
			{
				++count.keys;
			}

			private readonly Hashtable4TestCase _enclosing;

			private readonly com.db4o.db4ounit.common.foundation.Hashtable4TestCase.KeyCount 
				count;
		}
	}
}
