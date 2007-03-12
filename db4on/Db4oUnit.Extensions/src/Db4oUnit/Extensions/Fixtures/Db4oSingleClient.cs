namespace Db4oUnit.Extensions.Fixtures
{
	public class Db4oSingleClient : Db4oUnit.Extensions.Fixtures.AbstractClientServerDb4oFixture
	{
		private com.db4o.ext.ExtObjectContainer _objectContainer;

		public Db4oSingleClient(Db4oUnit.Extensions.Fixtures.ConfigurationSource config, 
			string fileName, int port) : base(config, fileName, port)
		{
		}

		public Db4oSingleClient(Db4oUnit.Extensions.Fixtures.ConfigurationSource config, 
			int port) : base(config, FILE, port)
		{
		}

		public Db4oSingleClient(Db4oUnit.Extensions.Fixtures.ConfigurationSource config) : 
			base(config)
		{
		}

		public Db4oSingleClient() : this(new Db4oUnit.Extensions.Fixtures.IndependentConfigurationSource
			())
		{
		}

		public override void Close()
		{
			_objectContainer.Close();
			base.Close();
		}

		public override void Open()
		{
			base.Open();
			try
			{
				_objectContainer = _port == 0 ? OpenEmbeddedClient().Ext() : com.db4o.Db4o.OpenClient
					(Config(), HOST, _port, USERNAME, PASSWORD).Ext();
			}
			catch (System.IO.IOException e)
			{
				j4o.lang.JavaSystem.PrintStackTrace(e);
				throw new Db4oUnit.TestException(e);
			}
		}

		public override com.db4o.ext.ExtObjectContainer Db()
		{
			return _objectContainer;
		}

		public override string GetLabel()
		{
			return "C/S SINGLE-CLIENT";
		}
	}
}
