namespace com.db4o.db4ounit.common.foundation
{
	/// <exclude></exclude>
	public class SortedCollection4TestCase : Db4oUnit.TestCase
	{
		private sealed class _AnonymousInnerClass14 : com.db4o.foundation.Comparison4
		{
			public _AnonymousInnerClass14()
			{
			}

			public int Compare(object x, object y)
			{
				return ((int)x) - ((int)y);
			}
		}

		internal static readonly com.db4o.foundation.Comparison4 INTEGER_COMPARISON = new 
			_AnonymousInnerClass14();

		public virtual void TestAddAllAndToArray()
		{
			object[] array = com.db4o.db4ounit.common.foundation.IntArrays4.ToObjectArray(new 
				int[] { 6, 4, 1, 2, 7, 3 });
			com.db4o.foundation.SortedCollection4 collection = NewSortedCollection();
			Db4oUnit.Assert.AreEqual(0, collection.Size());
			collection.AddAll(new com.db4o.foundation.ArrayIterator4(array));
			AssertCollection(new int[] { 1, 2, 3, 4, 6, 7 }, collection);
		}

		public virtual void TestAddRemove()
		{
			com.db4o.foundation.SortedCollection4 collection = NewSortedCollection();
			collection.Add(3);
			collection.Add(1);
			collection.Add(5);
			AssertCollection(new int[] { 1, 3, 5 }, collection);
			collection.Remove(3);
			AssertCollection(new int[] { 1, 5 }, collection);
			collection.Remove(1);
			AssertCollection(new int[] { 5 }, collection);
		}

		private void AssertCollection(int[] expected, com.db4o.foundation.SortedCollection4
			 collection)
		{
			Db4oUnit.Assert.AreEqual(expected.Length, collection.Size());
			Db4oUnit.ArrayAssert.AreEqual(com.db4o.db4ounit.common.foundation.IntArrays4.ToObjectArray
				(expected), collection.ToArray(new object[collection.Size()]));
		}

		private com.db4o.foundation.SortedCollection4 NewSortedCollection()
		{
			return new com.db4o.foundation.SortedCollection4(INTEGER_COMPARISON);
		}
	}
}
