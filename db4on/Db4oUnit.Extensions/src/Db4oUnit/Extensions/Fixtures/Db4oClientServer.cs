namespace Db4oUnit.Extensions.Fixtures
{
	public class Db4oClientServer : Db4oUnit.Extensions.Db4oFixture
	{
		private static readonly string HOST = "localhost";

		private static readonly string USERNAME = "db4o";

		private static readonly string PASSWORD = USERNAME;

		private com.db4o.ObjectServer _server;

		private readonly int _port;

		private readonly j4o.io.File _yap;

		public Db4oClientServer(string fileName, int port)
		{
			_yap = new j4o.io.File(fileName);
			_port = port;
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

		public virtual com.db4o.ext.ExtObjectContainer Db()
		{
			try
			{
				return com.db4o.Db4o.OpenClient(HOST, _port, USERNAME, PASSWORD).Ext();
			}
			catch (System.IO.IOException e)
			{
				j4o.lang.JavaSystem.PrintStackTrace(e);
				throw new Db4oUnit.TestException(e);
			}
		}

		public virtual void Clean()
		{
			_yap.Delete();
		}
	}
}
