namespace com.db4o.drs.db4o
{
	public class Db4oReplicationProviderSignature : com.db4o.drs.inside.ReadonlyReplicationProviderSignature
	{
		private readonly com.db4o.ext.Db4oDatabase _delegate;

		public Db4oReplicationProviderSignature(com.db4o.ext.Db4oDatabase delegate_)
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
