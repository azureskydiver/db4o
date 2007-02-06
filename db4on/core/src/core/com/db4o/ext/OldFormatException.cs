namespace com.db4o.ext
{
	/// <summary>An old file was detected and could not be open.</summary>
	/// <remarks>An old file was detected and could not be open.</remarks>
	[System.Serializable]
	public class OldFormatException : com.db4o.ext.Db4oException
	{
		public OldFormatException() : base(com.db4o.@internal.Messages.OLD_DATABASE_FORMAT
			)
		{
		}
	}
}
