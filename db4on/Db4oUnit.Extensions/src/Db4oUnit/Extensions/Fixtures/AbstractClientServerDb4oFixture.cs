namespace Db4oUnit.Extensions.Fixtures
{
	public abstract class AbstractClientServerDb4oFixture : Db4oUnit.Extensions.Db4oFixture
	{
		protected static readonly string FILE = "Db4oClientServer.yap";

		protected static readonly string HOST = "localhost";

		protected const int PORT = unchecked((int)(0xdb40));

		protected static readonly string USERNAME = "db4o";

		protected static readonly string PASSWORD = USERNAME;

		private com.db4o.ObjectServer _server;

		private readonly j4o.io.File _yap;

		private readonly int _port;

		public AbstractClientServerDb4oFixture(string fileName, int port)
		{
			_yap = new j4o.io.File(fileName);
			_port = port;
		}

		public AbstractClientServerDb4oFixture() : this(FILE, PORT)
		{
		}

		public virtual void Close()
		{
			_server.Close();
		}

		public virtual void Open()
		{
			_server = com.db4o.Db4o.OpenServer(_yap.GetAbsolutePath(), _port);
			_server.GrantAccess(USERNAME, PASSWORD);
		}

		public abstract com.db4o.ext.ExtObjectContainer Db();

		public virtual com.db4o.config.Configuration Config()
		{
			return com.db4o.Db4o.CloneConfiguration();
		}

		public virtual void Reopen()
		{
			Close();
			Open();
		}

		public virtual void Clean()
		{
			_yap.Delete();
		}
	}
}
