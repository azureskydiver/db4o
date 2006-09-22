namespace com.db4o.foundation
{
	/// <exclude></exclude>
	public interface Comparison4
	{
		/// <summary>
		/// Returns negative number if x < y
		/// Returns zero if x == y
		/// Returns positive number if x > y
		/// </summary>
		int Compare(object x, object y);
	}
}
