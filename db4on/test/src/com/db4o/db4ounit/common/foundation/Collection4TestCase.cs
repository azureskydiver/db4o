namespace com.db4o.db4ounit.common.foundation
{
	public class Collection4TestCase : Db4oUnit.TestCase
	{
		public virtual void TestFastIterator()
		{
			com.db4o.foundation.Collection4 c = new com.db4o.foundation.Collection4();
			string[] expected = new string[] { "1", "2", "3" };
			c.AddAll(expected);
			com.db4o.foundation.Iterator4 iterator = c.Iterator();
			Db4oUnit.Assert.IsNotNull(iterator);
			for (int i = expected.Length - 1; i >= 0; --i)
			{
				Db4oUnit.Assert.IsTrue(iterator.MoveNext());
				Db4oUnit.Assert.AreEqual(expected[i], iterator.Current());
			}
			Db4oUnit.Assert.IsFalse(iterator.MoveNext());
		}

		public virtual void TestStrictIterator()
		{
			com.db4o.foundation.Collection4 c = new com.db4o.foundation.Collection4();
			string[] expected = new string[] { "1", "2", "3" };
			c.AddAll(expected);
			com.db4o.foundation.Iterator4 iterator = c.StrictIterator();
			Db4oUnit.Assert.IsNotNull(iterator);
			for (int i = 0; i < expected.Length; ++i)
			{
				Db4oUnit.Assert.IsTrue(iterator.MoveNext());
				Db4oUnit.Assert.AreEqual(expected[i], iterator.Current());
			}
			Db4oUnit.Assert.IsFalse(iterator.MoveNext());
		}

		public virtual void TestToString()
		{
			com.db4o.foundation.Collection4 c = new com.db4o.foundation.Collection4();
			Db4oUnit.Assert.AreEqual("[]", c.ToString());
			c.Add("foo");
			Db4oUnit.Assert.AreEqual("[foo]", c.ToString());
			c.Add("bar");
			Db4oUnit.Assert.AreEqual("[foo, bar]", c.ToString());
		}
	}
}
