namespace Db4oUnit.Extensions.Fixtures
{
	public abstract class AbstractDb4oFixture : Db4oUnit.Extensions.Db4oFixture
	{
		private com.db4o.ext.ExtObjectContainer _db;

		private readonly Db4oUnit.Extensions.Fixtures.ConfigurationSource _configSource;

		private com.db4o.config.Configuration _config;

		protected AbstractDb4oFixture(Db4oUnit.Extensions.Fixtures.ConfigurationSource configSource
			)
		{
			_configSource = configSource;
		}

		public void Open()
		{
			_db = CreateDatabase(Config()).Ext();
		}

		public virtual void Close()
		{
			_db.Close();
			_db = null;
		}

		public virtual void Reopen()
		{
			_db.Close();
			Open();
		}

		public virtual com.db4o.ext.ExtObjectContainer Db()
		{
			return _db;
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
			_config = null;
		}

		protected abstract void DoClean();

		protected abstract com.db4o.ObjectContainer CreateDatabase(com.db4o.config.Configuration
			 config);
	}
}
