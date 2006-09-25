namespace Db4oUnit.Extensions.Fixtures
{
	public class GlobalConfigurationSource : Db4oUnit.Extensions.Fixtures.ConfigurationSource
	{
		private readonly com.db4o.config.Configuration _config = com.db4o.Db4o.NewConfiguration
			();

		public virtual com.db4o.config.Configuration Config()
		{
			return _config;
		}
	}
}
