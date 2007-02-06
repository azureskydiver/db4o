namespace com.db4o.db4ounit.common.foundation
{
	public class Queue4TestCase : Db4oUnit.TestCase
	{
		public virtual void TestIterator()
		{
			com.db4o.foundation.Queue4 queue = new com.db4o.foundation.Queue4();
			string[] data = { "a", "b", "c", "d" };
			for (int idx = 0; idx < data.Length; idx++)
			{
				AssertIterator(queue, data, idx);
				queue.Add(data[idx]);
				AssertIterator(queue, data, idx + 1);
			}
		}

		private void AssertIterator(com.db4o.foundation.Queue4 queue, string[] data, int 
			size)
		{
			System.Collections.IEnumerator iter = queue.Iterator();
			for (int idx = 0; idx < size; idx++)
			{
				Db4oUnit.Assert.IsTrue(iter.MoveNext(), "should be able to move in iteration #" +
					 idx + " of " + size);
				Db4oUnit.Assert.AreEqual(data[idx], iter.Current);
			}
			Db4oUnit.Assert.IsFalse(iter.MoveNext());
		}
	}
}
