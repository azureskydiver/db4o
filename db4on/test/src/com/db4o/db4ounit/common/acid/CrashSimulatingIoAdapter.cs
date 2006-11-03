namespace com.db4o.db4ounit.common.acid
{
	public class CrashSimulatingIoAdapter : com.db4o.io.VanillaIoAdapter
	{
		internal com.db4o.db4ounit.common.acid.CrashSimulatingBatch batch;

		internal long curPos;

		public CrashSimulatingIoAdapter(com.db4o.io.IoAdapter delegateAdapter) : base(delegateAdapter
			)
		{
			batch = new com.db4o.db4ounit.common.acid.CrashSimulatingBatch();
		}

		private CrashSimulatingIoAdapter(com.db4o.io.IoAdapter delegateAdapter, string path
			, bool lockFile, long initialLength, com.db4o.db4ounit.common.acid.CrashSimulatingBatch
			 batch) : base(delegateAdapter.Open(path, lockFile, initialLength))
		{
			this.batch = batch;
		}

		public override com.db4o.io.IoAdapter Open(string path, bool lockFile, long initialLength
			)
		{
			return new com.db4o.db4ounit.common.acid.CrashSimulatingIoAdapter(_delegate, path
				, lockFile, initialLength, batch);
		}

		public override void Seek(long pos)
		{
			curPos = pos;
			base.Seek(pos);
		}

		public override void Write(byte[] buffer, int length)
		{
			base.Write(buffer, length);
			byte[] copy = new byte[buffer.Length];
			System.Array.Copy(buffer, 0, copy, 0, buffer.Length);
			batch.Add(copy, curPos, length);
		}

		public override void Sync()
		{
			base.Sync();
			batch.Sync();
		}
	}
}
