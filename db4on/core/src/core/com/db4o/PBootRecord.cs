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

		public override int ActivationDepth()
		{
			return int.MaxValue;
		}

		private void CreateVersionTimeGenerator()
		{
			if (_versionTimeGenerator == null)
			{
				_versionTimeGenerator = new com.db4o.foundation.TimeStampIdGenerator(i_versionGenerator
					);
			}
		}

		internal virtual void Init(com.db4o.Config4Impl a_config)
		{
			i_db = com.db4o.ext.Db4oDatabase.Generate();
			i_uuidMetaIndex = new com.db4o.MetaIndex();
			InitConfig(a_config);
			i_dirty = true;
		}

		internal virtual bool InitConfig(com.db4o.Config4Impl a_config)
		{
			bool modified = false;
			if (i_generateVersionNumbers != a_config.GenerateVersionNumbers())
			{
				i_generateVersionNumbers = a_config.GenerateVersionNumbers();
				modified = true;
			}
			if (i_generateUUIDs != a_config.GenerateUUIDs())
			{
				i_generateUUIDs = a_config.GenerateUUIDs();
				modified = true;
			}
			return modified;
		}

		internal virtual com.db4o.MetaIndex GetUUIDMetaIndex()
		{
			if (i_uuidMetaIndex == null)
			{
				i_uuidMetaIndex = new com.db4o.MetaIndex();
				com.db4o.Transaction systemTrans = i_stream.GetSystemTransaction();
				i_stream.ShowInternalClasses(true);
				i_stream.SetInternal(systemTrans, this, false);
				i_stream.ShowInternalClasses(false);
				systemTrans.Commit();
			}
			return i_uuidMetaIndex;
		}

		internal virtual long NewUUID()
		{
			return NextVersion();
		}

		public virtual void RaiseVersion(long a_minimumVersion)
		{
			if (i_versionGenerator < a_minimumVersion)
			{
				CreateVersionTimeGenerator();
				_versionTimeGenerator.SetMinimumNext(a_minimumVersion);
				i_versionGenerator = a_minimumVersion;
				SetDirty();
				Store(1);
			}
		}

		public virtual void SetDirty()
		{
			i_dirty = true;
		}

		public override void Store(int a_depth)
		{
			if (i_dirty)
			{
				CreateVersionTimeGenerator();
				i_versionGenerator = _versionTimeGenerator.Generate();
				i_stream.ShowInternalClasses(true);
				base.Store(a_depth);
				i_stream.ShowInternalClasses(false);
			}
			i_dirty = false;
		}

		internal virtual long NextVersion()
		{
			i_dirty = true;
			CreateVersionTimeGenerator();
			i_versionGenerator = _versionTimeGenerator.Generate();
			return i_versionGenerator;
		}

		internal virtual long CurrentVersion()
		{
			return i_versionGenerator;
		}
	}
}
