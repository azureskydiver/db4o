namespace Db4oUnit.Extensions.Fixtures
{
	public class Db4oSolo : Db4oUnit.Extensions.Fixtures.AbstractFileBasedDb4oFixture
	{
		public Db4oSolo() : base("db4oSoloTest.yap")
		{
		}

		public override void Open()
		{
			Db(com.db4o.Db4o.OpenFile(GetAbsolutePath()).Ext());
		}
	}
}
