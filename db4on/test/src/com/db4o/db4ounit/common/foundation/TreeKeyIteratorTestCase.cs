namespace com.db4o.db4ounit.common.foundation
{
	public class TreeKeyIteratorTestCase : Db4oUnit.TestCase
	{
		public static void Main(string[] args)
		{
			new Db4oUnit.TestRunner(typeof(com.db4o.db4ounit.common.foundation.TreeKeyIteratorTestCase)
				).Run();
		}

		private static int[] VALUES = new int[] { 1, 3, 5, 7, 9, 10, 11, 13, 24, 76 };

		public virtual void TestIterate()
		{
			for (int i = 1; i <= VALUES.Length; i++)
			{
				AssertIterateValues(VALUES, i);
			}
		}

		public virtual void TestMoveNextAfterCompletion()
		{
			System.Collections.IEnumerator i = new com.db4o.foundation.TreeKeyIterator(CreateTree
				(VALUES));
			while (i.MoveNext())
			{
			}
			Db4oUnit.Assert.IsFalse(i.MoveNext());
		}

		private void AssertIterateValues(int[] values, int count)
		{
			int[] testValues = new int[count];
			System.Array.Copy(values, 0, testValues, 0, count);
			AssertIterateValues(testValues);
		}

		private void AssertIterateValues(int[] values)
		{
			com.db4o.db4ounit.common.btree.ExpectingVisitor expectingVisitor = new com.db4o.db4ounit.common.btree.ExpectingVisitor
				(com.db4o.db4ounit.common.foundation.IntArrays4.ToObjectArray(values), true, false
				);
			System.Collections.IEnumerator i = new com.db4o.foundation.TreeKeyIterator(CreateTree
				(values));
			while (i.MoveNext())
			{
				expectingVisitor.Visit(i.Current);
			}
			expectingVisitor.AssertExpectations();
		}

		private com.db4o.foundation.Tree CreateTree(int[] values)
		{
			com.db4o.foundation.Tree tree = new com.db4o.@internal.TreeInt(values[0]);
			for (int i = 1; i < values.Length; i++)
			{
				tree = tree.Add(new com.db4o.@internal.TreeInt(values[i]));
			}
			return tree;
		}

		public virtual void TestEmpty()
		{
			System.Collections.IEnumerator i = new com.db4o.foundation.TreeKeyIterator(null);
			Db4oUnit.Assert.IsFalse(i.MoveNext());
		}
	}
}
