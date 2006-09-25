namespace Db4oUnit.Extensions.Fixtures
{
	public class Db4oMultiClient : Db4oUnit.Extensions.Fixtures.AbstractClientServerDb4oFixture
	{
		public override com.db4o.ext.ExtObjectContainer Db()
		{
			try
			{
				return com.db4o.Db4o.OpenClient(HOST, PORT, USERNAME, PASSWORD).Ext();
			}
			catch (System.IO.IOException e)
			{
				j4o.lang.JavaSystem.PrintStackTrace(e);
				throw new Db4oUnit.TestException(e);
			}
		}
	}
}
