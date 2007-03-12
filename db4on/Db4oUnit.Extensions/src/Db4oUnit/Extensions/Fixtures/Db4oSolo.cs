namespace Db4oUnit.Extensions.Fixtures
{
	public class Db4oSolo : Db4oUnit.Extensions.Fixtures.AbstractFileBasedDb4oFixture
	{
		private static readonly string FILE = "db4oSoloTest.yap";

		public Db4oSolo() : this(new Db4oUnit.Extensions.Fixtures.IndependentConfigurationSource
			())
		{
		}

		public Db4oSolo(Db4oUnit.Extensions.Fixtures.ConfigurationSource configSource) : 
			base(configSource, FILE)
		{
		}

		protected override com.db4o.ObjectContainer CreateDatabase(com.db4o.config.Configuration
			 config)
		{
			return com.db4o.Db4o.OpenFile(config, GetAbsolutePath());
		}

		public override string GetLabel()
		{
			return "SOLO";
		}

		public override void Defragment()
		{
			Defragment(FILE);
		}
	}
}
