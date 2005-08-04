namespace com.db4o.config
{
	/// <summary>allows special comparison behaviour during query evaluation.</summary>
	/// <remarks>
	/// allows special comparison behaviour during query evaluation.
	/// <br /><br />db4o will use the Object returned by the <code>compare()</code>
	/// method for all query comparisons.
	/// </remarks>
	public interface Compare
	{
		/// <summary>return the Object to be compared during query evaluation.</summary>
		/// <remarks>return the Object to be compared during query evaluation.</remarks>
		object compare();
	}
}
