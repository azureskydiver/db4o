namespace com.db4o.drs.inside
{
	/// <summary>A default implementation of ConflictResolver.</summary>
	/// <remarks>
	/// A default implementation of ConflictResolver. In case of a conflict
	/// a
	/// <see cref="com.db4o.drs.ReplicationConflictException">com.db4o.drs.ReplicationConflictException
	/// 	</see>
	/// is thrown.
	/// </remarks>
	/// <author>Albert Kwan</author>
	/// <author>Carl Rosenberger</author>
	/// <author>Klaus Wuestefeld</author>
	/// <version>1.0</version>
	/// <since>dRS 1.0</since>
	public class DefaultReplicationEventListener : com.db4o.drs.ReplicationEventListener
	{
		public virtual void OnReplicate(com.db4o.drs.ReplicationEvent e)
		{
		}
	}
}
