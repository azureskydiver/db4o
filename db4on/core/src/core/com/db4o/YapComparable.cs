
namespace com.db4o
{
	/// <exclude></exclude>
	public interface YapComparable
	{
		com.db4o.YapComparable prepareComparison(object obj);

		int compareTo(object obj);

		bool isEqual(object obj);

		bool isGreater(object obj);

		bool isSmaller(object obj);
	}
}
