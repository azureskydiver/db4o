namespace com.db4o
{
	/// <summary>
	/// Renaming actions are stored to the database file to make
	/// sure that they are only performed once.
	/// </summary>
	/// <remarks>
	/// Renaming actions are stored to the database file to make
	/// sure that they are only performed once.
	/// </remarks>
	/// <exclude></exclude>
	/// <persistent></persistent>
	public sealed class Rename : com.db4o.Internal4
	{
		public string rClass;

		public string rFrom;

		public string rTo;

		public Rename()
		{
		}

		public Rename(string aClass, string aFrom, string aTo)
		{
			rClass = aClass;
			rFrom = aFrom;
			rTo = aTo;
		}
	}
}
