namespace com.db4o.@internal
{
	public class ObjectContainerFactory
	{
		public static com.db4o.ObjectContainer OpenObjectContainer(com.db4o.config.Configuration
			 config, string databaseFileName)
		{
			com.db4o.ObjectContainer oc = null;
			try
			{
				oc = new com.db4o.@internal.IoAdaptedObjectContainer(config, databaseFileName);
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
			catch (System.Exception ex)
			{
				com.db4o.@internal.Messages.LogErr(com.db4o.Db4o.Configure(), 4, databaseFileName
					, ex);
				return null;
			}
			com.db4o.@internal.Platform4.PostOpen(oc);
			com.db4o.@internal.Messages.LogMsg(com.db4o.Db4o.Configure(), 5, databaseFileName
				);
			return oc;
		}
	}
}
