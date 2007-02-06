namespace com.db4o.@internal
{
	public class Sessions : com.db4o.foundation.Collection4
	{
		private static readonly com.db4o.@internal.Sessions _instance = new com.db4o.@internal.Sessions
			();

		private Sessions()
		{
		}

		public virtual void ForEach(com.db4o.foundation.Visitor4 visitor)
		{
			lock (com.db4o.@internal.Global4.Lock)
			{
				System.Collections.IEnumerator i = GetEnumerator();
				while (i.MoveNext())
				{
					visitor.Visit(i.Current);
				}
			}
		}

		public static com.db4o.ObjectContainer Open(com.db4o.config.Configuration config, 
			string databaseFileName)
		{
			return _instance.OpenSession(config, databaseFileName);
		}

		private com.db4o.ObjectContainer OpenSession(com.db4o.config.Configuration config
			, string databaseFileName)
		{
			lock (com.db4o.@internal.Global4.Lock)
			{
				com.db4o.ObjectContainer oc = null;
				com.db4o.@internal.Session newSession = new com.db4o.@internal.Session(databaseFileName
					);
				com.db4o.@internal.Session oldSession = (com.db4o.@internal.Session)Get(newSession
					);
				if (oldSession != null)
				{
					oc = oldSession.SubSequentOpen();
					if (oc == null)
					{
						Remove(oldSession);
					}
					return oc;
				}
				try
				{
					oc = new com.db4o.@internal.IoAdaptedObjectContainer(config, newSession);
				}
				catch (com.db4o.ext.DatabaseFileLockedException e)
				{
					throw;
				}
				catch (com.db4o.ext.ObjectNotStorableException e)
				{
					throw;
				}
				catch (com.db4o.ext.Db4oException e)
				{
					throw;
				}
				catch (System.Exception t)
				{
					com.db4o.@internal.Messages.LogErr(com.db4o.Db4o.Configure(), 4, databaseFileName
						, t);
					return null;
				}
				newSession.i_stream = (com.db4o.@internal.ObjectContainerBase)oc;
				Add(newSession);
				com.db4o.@internal.Platform4.PostOpen(oc);
				com.db4o.@internal.Messages.LogMsg(com.db4o.Db4o.Configure(), 5, databaseFileName
					);
				return oc;
			}
		}

		public override object Remove(object obj)
		{
			lock (com.db4o.@internal.Global4.Lock)
			{
				return base.Remove(obj);
			}
		}

		internal static void ForEachSession(com.db4o.foundation.Visitor4 visitor)
		{
			_instance.ForEach(visitor);
		}

		internal static void SessionStopped(com.db4o.@internal.Session a_session)
		{
			_instance.Remove(a_session);
		}
	}
}
