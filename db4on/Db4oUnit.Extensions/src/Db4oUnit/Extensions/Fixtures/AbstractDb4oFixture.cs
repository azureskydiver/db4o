namespace Db4oUnit.Extensions.Fixtures
{
	public abstract class AbstractDb4oFixture : Db4oUnit.Extensions.Db4oFixture
	{
		private readonly Db4oUnit.Extensions.Fixtures.ConfigurationSource _configSource;

		private com.db4o.config.Configuration _config;

		protected AbstractDb4oFixture(Db4oUnit.Extensions.Fixtures.ConfigurationSource configSource
			)
		{
			_configSource = configSource;
		}

		public virtual void Reopen()
		{
			Close();
			Open();
		}

		public virtual com.db4o.config.Configuration Config()
		{
			if (_config == null)
			{
				_config = _configSource.Config();
			}
			return _config;
		}

		public virtual void Clean()
		{
			DoClean();
			ResetConfig();
		}

		public virtual bool Accept(System.Type clazz)
		{
			return true;
		}

		protected abstract void DoClean();

		protected virtual void ResetConfig()
		{
			_config = null;
		}

		public abstract void Close();

		public abstract com.db4o.ext.ExtObjectContainer Db();

		public abstract void Open();
	}
}
