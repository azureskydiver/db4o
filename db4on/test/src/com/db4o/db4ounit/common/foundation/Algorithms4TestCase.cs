namespace com.db4o.db4ounit.common.foundation
{
	public class Algorithms4TestCase : Db4oUnit.TestCase
	{
		public class QuickSortableIntArray : com.db4o.foundation.QuickSortable4
		{
			private int[] ints;

			public QuickSortableIntArray(int[] ints)
			{
				this.ints = ints;
			}

			public virtual int Compare(int leftIndex, int rightIndex)
			{
				return ints[leftIndex] - ints[rightIndex];
			}

			public virtual int Size()
			{
				return ints.Length;
			}

			public virtual void Swap(int leftIndex, int rightIndex)
			{
				int temp = ints[leftIndex];
				ints[leftIndex] = ints[rightIndex];
				ints[rightIndex] = temp;
			}

			public virtual void AssertSorted()
			{
				for (int i = 0; i < ints.Length; i++)
				{
					Db4oUnit.Assert.AreEqual(i + 1, ints[i]);
				}
			}
		}

		public virtual void TestUnsorted()
		{
			int[] ints = new int[] { 3, 5, 2, 1, 4 };
			AssertQSort(ints);
		}

		public virtual void TestStackUsage()
		{
			int[] ints = new int[50000];
			for (int i = 0; i < ints.Length; i++)
			{
				ints[i] = i + 1;
			}
			AssertQSort(ints);
		}

		private void AssertQSort(int[] ints)
		{
			com.db4o.db4ounit.common.foundation.Algorithms4TestCase.QuickSortableIntArray sample
				 = new com.db4o.db4ounit.common.foundation.Algorithms4TestCase.QuickSortableIntArray
				(ints);
			com.db4o.foundation.Algorithms4.Qsort(sample);
			sample.AssertSorted();
		}
	}
}
