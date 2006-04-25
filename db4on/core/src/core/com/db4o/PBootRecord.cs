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

		public long i_versionGenerator;

		public int i_generateVersionNumbers;

		public int i_generateUUIDs;

		[com.db4o.Transient]
		private bool i_dirty;

		public com.db4o.MetaIndex i_uuidMetaIndex;

		[com.db4o.Transient]
		private com.db4o.foundation.TimeStampIdGenerator _versionTimeGenerator;

		public PBootRecord()
		{
		}

		public override int activationDepth()
		{
			return int.MaxValue;
		}

		private void createVersionTimeGenerator()
		{
			if (_versionTimeGenerator == null)
			{
				_versionTimeGenerator = new com.db4o.foundation.TimeStampIdGenerator(i_versionGenerator
					);
			}
		}

		internal virtual void init(com.db4o.Config4Impl a_config)
		{
			i_db = com.db4o.ext.Db4oDatabase.generate();
			i_uuidMetaIndex = new com.db4o.MetaIndex();
			initConfig(a_config);
			i_dirty = true;
		}

		internal virtual bool initConfig(com.db4o.Config4Impl a_config)
		{
			bool modified = false;
			if (i_generateVersionNumbers != a_config.generateVersionNumbers())
			{
				i_generateVersionNumbers = a_config.generateVersionNumbers();
				modified = true;
			}
			if (i_generateUUIDs != a_config.generateUUIDs())
			{
				i_generateUUIDs = a_config.generateUUIDs();
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
			return nextVersion();
		}

		public virtual void raiseVersion(long a_minimumVersion)
		{
			if (i_versionGenerator < a_minimumVersion)
			{
				createVersionTimeGenerator();
				_versionTimeGenerator.setMinimumNext(a_minimumVersion);
				i_versionGenerator = a_minimumVersion;
				setDirty();
				store(1);
			}
		}

		public virtual void setDirty()
		{
			i_dirty = true;
		}

		public override void store(int a_depth)
		{
			if (i_dirty)
			{
				createVersionTimeGenerator();
				i_versionGenerator = _versionTimeGenerator.generate();
				i_stream.showInternalClasses(true);
				base.store(a_depth);
				i_stream.showInternalClasses(false);
			}
			i_dirty = false;
		}

		internal virtual long nextVersion()
		{
			i_dirty = true;
			createVersionTimeGenerator();
			i_versionGenerator = _versionTimeGenerator.generate();
			return i_versionGenerator;
		}

		internal virtual long currentVersion()
		{
			return i_versionGenerator;
		}
	}
}
