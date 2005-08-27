namespace com.db4o
{
	/// <summary>The index record that is written to the database file.</summary>
	/// <remarks>
	/// The index record that is written to the database file.
	/// Don't obfuscate.
	/// </remarks>
	/// <exclude></exclude>
	/// <persistent></persistent>
	public class MetaIndex : com.db4o.Internal4
	{
		public int indexAddress;

		public int indexEntries;

		public int indexLength;

		public int patchAddress;

		public int patchEntries;

		public int patchLength;
	}
}
