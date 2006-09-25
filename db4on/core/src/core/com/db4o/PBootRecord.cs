namespace com.db4o
{
	/// <summary>database boot record.</summary>
	/// <remarks>
	/// database boot record. Responsible for ID generation, version generation and
	/// holding a reference to the Db4oDatabase object of the ObjectContainer
	/// </remarks>
	/// <exclude></exclude>
	/// <persistent></persistent>
	public class PBootRecord : com.db4o.P1Object, com.db4o.Db4oTypeImpl, com.db4o.Internal4
	{
		[com.db4o.Transient]
		public com.db4o.YapFile i_stream;

		public com.db4o.ext.Db4oDatabase i_db;

		public long i_versionGenerator;

		public int i_generateVersionNumbers;

		public int i_generateUUIDs;

		[com.db4o.Transient]
		private bool i_dirty;

		public com.db4o.MetaIndex i_uuidMetaIndex;

		public PBootRecord()
		{
		}

		public override int ActivationDepth()
		{
			return int.MaxValue;
		}

		public virtual void Init()
		{
			i_uuidMetaIndex = new com.db4o.MetaIndex();
			i_dirty = true;
		}

		public virtual com.db4o.MetaIndex GetUUIDMetaIndex()
		{
			return i_uuidMetaIndex;
		}

		public virtual void SetDirty()
		{
			i_dirty = true;
		}

		public override void Store(int a_depth)
		{
			if (i_dirty)
			{
				i_stream.ShowInternalClasses(true);
				base.Store(a_depth);
				i_stream.ShowInternalClasses(false);
			}
			i_dirty = false;
		}
	}
}
