/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

using Db4objects.Db4o.Events;
using Db4objects.Db4o.Query;

namespace Db4objects.Db4o.Events
{
	/// <summary>
	/// Arguments for
	/// <see cref="IQuery">IQuery</see>
	/// related events.
	/// </summary>
	/// <seealso cref="IEventRegistry">IEventRegistry</seealso>
	public class QueryEventArgs : ObjectEventArgs
	{
		public QueryEventArgs(IQuery q) : base(q)
		{
		}

		/// <summary>
		/// The
		/// <see cref="IQuery">IQuery</see>
		/// which triggered the event.
		/// </summary>
		public virtual IQuery Query
		{
			get
			{
				return (IQuery)Object;
			}
		}
	}
}
