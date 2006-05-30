namespace com.db4o
{
	/// <exclude></exclude>
	public interface YapComparable
	{
		com.db4o.YapComparable PrepareComparison(object obj);

		int CompareTo(object obj);

		bool IsEqual(object obj);

		bool IsGreater(object obj);

		bool IsSmaller(object obj);

		object Current();
	}
}
