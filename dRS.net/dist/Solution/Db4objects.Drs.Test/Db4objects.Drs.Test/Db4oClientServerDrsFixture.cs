namespace Db4objects.Drs.Test
{
	public class Db4oClientServerDrsFixture : Db4objects.Drs.Test.Db4oDrsFixture
	{
		private static readonly string HOST = "localhost";

		private static readonly string USERNAME = "db4o";

		private static readonly string PASSWORD = USERNAME;

		private Db4objects.Db4o.IObjectServer _server;

		private int _port;

		public Db4oClientServerDrsFixture(string name, int port) : base(name)
		{
			_port = port;
		}

		public override void Close()
		{
			base.Close();
			_server.Close();
		}

		public override void Open()
		{
			Db4objects.Db4o.Db4oFactory.Configure().MessageLevel(-1);
			_server = Db4objects.Db4o.Db4oFactory.OpenServer(testFile.GetPath(), _port);
			_server.GrantAccess(USERNAME, PASSWORD);
			try
			{
				_db = (Db4objects.Db4o.Ext.IExtObjectContainer)Db4objects.Db4o.Db4oFactory.OpenClient
					(HOST, _port, USERNAME, PASSWORD);
			}
			catch (System.IO.IOException e)
			{
				throw new System.Exception(e.Message, e);
			}
			_provider = new Db4objects.Drs.Db4o.Db4oReplicationProvider(_db, _name);
		}
	}
}
