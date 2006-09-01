namespace com.db4o.events
{
	/// <summary>
	/// Arguments for
	/// <see cref="com.db4o.query.Query">com.db4o.query.Query</see>
	/// related events.
	/// </summary>
	/// <seealso cref="com.db4o.events.EventRegistry">com.db4o.events.EventRegistry</seealso>
	public class QueryEventArgs : com.db4o.events.ObjectEventArgs
	{
		public QueryEventArgs(com.db4o.query.Query q) : base(q)
		{
		}

		/// <summary>
		/// The
		/// <see cref="com.db4o.query.Query">com.db4o.query.Query</see>
		/// which triggered the event.
		/// </summary>
		public virtual com.db4o.query.Query Query
		{
			get
			{
				return (com.db4o.query.Query)Object;
			}
		}
	}
}
