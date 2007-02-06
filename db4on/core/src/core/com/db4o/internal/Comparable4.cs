namespace com.db4o.@internal
{
	/// <exclude></exclude>
	/// <renameto>com.db4o.internal.Comparable4</renameto>
	public interface Comparable4
	{
		com.db4o.@internal.Comparable4 PrepareComparison(object obj);

		int CompareTo(object obj);

		bool IsEqual(object obj);

		bool IsGreater(object obj);

		bool IsSmaller(object obj);

		object Current();
	}
}
