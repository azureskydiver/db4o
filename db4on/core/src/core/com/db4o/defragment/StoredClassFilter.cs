namespace com.db4o.defragment
{
	/// <summary>Filter for StoredClass instances.</summary>
	/// <remarks>Filter for StoredClass instances.</remarks>
	public interface StoredClassFilter
	{
		/// <param name="storedClass">StoredClass instance to be checked</param>
		/// <returns>true, if the given StoredClass instance should be accepted, false otherwise.
		/// 	</returns>
		bool Accept(com.db4o.ext.StoredClass storedClass);
	}
}
