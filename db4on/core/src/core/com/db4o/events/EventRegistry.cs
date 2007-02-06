namespace com.db4o.events
{
	public delegate void QueryEventHandler(object sender, com.db4o.events.QueryEventArgs
		 args);

	public delegate void CancellableObjectEventHandler(object sender, com.db4o.events.CancellableObjectEventArgs
		 args);

	public delegate void ObjectEventHandler(object sender, com.db4o.events.ObjectEventArgs
		 args);

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
		event com.db4o.events.QueryEventHandler QueryStarted;

		/// <summary>
		/// Receives
		/// <see cref="com.db4o.events.QueryEventArgs">com.db4o.events.QueryEventArgs</see>
		/// .
		/// </summary>
		/// <returns></returns>
		event com.db4o.events.QueryEventHandler QueryFinished;

		/// <summary>
		/// Receives
		/// <see cref="com.db4o.events.CancellableObjectEventArgs">com.db4o.events.CancellableObjectEventArgs
		/// 	</see>
		/// .
		/// </summary>
		/// <returns></returns>
		event com.db4o.events.CancellableObjectEventHandler Creating;

		/// <summary>
		/// Receives
		/// <see cref="com.db4o.events.CancellableObjectEventArgs">com.db4o.events.CancellableObjectEventArgs
		/// 	</see>
		/// .
		/// </summary>
		/// <returns></returns>
		event com.db4o.events.CancellableObjectEventHandler Activating;

		/// <summary>
		/// Receives
		/// <see cref="com.db4o.events.CancellableObjectEventArgs">com.db4o.events.CancellableObjectEventArgs
		/// 	</see>
		/// </summary>
		/// <returns></returns>
		event com.db4o.events.CancellableObjectEventHandler Updating;

		/// <summary>
		/// Receives
		/// <see cref="com.db4o.events.CancellableObjectEventArgs">com.db4o.events.CancellableObjectEventArgs
		/// 	</see>
		/// </summary>
		/// <returns></returns>
		event com.db4o.events.CancellableObjectEventHandler Deleting;

		/// <summary>
		/// Receives
		/// <see cref="com.db4o.events.CancellableObjectEventArgs">com.db4o.events.CancellableObjectEventArgs
		/// 	</see>
		/// </summary>
		/// <returns></returns>
		event com.db4o.events.CancellableObjectEventHandler Deactivating;

		/// <summary>
		/// Receives
		/// <see cref="com.db4o.events.ObjectEventArgs">com.db4o.events.ObjectEventArgs</see>
		/// .
		/// </summary>
		/// <returns></returns>
		event com.db4o.events.ObjectEventHandler Activated;

		/// <summary>
		/// Receives
		/// <see cref="com.db4o.events.ObjectEventArgs">com.db4o.events.ObjectEventArgs</see>
		/// .
		/// </summary>
		/// <returns></returns>
		event com.db4o.events.ObjectEventHandler Created;

		/// <summary>
		/// Receives
		/// <see cref="com.db4o.events.ObjectEventArgs">com.db4o.events.ObjectEventArgs</see>
		/// </summary>
		/// <returns></returns>
		event com.db4o.events.ObjectEventHandler Updated;

		/// <summary>
		/// Receives
		/// <see cref="com.db4o.events.ObjectEventArgs">com.db4o.events.ObjectEventArgs</see>
		/// </summary>
		/// <returns></returns>
		event com.db4o.events.ObjectEventHandler Deleted;

		/// <summary>
		/// Receives
		/// <see cref="com.db4o.events.ObjectEventArgs">com.db4o.events.ObjectEventArgs</see>
		/// </summary>
		/// <returns></returns>
		event com.db4o.events.ObjectEventHandler Deactivated;
	}
}
