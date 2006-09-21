namespace com.db4o.drs
{
	/// <summary>Thrown when a conflict occurs and no ReplicationEventListener is specified.
	/// 	</summary>
	/// <remarks>Thrown when a conflict occurs and no ReplicationEventListener is specified.
	/// 	</remarks>
	/// <author>Albert Kwan</author>
	/// <author>Klaus Wuestefeld</author>
	/// <version>1.2</version>
	/// <seealso cref="com.db4o.drs.ReplicationEventListener">com.db4o.drs.ReplicationEventListener
	/// 	</seealso>
	/// <since>dRS 1.2</since>
	public class ReplicationConflictException : com.db4o.ext.Db4oException
	{
		public ReplicationConflictException(string message) : base(message)
		{
		}
	}
}
