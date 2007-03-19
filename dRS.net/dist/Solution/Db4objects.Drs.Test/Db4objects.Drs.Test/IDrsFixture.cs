namespace Db4objects.Drs.Test
{
	public interface IDrsFixture
	{
		Db4objects.Drs.Inside.ITestableReplicationProviderInside Provider();

		void Open();

		void Close();

		void Clean();
	}
}
