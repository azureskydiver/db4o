namespace com.db4o.foundation
{
	/// <exclude></exclude>
	public class Algorithms4
	{
		private class Range
		{
			internal int _from;

			internal int _to;

			public Range(int from, int to)
			{
				_from = from;
				_to = to;
			}
		}

		public static void Qsort(com.db4o.foundation.QuickSortable4 sortable)
		{
			com.db4o.foundation.Stack4 stack = new com.db4o.foundation.Stack4();
			AddRange(stack, 0, sortable.Size() - 1);
			Qsort(sortable, stack);
		}

		private static void Qsort(com.db4o.foundation.QuickSortable4 sortable, com.db4o.foundation.Stack4
			 stack)
		{
			while (!stack.IsEmpty())
			{
				com.db4o.foundation.Algorithms4.Range range = (com.db4o.foundation.Algorithms4.Range
					)stack.Peek();
				stack.Pop();
				int from = range._from;
				int to = range._to;
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
				AddRange(stack, from, right - 1);
				AddRange(stack, right + 1, to);
			}
		}

		private static void AddRange(com.db4o.foundation.Stack4 stack, int from, int to)
		{
			if (to - from < 1)
			{
				return;
			}
			stack.Push(new com.db4o.foundation.Algorithms4.Range(from, to));
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
