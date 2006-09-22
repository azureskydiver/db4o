namespace com.db4o.db4ounit.common.foundation
{
	public class CompositeIterator4TestCase : Db4oUnit.TestCase
	{
		public virtual void TestWithEmptyIterators()
		{
			com.db4o.foundation.Collection4 iterators = new com.db4o.foundation.Collection4();
			iterators.Add(com.db4o.db4ounit.common.foundation.IntArrays4.NewIterator(new int[
				] { 1, 2, 3 }));
			iterators.Add(com.db4o.db4ounit.common.foundation.IntArrays4.NewIterator(new int[
				] {  }));
			iterators.Add(com.db4o.db4ounit.common.foundation.IntArrays4.NewIterator(new int[
				] { 4 }));
			iterators.Add(com.db4o.db4ounit.common.foundation.IntArrays4.NewIterator(new int[
				] { 5, 6 }));
			com.db4o.foundation.CompositeIterator4 iterator = new com.db4o.foundation.CompositeIterator4
				(iterators.StrictIterator());
			com.db4o.db4ounit.common.foundation.IteratorAssert.AreEqual(com.db4o.db4ounit.common.foundation.IntArrays4
				.NewIterator(new int[] { 1, 2, 3, 4, 5, 6 }), iterator);
		}
	}
}
