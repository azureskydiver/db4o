namespace com.db4o
{
	internal class VirtualAttributes : j4o.lang.Cloneable
	{
		internal com.db4o.ext.Db4oDatabase i_database;

		internal long i_version;

		internal long i_uuid;

		internal virtual com.db4o.VirtualAttributes shallowClone()
		{
			try
			{
				return (com.db4o.VirtualAttributes)j4o.lang.JavaSystem.clone(this);
			}
			catch (System.Exception e)
			{
			}
			return null;
		}
	}
}
