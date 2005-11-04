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
		internal com.db4o.YapFile i_stream;

		public com.db4o.ext.Db4oDatabase i_db;

		public long i_uuidGenerator;

		public long i_versionGenerator;

		public int i_generateVersionNumbers;

		public int i_generateUUIDs;

		[com.db4o.Transient]
		private bool i_dirty;

		public com.db4o.MetaIndex i_uuidMetaIndex;

		public PBootRecord()
		{
		}

		public override int activationDepth()
		{
			return int.MaxValue;
		}

		internal virtual void init(com.db4o.Config4Impl a_config)
		{
			i_db = com.db4o.ext.Db4oDatabase.generate();
			i_uuidGenerator = com.db4o.Unobfuscated.randomLong();
			i_uuidMetaIndex = new com.db4o.MetaIndex();
			initConfig(a_config);
			i_dirty = true;
		}

		internal virtual bool initConfig(com.db4o.Config4Impl a_config)
		{
			bool modified = false;
			if (i_generateVersionNumbers != a_config.i_generateVersionNumbers)
			{
				i_generateVersionNumbers = a_config.i_generateVersionNumbers;
				modified = true;
			}
			if (i_generateUUIDs != a_config.i_generateUUIDs)
			{
				i_generateUUIDs = a_config.i_generateUUIDs;
				modified = true;
			}
			return modified;
		}

		internal virtual com.db4o.MetaIndex getUUIDMetaIndex()
		{
			if (i_uuidMetaIndex == null)
			{
				i_uuidMetaIndex = new com.db4o.MetaIndex();
				com.db4o.Transaction systemTrans = i_stream.getSystemTransaction();
				i_stream.showInternalClasses(true);
				i_stream.setInternal(systemTrans, this, false);
				i_stream.showInternalClasses(false);
				systemTrans.commit();
			}
			return i_uuidMetaIndex;
		}

		internal virtual long newUUID()
		{
			i_dirty = true;
			return i_uuidGenerator++;
		}

		public virtual void setDirty()
		{
			i_dirty = true;
		}

		public override void store(int a_depth)
		{
			if (i_dirty)
			{
				i_versionGenerator++;
				i_stream.showInternalClasses(true);
				base.store(a_depth);
				i_stream.showInternalClasses(false);
			}
			i_dirty = false;
		}

		internal virtual long version()
		{
			i_dirty = true;
			return i_versionGenerator;
		}
	}
}
