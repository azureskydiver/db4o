/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

using com.db4o.query;
using com.db4o.events;

namespace com.db4o.events.impl
{
	internal class EventPlatform
	{
		public static void TriggerQueryEvent(QueryEventHandler e, Query q)
		{
			e.Invoke(q, new QueryEventArgs(q));
		}

		public static bool TriggerCancellableObjectEventArgs(CancellableObjectEventHandler e, object o)
		{
			CancellableObjectEventArgs coea = new CancellableObjectEventArgs(o);
			e.Invoke(o, coea);
			return !coea.IsCancelled();
		}

		public static void TriggerObjectEvent(ObjectEventHandler e, object o)
		{
			e.Invoke(o, new ObjectEventArgs(o));
		}
	}
}
