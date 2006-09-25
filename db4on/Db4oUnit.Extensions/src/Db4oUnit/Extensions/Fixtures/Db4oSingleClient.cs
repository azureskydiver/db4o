namespace Db4oUnit.Extensions.Fixtures
{
	public class Db4oSingleClient : Db4oUnit.Extensions.Fixtures.AbstractClientServerDb4oFixture
	{
		private com.db4o.ext.ExtObjectContainer _objectContainer;

		public Db4oSingleClient(string fileName, int port) : base(fileName, port)
		{
		}

		public Db4oSingleClient() : base()
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
				_objectContainer = com.db4o.Db4o.OpenClient(HOST, PORT, USERNAME, PASSWORD).Ext();
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
	}
}
