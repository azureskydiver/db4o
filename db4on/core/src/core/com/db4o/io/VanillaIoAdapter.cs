namespace com.db4o.io
{
	/// <summary>base class for IoAdapters that delegate to other IoAdapters (decorator pattern)
	/// 	</summary>
	public abstract class VanillaIoAdapter : com.db4o.io.IoAdapter
	{
		protected com.db4o.io.IoAdapter _delegate;

		public VanillaIoAdapter(com.db4o.io.IoAdapter delegateAdapter)
		{
			_delegate = delegateAdapter;
		}

		protected VanillaIoAdapter(com.db4o.io.IoAdapter delegateAdapter, string path, bool
			 lockFile, long initialLength)
		{
			_delegate = delegateAdapter.open(path, lockFile, initialLength);
		}

		public override void close()
		{
			_delegate.close();
		}

		public override void delete(string path)
		{
			_delegate.delete(path);
		}

		public override bool exists(string path)
		{
			return _delegate.exists(path);
		}

		public override long getLength()
		{
			return _delegate.getLength();
		}

		public override int read(byte[] bytes, int length)
		{
			return _delegate.read(bytes, length);
		}

		public override void seek(long pos)
		{
			_delegate.seek(pos);
		}

		public override void sync()
		{
			_delegate.sync();
		}

		public override void write(byte[] buffer, int length)
		{
			_delegate.write(buffer, length);
		}
	}
}
