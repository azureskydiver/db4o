namespace com.db4o
{
	internal class Sessions : com.db4o.foundation.Collection4
	{
		internal virtual void ForEach(com.db4o.foundation.Visitor4 visitor)
		{
			lock (com.db4o.Db4o.Lock)
			{
				com.db4o.foundation.Iterator4 i = Iterator();
				while (i.HasNext())
				{
					visitor.Visit(i.Next());
				}
			}
		}

		internal virtual com.db4o.ObjectContainer Open(string databaseFileName)
		{
			lock (com.db4o.Db4o.Lock)
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
					oc = new com.db4o.YapRandomAccessFile(newSession);
				}
				catch (com.db4o.LongJumpOutException e)
				{
					throw e;
				}
				catch (com.db4o.ext.DatabaseFileLockedException e)
				{
					throw e;
				}
				catch (com.db4o.ext.ObjectNotStorableException e)
				{
					throw e;
				}
				catch (com.db4o.UserException eu)
				{
					com.db4o.inside.Exceptions4.ThrowRuntimeException(eu.errCode, eu.errMsg);
				}
				catch (System.Exception t)
				{
					com.db4o.Messages.LogErr(com.db4o.Db4o.i_config, 4, databaseFileName, t);
					return null;
				}
				if (oc != null)
				{
					newSession.i_stream = (com.db4o.YapStream)oc;
					Add(newSession);
					com.db4o.Platform4.PostOpen(oc);
					com.db4o.Messages.LogMsg(com.db4o.Db4o.i_config, 5, databaseFileName);
				}
				return oc;
			}
		}

		public override object Remove(object obj)
		{
			lock (com.db4o.Db4o.Lock)
			{
				return base.Remove(obj);
			}
		}
	}
}
