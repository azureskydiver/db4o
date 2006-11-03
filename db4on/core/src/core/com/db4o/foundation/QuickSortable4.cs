namespace com.db4o.foundation
{
	/// <exclude></exclude>
	public interface QuickSortable4
	{
		int Size();

		int Compare(int leftIndex, int rightIndex);

		void Swap(int leftIndex, int rightIndex);
	}
}
