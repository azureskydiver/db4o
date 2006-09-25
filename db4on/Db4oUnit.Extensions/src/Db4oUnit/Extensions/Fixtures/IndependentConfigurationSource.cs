namespace Db4oUnit.Extensions.Fixtures
{
	public class IndependentConfigurationSource : Db4oUnit.Extensions.Fixtures.ConfigurationSource
	{
		public virtual com.db4o.config.Configuration Config()
		{
			return com.db4o.Db4o.NewConfiguration();
		}
	}
}
