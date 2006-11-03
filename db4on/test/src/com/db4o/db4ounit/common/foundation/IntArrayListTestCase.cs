namespace com.db4o.db4ounit.common.foundation
{
	/// <exclude></exclude>
	public class IntArrayListTestCase : Db4oUnit.TestCase
	{
		public virtual void TestIteratorGoesBackwards()
		{
			com.db4o.foundation.IntArrayList list = new com.db4o.foundation.IntArrayList();
			AssertIterator(new int[] {  }, list.IntIterator());
			list.Add(1);
			AssertIterator(new int[] { 1 }, list.IntIterator());
			list.Add(2);
			AssertIterator(new int[] { 2, 1 }, list.IntIterator());
		}

		private void AssertIterator(int[] expected, com.db4o.foundation.IntIterator4 iterator
			)
		{
			for (int i = 0; i < expected.Length; ++i)
			{
				Db4oUnit.Assert.IsTrue(iterator.MoveNext());
				Db4oUnit.Assert.AreEqual(expected[i], iterator.CurrentInt());
				Db4oUnit.Assert.AreEqual(expected[i], iterator.Current);
			}
			Db4oUnit.Assert.IsFalse(iterator.MoveNext());
		}
	}
}
