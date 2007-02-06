namespace com.db4o.@internal
{
	/// <exclude></exclude>
	public class VirtualAttributes : com.db4o.foundation.ShallowClone
	{
		public com.db4o.ext.Db4oDatabase i_database;

		public long i_version;

		public long i_uuid;

		public virtual object ShallowClone()
		{
			com.db4o.@internal.VirtualAttributes va = new com.db4o.@internal.VirtualAttributes
				();
			va.i_database = i_database;
			va.i_version = i_version;
			va.i_uuid = i_uuid;
			return va;
		}

		internal virtual bool SuppliesUUID()
		{
			return i_database != null && i_uuid != 0;
		}
	}
}
