namespace com.db4o.test.acid
{
	public class CrashSimulatingIoAdapter : com.db4o.io.VanillaIoAdapter
	{
		internal com.db4o.test.acid.CrashSimulatingBatch batch;

		internal long curPos;

		public CrashSimulatingIoAdapter(com.db4o.io.IoAdapter delegateAdapter) : base(delegateAdapter
			)
		{
			batch = new com.db4o.test.acid.CrashSimulatingBatch();
		}

		private CrashSimulatingIoAdapter(com.db4o.io.IoAdapter delegateAdapter, string path
			, bool lockFile, long initialLength, com.db4o.test.acid.CrashSimulatingBatch batch
			) : base(delegateAdapter.open(path, lockFile, initialLength))
		{
			this.batch = batch;
		}

		public override com.db4o.io.IoAdapter open(string path, bool lockFile, long initialLength
			)
		{
			return new com.db4o.test.acid.CrashSimulatingIoAdapter(_delegate, path, lockFile, 
				initialLength, batch);
		}

		public override void seek(long pos)
		{
			curPos = pos;
			base.seek(pos);
		}

		public override void write(byte[] buffer, int length)
		{
			base.write(buffer, length);
			byte[] copy = new byte[buffer.Length];
			System.Array.Copy(buffer, 0, copy, 0, buffer.Length);
			batch.add(copy, curPos, length);
		}

		public override void sync()
		{
			base.sync();
			batch.sync();
		}
	}
}
