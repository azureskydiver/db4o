namespace com.db4o.defragment
{
	/// <summary>Configuration for a defragmentation run.</summary>
	/// <remarks>Configuration for a defragmentation run.</remarks>
	/// <seealso cref="com.db4o.defragment.Defragment">com.db4o.defragment.Defragment</seealso>
	public class DefragmentConfig
	{
		public const bool DEBUG = false;

		public static readonly string BACKUP_SUFFIX = "backup";

		private string _origPath;

		private string _backupPath;

		private com.db4o.defragment.ContextIDMapping _mapping;

		private com.db4o.config.Configuration _config;

		private com.db4o.defragment.StoredClassFilter _storedClassFilter = null;

		private bool _forceBackupDelete = false;

		private int _objectCommitFrequency;

		public DefragmentConfig(string origPath) : this(origPath, origPath + "." + BACKUP_SUFFIX
			)
		{
		}

		public DefragmentConfig(string origPath, string backupPath) : this(origPath, backupPath
			, new com.db4o.defragment.TreeIDMapping())
		{
		}

		public DefragmentConfig(string origPath, string backupPath, com.db4o.defragment.ContextIDMapping
			 mapping)
		{
			_origPath = origPath;
			_backupPath = backupPath;
			_mapping = mapping;
		}

		/// <returns>The path to the file to be defragmented.</returns>
		public virtual string OrigPath()
		{
			return _origPath;
		}

		/// <returns>The path to the backup of the original file.</returns>
		public virtual string BackupPath()
		{
			return _backupPath;
		}

		/// <returns>The intermediate mapping used internally. For internal use only.</returns>
		public virtual com.db4o.defragment.ContextIDMapping Mapping()
		{
			return _mapping;
		}

		/// <returns>
		/// The
		/// <see cref="com.db4o.defragment.StoredClassFilter">com.db4o.defragment.StoredClassFilter
		/// 	</see>
		/// used to select stored class extents to
		/// be included into the defragmented file.
		/// </returns>
		public virtual com.db4o.defragment.StoredClassFilter StoredClassFilter()
		{
			return (_storedClassFilter == null ? NULLFILTER : _storedClassFilter);
		}

		/// <param name="storedClassFilter">
		/// The
		/// <see cref="com.db4o.defragment.StoredClassFilter">com.db4o.defragment.StoredClassFilter
		/// 	</see>
		/// used to select stored class extents to
		/// be included into the defragmented file.
		/// </param>
		public virtual void StoredClassFilter(com.db4o.defragment.StoredClassFilter storedClassFilter
			)
		{
			_storedClassFilter = storedClassFilter;
		}

		/// <returns>true, if an existing backup file should be deleted, false otherwise.</returns>
		public virtual bool ForceBackupDelete()
		{
			return _forceBackupDelete;
		}

		/// <param name="forceBackupDelete">true, if an existing backup file should be deleted, false otherwise.
		/// 	</param>
		public virtual void ForceBackupDelete(bool forceBackupDelete)
		{
			_forceBackupDelete = forceBackupDelete;
		}

		/// <returns>
		/// The db4o
		/// <see cref="com.db4o.config.Configuration">Configuration</see>
		/// to be applied
		/// during the defragment process.
		/// </returns>
		public virtual com.db4o.config.Configuration Db4oConfig()
		{
			if (_config == null)
			{
				_config = VanillaDb4oConfig();
			}
			return _config;
		}

		/// <param name="config">
		/// The db4o
		/// <see cref="com.db4o.config.Configuration">Configuration</see>
		/// to be applied
		/// during the defragment process.
		/// </param>
		public virtual void Db4oConfig(com.db4o.config.Configuration config)
		{
			_config = config;
		}

		public virtual int ObjectCommitFrequency()
		{
			return _objectCommitFrequency;
		}

		public virtual void ObjectCommitFrequency(int objectCommitFrequency)
		{
			_objectCommitFrequency = objectCommitFrequency;
		}

		private class NullFilter : com.db4o.defragment.StoredClassFilter
		{
			public virtual bool Accept(com.db4o.ext.StoredClass storedClass)
			{
				return true;
			}
		}

		private static readonly com.db4o.defragment.StoredClassFilter NULLFILTER = new com.db4o.defragment.DefragmentConfig.NullFilter
			();

		public static com.db4o.config.Configuration VanillaDb4oConfig()
		{
			com.db4o.config.Configuration config = com.db4o.Db4o.NewConfiguration();
			config.WeakReferences(false);
			return config;
		}
	}
}
