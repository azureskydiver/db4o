namespace com.db4o.inside.replication
{
	/// <exclude></exclude>
	public interface Db4oReplicationReference
	{
		com.db4o.ext.Db4oDatabase signaturePart();

		long longPart();

		long version();
	}
}
