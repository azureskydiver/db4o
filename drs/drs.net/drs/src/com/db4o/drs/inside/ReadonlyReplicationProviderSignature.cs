namespace com.db4o.drs.inside
{
	public interface ReadonlyReplicationProviderSignature
	{
		long GetId();

		byte[] GetSignature();

		long GetCreated();
	}
}
