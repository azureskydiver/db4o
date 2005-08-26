namespace com.db4o.io
{
	public class RandomAccessFileAdapter : com.db4o.io.IoAdapter
	{
		private j4o.io.RandomAccessFile _delegate;

		private byte[] _seekBytes;

		public RandomAccessFileAdapter()
		{
		}

		private RandomAccessFileAdapter(string path, bool lockFile, long initialLength)
		{
			_delegate = new j4o.io.RandomAccessFile(path, "rw");
			_seekBytes = null;
			if (initialLength > 0)
			{
				_delegate.seek(initialLength - 1);
				_delegate.write(new byte[] { 0 });
			}
			if (lockFile)
			{
				com.db4o.Platform4.Lock(_delegate);
			}
		}

		public override void close()
		{
			try
			{
				com.db4o.Platform4.unlock(_delegate);
			}
			catch (System.Exception e)
			{
			}
			_delegate.close();
		}

		public override long getLength()
		{
			return _delegate.length();
		}

		public override com.db4o.io.IoAdapter open(string path, bool lockFile, long initialLength
			)
		{
			return new com.db4o.io.RandomAccessFileAdapter(path, lockFile, initialLength);
		}

		public override int read(byte[] bytes, int length)
		{
			return _delegate.read(bytes, 0, length);
		}

		public override void seek(long pos)
		{
			_delegate.seek(pos);
		}

		public override void sync()
		{
			_delegate.getFD().sync();
		}

		public override void write(byte[] buffer, int length)
		{
			_delegate.write(buffer, 0, length);
		}
	}
}
