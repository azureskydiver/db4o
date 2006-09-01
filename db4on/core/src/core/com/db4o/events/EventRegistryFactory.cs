namespace com.db4o.events
{
	/// <summary>
	/// Provides an interface for getting an
	/// <see cref="com.db4o.events.EventRegistry">com.db4o.events.EventRegistry</see>
	/// from an
	/// <see cref="com.db4o.ObjectContainer">com.db4o.ObjectContainer</see>
	/// .
	/// </summary>
	public class EventRegistryFactory
	{
		/// <summary>
		/// Returns an
		/// <see cref="com.db4o.events.EventRegistry">com.db4o.events.EventRegistry</see>
		/// for registering events with the specified container.
		/// </summary>
		public static com.db4o.events.EventRegistry ForObjectContainer(com.db4o.ObjectContainer
			 container)
		{
			if (null == container)
			{
				throw new System.ArgumentNullException("container");
			}
			com.db4o.YapStream stream = ((com.db4o.YapStream)container);
			com.db4o.inside.callbacks.Callbacks callbacks = stream.Callbacks();
			if (callbacks is com.db4o.events.EventRegistry)
			{
				return (com.db4o.events.EventRegistry)callbacks;
			}
			if (callbacks is com.db4o.inside.callbacks.NullCallbacks)
			{
				com.db4o.events.impl.EventRegistryImpl impl = new com.db4o.events.impl.EventRegistryImpl
					();
				stream.Callbacks(impl);
				return impl;
			}
			throw new System.ArgumentException("container callbacks already in use");
		}
	}
}
