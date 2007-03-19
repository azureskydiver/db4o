namespace Db4objects.Drs.Db4o
{
	public class Db4oReplicationProviderSignature : Db4objects.Drs.Inside.IReadonlyReplicationProviderSignature
	{
		private readonly Db4objects.Db4o.Ext.Db4oDatabase _delegate;

		public Db4oReplicationProviderSignature(Db4objects.Db4o.Ext.Db4oDatabase delegate_
			)
		{
			_delegate = delegate_;
		}

		public virtual long GetId()
		{
			return 0;
		}

		public virtual byte[] GetSignature()
		{
			return _delegate.GetSignature();
		}

		public virtual long GetCreated()
		{
			return _delegate.GetCreationTime();
		}
	}
}
