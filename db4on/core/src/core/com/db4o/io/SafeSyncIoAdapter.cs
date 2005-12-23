namespace com.db4o.io
{
	/// <summary>Debug IoAdapter syncing to drive after every write call.</summary>
	/// <remarks>Debug IoAdapter syncing to drive after every write call.</remarks>
	public class SafeSyncIoAdapter : com.db4o.io.VanillaIoAdapter
	{
		public SafeSyncIoAdapter(com.db4o.io.IoAdapter delegateAdapter) : base(delegateAdapter
			)
		{
		}

		private SafeSyncIoAdapter(com.db4o.io.IoAdapter delegateAdapter, string path, bool
			 lockFile, long initialLength) : base(delegateAdapter.open(path, lockFile, initialLength
			))
		{
		}

		public override com.db4o.io.IoAdapter open(string path, bool lockFile, long initialLength
			)
		{
			return new com.db4o.io.SafeSyncIoAdapter(_delegate, path, lockFile, initialLength
				);
		}

		public override void write(byte[] buffer, int length)
		{
			base.write(buffer, length);
			sync();
		}
	}
}
