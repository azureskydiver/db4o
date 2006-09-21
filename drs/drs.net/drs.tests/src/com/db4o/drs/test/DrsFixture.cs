namespace com.db4o.drs.test
{
	public interface DrsFixture
	{
		com.db4o.drs.inside.TestableReplicationProviderInside Provider();

		void Open();

		void Close();

		void Clean();
	}
}
