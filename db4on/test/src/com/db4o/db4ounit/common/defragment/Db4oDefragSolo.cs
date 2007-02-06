namespace com.db4o.db4ounit.common.defragment
{
	public class Db4oDefragSolo : Db4oUnit.Extensions.Fixtures.Db4oSolo
	{
		public Db4oDefragSolo(Db4oUnit.Extensions.Fixtures.ConfigurationSource configSource
			) : base(configSource)
		{
		}

		protected override com.db4o.ObjectContainer CreateDatabase(com.db4o.config.Configuration
			 config)
		{
			j4o.io.File origFile = new j4o.io.File(GetAbsolutePath());
			if (origFile.Exists())
			{
				try
				{
					string backupFile = GetAbsolutePath() + ".defrag.backup";
					com.db4o.defragment.ContextIDMapping mapping = new com.db4o.defragment.TreeIDMapping
						();
					com.db4o.defragment.DefragmentConfig defragConfig = new com.db4o.defragment.DefragmentConfig
						(GetAbsolutePath(), backupFile, mapping);
					defragConfig.ForceBackupDelete(true);
					com.db4o.config.Configuration clonedConfig = (com.db4o.config.Configuration)((com.db4o.foundation.DeepClone
						)config).DeepClone(null);
					defragConfig.Db4oConfig(clonedConfig);
					com.db4o.defragment.Defragment.Defrag(defragConfig, new _AnonymousInnerClass32(this
						));
				}
				catch (System.IO.IOException e)
				{
					j4o.lang.JavaSystem.PrintStackTrace(e);
				}
			}
			return base.CreateDatabase(config);
		}

		private sealed class _AnonymousInnerClass32 : com.db4o.defragment.DefragmentListener
		{
			public _AnonymousInnerClass32(Db4oDefragSolo _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void NotifyDefragmentInfo(com.db4o.defragment.DefragmentInfo info)
			{
				j4o.lang.JavaSystem.Err.WriteLine(info);
			}

			private readonly Db4oDefragSolo _enclosing;
		}

		public override bool Accept(System.Type clazz)
		{
			return !typeof(Db4oUnit.Extensions.Fixtures.OptOutDefragSolo).IsAssignableFrom(clazz
				);
		}
	}
}
