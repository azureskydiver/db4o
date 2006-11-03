namespace com.db4o
{
	internal class Sessions : com.db4o.foundation.Collection4
	{
		internal virtual void ForEach(com.db4o.foundation.Visitor4 visitor)
		{
			lock (com.db4o.inside.Global4.Lock)
			{
				System.Collections.IEnumerator i = GetEnumerator();
				while (i.MoveNext())
				{
					visitor.Visit(i.Current);
				}
			}
		}

		internal virtual com.db4o.ObjectContainer Open(com.db4o.config.Configuration config
			, string databaseFileName)
		{
			lock (com.db4o.inside.Global4.Lock)
			{
				com.db4o.ObjectContainer oc = null;
				com.db4o.Session newSession = new com.db4o.Session(databaseFileName);
				com.db4o.Session oldSession = (com.db4o.Session)Get(newSession);
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
					oc = new com.db4o.YapRandomAccessFile(config, newSession);
				}
				catch (com.db4o.ext.DatabaseFileLockedException e)
				{
					throw;
				}
				catch (com.db4o.ext.ObjectNotStorableException e)
				{
					throw;
				}
				catch (System.Exception t)
				{
					com.db4o.Messages.LogErr(com.db4o.Db4o.i_config, 4, databaseFileName, t);
					return null;
				}
				newSession.i_stream = (com.db4o.YapStream)oc;
				Add(newSession);
				com.db4o.Platform4.PostOpen(oc);
				com.db4o.Messages.LogMsg(com.db4o.Db4o.i_config, 5, databaseFileName);
				return oc;
			}
		}

		public override object Remove(object obj)
		{
			lock (com.db4o.inside.Global4.Lock)
			{
				return base.Remove(obj);
			}
		}
	}
}
