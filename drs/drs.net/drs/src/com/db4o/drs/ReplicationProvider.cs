namespace com.db4o.drs
{
	/// <summary>Facade for persistence systems that provide replication support.</summary>
	/// <remarks>
	/// Facade for persistence systems that provide replication support.
	/// Interacts with another ReplicationProvider and a  ReplicationSession
	/// to allows replication of objects between two ReplicationProviders.
	/// <p/>
	/// <p/> To create an instance of this class, use the methods of
	/// <see cref="com.db4o.drs.Replication">com.db4o.drs.Replication</see>
	/// .
	/// </remarks>
	/// <author>Albert Kwan</author>
	/// <author>Klaus Wuestefeld</author>
	/// <version>1.2</version>
	/// <seealso cref="com.db4o.drs.ReplicationSession">com.db4o.drs.ReplicationSession</seealso>
	/// <seealso cref="com.db4o.drs.Replication">com.db4o.drs.Replication</seealso>
	/// <since>dRS 1.0</since>
	public interface ReplicationProvider
	{
		/// <summary>Returns newly created objects and changed objects since last replication with the opposite provider.
		/// 	</summary>
		/// <remarks>Returns newly created objects and changed objects since last replication with the opposite provider.
		/// 	</remarks>
		/// <returns>newly created objects and changed objects since last replication with the opposite provider.
		/// 	</returns>
		com.db4o.ObjectSet ObjectsChangedSinceLastReplication();

		/// <summary>Returns newly created objects and changed objects since last replication with the opposite provider.
		/// 	</summary>
		/// <remarks>Returns newly created objects and changed objects since last replication with the opposite provider.
		/// 	</remarks>
		/// <param name="clazz">the type of objects interested</param>
		/// <returns>newly created objects and changed objects of the type specified in the clazz parameter since last replication
		/// 	</returns>
		com.db4o.ObjectSet ObjectsChangedSinceLastReplication(System.Type clazz);
	}
}
