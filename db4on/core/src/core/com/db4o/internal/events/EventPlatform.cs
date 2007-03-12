/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

using com.db4o.events;
using com.db4o.ext;

namespace com.db4o.@internal.events
{
	internal class EventPlatform
	{
		public static void TriggerQueryEvent(QueryEventHandler e, com.db4o.query.Query q)
		{
			if (null == e) return;
			e(q, new QueryEventArgs(q));
		}

		public static bool TriggerCancellableObjectEventArgs(CancellableObjectEventHandler e, object o)
		{
			if (null == e) return true;
			CancellableObjectEventArgs coea = new CancellableObjectEventArgs(o);
			e(o, coea);
			return !coea.IsCancelled;
		}

		public static void TriggerObjectEvent(ObjectEventHandler e, object o)
		{
			if (null == e) return;
			e(o, new ObjectEventArgs(o));
		}
		
		public static void TriggerCommitEvent(CommitEventHandler e, ObjectInfoCollection added, ObjectInfoCollection deleted, ObjectInfoCollection updated)
		{
			if (null == e) return;
			e(null, new CommitEventArgs(added, deleted, updated));
		}
		
		public static bool HasListeners(System.Delegate e)
		{
			return null != e;
		}
	}
}
