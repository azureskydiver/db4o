namespace Db4objects.Drs.Inside
{
	public interface IReadonlyReplicationProviderSignature
	{
		long GetId();

		byte[] GetSignature();

		long GetCreated();
	}
}
