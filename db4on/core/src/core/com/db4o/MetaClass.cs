namespace com.db4o
{
	/// <summary>
	/// Class metadata to be stored to the database file
	/// Don't obfuscate.
	/// </summary>
	/// <remarks>
	/// Class metadata to be stored to the database file
	/// Don't obfuscate.
	/// </remarks>
	/// <exclude></exclude>
	/// <persistent></persistent>
	public class MetaClass : com.db4o.Internal4
	{
		/// <summary>persistent field, don't touch</summary>
		public string name;

		/// <summary>persistent field, don't touch</summary>
		public com.db4o.MetaField[] fields;
	}
}
