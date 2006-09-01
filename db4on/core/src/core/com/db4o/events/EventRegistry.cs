namespace com.db4o.events
{
	public delegate void QueryEventHandler(object sender, QueryEventArgs args);

	public delegate void CancellableObjectEventHandler(object sender, CancellableObjectEventArgs
		 args);

	public delegate void ObjectEventHandler(object sender, ObjectEventArgs args);

	/// <summary>
	/// Provides a way to register event handlers for specific
	/// <see cref="ObjectContainer">ObjectContainer</see>
	/// events.
	/// </summary>
	/// <seealso cref="com.db4o.events.EventRegistryFactory">com.db4o.events.EventRegistryFactory
	/// 	</seealso>
	public interface EventRegistry
	{
		/// <summary>
		/// Receives
		/// <see cref="com.db4o.events.QueryEventArgs">com.db4o.events.QueryEventArgs</see>
		/// .
		/// </summary>
		/// <returns></returns>
		event QueryEventHandler QueryStarted;

		/// <summary>
		/// Receives
		/// <see cref="com.db4o.events.QueryEventArgs">com.db4o.events.QueryEventArgs</see>
		/// .
		/// </summary>
		/// <returns></returns>
		event QueryEventHandler QueryFinished;

		/// <summary>
		/// Receives
		/// <see cref="com.db4o.events.CancellableObjectEventArgs">com.db4o.events.CancellableObjectEventArgs
		/// 	</see>
		/// .
		/// </summary>
		/// <returns></returns>
		event CancellableObjectEventHandler Creating;

		/// <summary>
		/// Receives
		/// <see cref="com.db4o.events.CancellableObjectEventArgs">com.db4o.events.CancellableObjectEventArgs
		/// 	</see>
		/// .
		/// </summary>
		/// <returns></returns>
		event CancellableObjectEventHandler Activating;

		/// <summary>
		/// Receives
		/// <see cref="com.db4o.events.CancellableObjectEventArgs">com.db4o.events.CancellableObjectEventArgs
		/// 	</see>
		/// </summary>
		/// <returns></returns>
		event CancellableObjectEventHandler Updating;

		/// <summary>
		/// Receives
		/// <see cref="com.db4o.events.CancellableObjectEventArgs">com.db4o.events.CancellableObjectEventArgs
		/// 	</see>
		/// </summary>
		/// <returns></returns>
		event CancellableObjectEventHandler Deleting;

		/// <summary>
		/// Receives
		/// <see cref="com.db4o.events.CancellableObjectEventArgs">com.db4o.events.CancellableObjectEventArgs
		/// 	</see>
		/// </summary>
		/// <returns></returns>
		event CancellableObjectEventHandler Deactivating;

		/// <summary>
		/// Receives
		/// <see cref="com.db4o.events.ObjectEventArgs">com.db4o.events.ObjectEventArgs</see>
		/// .
		/// </summary>
		/// <returns></returns>
		event ObjectEventHandler Activated;

		/// <summary>
		/// Receives
		/// <see cref="com.db4o.events.ObjectEventArgs">com.db4o.events.ObjectEventArgs</see>
		/// .
		/// </summary>
		/// <returns></returns>
		event ObjectEventHandler Created;

		/// <summary>
		/// Receives
		/// <see cref="com.db4o.events.ObjectEventArgs">com.db4o.events.ObjectEventArgs</see>
		/// </summary>
		/// <returns></returns>
		event ObjectEventHandler Updated;

		/// <summary>
		/// Receives
		/// <see cref="com.db4o.events.ObjectEventArgs">com.db4o.events.ObjectEventArgs</see>
		/// </summary>
		/// <returns></returns>
		event ObjectEventHandler Deleted;

		/// <summary>
		/// Receives
		/// <see cref="com.db4o.events.ObjectEventArgs">com.db4o.events.ObjectEventArgs</see>
		/// </summary>
		/// <returns></returns>
		event ObjectEventHandler Deactivated;
	}
}
