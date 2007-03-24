namespace Db4objects.Drs.Db4o
{
	public interface IDb4oReplicationProvider : Db4objects.Drs.Inside.ITestableReplicationProvider
		, Db4objects.Db4o.Internal.Replication.IDb4oReplicationReferenceProvider, Db4objects.Drs.Inside.ITestableReplicationProviderInside
	{
		Db4objects.Db4o.Ext.IExtObjectContainer GetObjectContainer();
	}
}
