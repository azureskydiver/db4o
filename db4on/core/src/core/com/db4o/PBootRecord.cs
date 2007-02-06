namespace com.db4o
{
	/// <summary>Old database boot record class.</summary>
	/// <remarks>
	/// Old database boot record class.
	/// This class was responsible for storing the last timestamp id,
	/// for holding a reference to the Db4oDatabase object of the
	/// ObjectContainer and for holding on to the UUID index.
	/// This class is no longer needed with the change to the new
	/// fileheader. It still has to stay here to be able to read
	/// old databases.
	/// </remarks>
	/// <exclude></exclude>
	/// <persistent></persistent>
	public class PBootRecord : com.db4o.P1Object, com.db4o.@internal.Db4oTypeImpl, com.db4o.Internal4
	{
		public com.db4o.ext.Db4oDatabase i_db;

		public long i_versionGenerator;

		public com.db4o.MetaIndex i_uuidMetaIndex;

		public override int ActivationDepth()
		{
			return int.MaxValue;
		}

		public virtual com.db4o.MetaIndex GetUUIDMetaIndex()
		{
			return i_uuidMetaIndex;
		}

		public virtual void Write(com.db4o.@internal.LocalObjectContainer file)
		{
			com.db4o.@internal.SystemData systemData = file.SystemData();
			i_versionGenerator = systemData.LastTimeStampID();
			i_db = systemData.Identity();
			file.ShowInternalClasses(true);
			Store(2);
			file.ShowInternalClasses(false);
		}
	}
}
