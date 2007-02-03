namespace Db4oUnit.Extensions.Fixtures
{
	public abstract class AbstractSoloDb4oFixture : Db4oUnit.Extensions.Fixtures.AbstractDb4oFixture
	{
		private com.db4o.ext.ExtObjectContainer _db;

		protected AbstractSoloDb4oFixture(Db4oUnit.Extensions.Fixtures.ConfigurationSource
			 configSource) : base(configSource)
		{
		}

		public sealed override void Open()
		{
			Db4oUnit.Assert.IsNull(_db);
			_db = CreateDatabase(Config()).Ext();
		}

		public override void Close()
		{
			if (null != _db)
			{
				Db4oUnit.Assert.IsTrue(Db().Close());
				_db = null;
			}
		}

		public override com.db4o.ext.ExtObjectContainer Db()
		{
			return _db;
		}

		protected abstract com.db4o.ObjectContainer CreateDatabase(com.db4o.config.Configuration
			 config);

		public override com.db4o.@internal.LocalObjectContainer FileSession()
		{
			return (com.db4o.@internal.LocalObjectContainer)_db;
		}
	}
}
