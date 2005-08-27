
namespace com.db4o.ext
{
	/// <summary>
	/// this Exception is thrown during any of the db4o open calls
	/// if the database file is locked by another process.
	/// </summary>
	/// <remarks>
	/// this Exception is thrown during any of the db4o open calls
	/// if the database file is locked by another process.
	/// </remarks>
	/// <seealso cref="com.db4o.Db4o.openFile">com.db4o.Db4o.openFile</seealso>
	public class DatabaseFileLockedException : j4o.lang.RuntimeException
	{
	}
}
