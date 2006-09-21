namespace com.db4o.drs.inside
{
	public interface TestableReplicationProviderInside : com.db4o.drs.inside.ReplicationProviderInside
		, com.db4o.drs.inside.SimpleObjectContainer
	{
		bool SupportsMultiDimensionalArrays();

		bool SupportsHybridCollection();

		bool SupportsRollback();

		bool SupportsCascadeDelete();
	}
}
