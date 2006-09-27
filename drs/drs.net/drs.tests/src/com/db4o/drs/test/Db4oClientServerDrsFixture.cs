namespace com.db4o.drs.test
{
	public class Db4oClientServerDrsFixture : com.db4o.drs.test.DrsFixture
	{
		private static readonly string HOST = "localhost";

		private static readonly string USERNAME = "db4o";

		private static readonly string PASSWORD = USERNAME;

		private string _name;

		private com.db4o.ObjectServer _server;

		private com.db4o.ext.ExtObjectContainer _db;

		private com.db4o.drs.inside.TestableReplicationProviderInside _provider;

		private int _port;

		public Db4oClientServerDrsFixture(string name, int port)
		{
			_name = name;
			_port = port;
		}

		public virtual com.db4o.drs.inside.TestableReplicationProviderInside Provider()
		{
			return _provider;
		}

		private string YapFileName()
		{
			return "drs_cs_" + _name + ".yap";
		}

		public virtual void Clean()
		{
			new j4o.io.File(YapFileName()).Delete();
		}

		public virtual void Close()
		{
			_db.Close();
			_provider.Destroy();
			_server.Close();
		}

		public virtual com.db4o.ext.ExtObjectContainer Db()
		{
			return _db;
		}

		public virtual void Open()
		{
			com.db4o.Db4o.Configure().MessageLevel(0);
			_server = com.db4o.Db4o.OpenServer(YapFileName(), _port);
			_server.GrantAccess(USERNAME, PASSWORD);
			try
			{
				_db = (com.db4o.ext.ExtObjectContainer)com.db4o.Db4o.OpenClient(HOST, _port, USERNAME
					, PASSWORD);
			}
			catch (System.IO.IOException e)
			{
				throw new j4o.lang.RuntimeException(e);
			}
			_provider = new com.db4o.drs.db4o.Db4oReplicationProvider(_db, _name);
		}
	}
}
