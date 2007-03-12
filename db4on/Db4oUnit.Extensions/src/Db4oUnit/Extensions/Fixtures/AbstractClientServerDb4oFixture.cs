namespace Db4oUnit.Extensions.Fixtures
{
	public abstract class AbstractClientServerDb4oFixture : Db4oUnit.Extensions.Fixtures.AbstractDb4oFixture
	{
		protected static readonly string FILE = "Db4oClientServer.yap";

		protected static readonly string HOST = "localhost";

		protected const int PORT = unchecked((int)(0xdb40));

		protected static readonly string USERNAME = "db4o";

		protected static readonly string PASSWORD = USERNAME;

		private com.db4o.ObjectServer _server;

		private readonly j4o.io.File _yap;

		protected readonly int _port;

		public AbstractClientServerDb4oFixture(Db4oUnit.Extensions.Fixtures.ConfigurationSource
			 configSource, string fileName, int port) : base(configSource)
		{
			_yap = new j4o.io.File(fileName);
			_port = port;
		}

		public AbstractClientServerDb4oFixture(Db4oUnit.Extensions.Fixtures.ConfigurationSource
			 configSource) : this(configSource, FILE, PORT)
		{
		}

		public override void Close()
		{
			_server.Close();
		}

		public override void Open()
		{
			_server = com.db4o.Db4o.OpenServer(Config(), _yap.GetAbsolutePath(), _port);
			_server.GrantAccess(USERNAME, PASSWORD);
		}

		public abstract override com.db4o.ext.ExtObjectContainer Db();

		protected override void DoClean()
		{
			_yap.Delete();
		}

		public virtual com.db4o.ObjectServer Server()
		{
			return _server;
		}

		/// <summary>
		/// Does not accept a clazz which is assignable from OptOutCS, or not
		/// assignable from Db4oTestCase.
		/// </summary>
		/// <remarks>
		/// Does not accept a clazz which is assignable from OptOutCS, or not
		/// assignable from Db4oTestCase.
		/// </remarks>
		/// <returns>
		/// returns false if the clazz is assignable from OptOutCS, or not
		/// assignable from Db4oTestCase. Otherwise, returns true.
		/// </returns>
		public override bool Accept(System.Type clazz)
		{
			if ((typeof(Db4oUnit.Extensions.Fixtures.OptOutCS).IsAssignableFrom(clazz)) || !typeof(Db4oUnit.Extensions.Db4oTestCase)
				.IsAssignableFrom(clazz))
			{
				return false;
			}
			return true;
		}

		public override com.db4o.@internal.LocalObjectContainer FileSession()
		{
			return (com.db4o.@internal.LocalObjectContainer)_server.Ext().ObjectContainer();
		}

		public override void Defragment()
		{
			Defragment(FILE);
		}

		protected virtual com.db4o.ObjectContainer OpenEmbeddedClient()
		{
			return _server.OpenClient(Config());
		}
	}
}
