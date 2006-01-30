namespace com.db4o
{
	/// <exclude></exclude>
	public class VirtualAttributes : j4o.lang.Cloneable
	{
		public com.db4o.ext.Db4oDatabase i_database;

		public long i_version;

		public long i_uuid;

		public virtual com.db4o.VirtualAttributes shallowClone()
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
