namespace Db4oUnit.Extensions.Fixtures
{
	public class Db4oInMemory : Db4oUnit.Extensions.Fixtures.AbstractSoloDb4oFixture
	{
		public Db4oInMemory() : base(new Db4oUnit.Extensions.Fixtures.IndependentConfigurationSource
			())
		{
		}

		public Db4oInMemory(Db4oUnit.Extensions.Fixtures.ConfigurationSource configSource
			) : base(configSource)
		{
		}

		private com.db4o.ext.MemoryFile _memoryFile;

		protected override com.db4o.ObjectContainer CreateDatabase(com.db4o.config.Configuration
			 config)
		{
			if (null == _memoryFile)
			{
				_memoryFile = new com.db4o.ext.MemoryFile();
			}
			return com.db4o.ext.ExtDb4o.OpenMemoryFile(config, _memoryFile);
		}

		protected override void DoClean()
		{
			_memoryFile = null;
		}
	}
}
