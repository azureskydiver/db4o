namespace com.db4o.query
{
	/// <summary>
	/// Comparator for sorting queries on JDKs where
	/// java.util.Comparator is not available.
	/// </summary>
	/// <remarks>
	/// Comparator for sorting queries on JDKs where
	/// java.util.Comparator is not available.
	/// </remarks>
	public interface QueryComparator : j4o.io.Serializable
	{
		/// <summary>Implement to compare two arguments for sorting.</summary>
		/// <remarks>
		/// Implement to compare two arguments for sorting.
		/// Return a negative value, zero, or a positive value if
		/// the first argument is smaller, equal or greater than
		/// the second.
		/// </remarks>
		int compare(object first, object second);
	}
}
