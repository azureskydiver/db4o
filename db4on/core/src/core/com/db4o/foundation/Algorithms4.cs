namespace com.db4o.foundation
{
	/// <exclude></exclude>
	public class Algorithms4
	{
		public static void Qsort(com.db4o.foundation.QuickSortable4 sortable)
		{
			Qsort(sortable, 0, sortable.Size() - 1);
		}

		public static void Qsort(com.db4o.foundation.QuickSortable4 sortable, int from, int
			 to)
		{
			if (to - from < 1)
			{
				return;
			}
			int pivot = to;
			int left = from;
			int right = to;
			while (left < right)
			{
				while (left < right && sortable.Compare(left, pivot) < 0)
				{
					left++;
				}
				while (left < right && sortable.Compare(right, pivot) >= 0)
				{
					right--;
				}
				Swap(sortable, left, right);
			}
			Swap(sortable, to, right);
			Qsort(sortable, from, right - 1);
			Qsort(sortable, right + 1, to);
		}

		private static void Swap(com.db4o.foundation.QuickSortable4 sortable, int left, int
			 right)
		{
			if (left == right)
			{
				return;
			}
			sortable.Swap(left, right);
		}
	}
}
