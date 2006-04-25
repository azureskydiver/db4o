namespace com.db4o.io
{
	/// <summary>IO adapter for random access files.</summary>
	/// <remarks>IO adapter for random access files.</remarks>
	public class RandomAccessFileAdapter : com.db4o.io.IoAdapter
	{
		private j4o.io.RandomAccessFile _delegate;

		public RandomAccessFileAdapter()
		{
		}

		protected RandomAccessFileAdapter(string path, bool lockFile, long initialLength)
		{
			_delegate = new j4o.io.RandomAccessFile(path, "rw");
			if (initialLength > 0)
			{
				_delegate.seek(initialLength - 1);
				_delegate.write(new byte[] { 0 });
			}
			if (lockFile)
			{
				com.db4o.Platform4.lockFile(_delegate);
			}
		}

		public override void close()
		{
			try
			{
				com.db4o.Platform4.unlockFile(_delegate);
			}
			catch (System.Exception e)
			{
			}
			_delegate.close();
		}

		public override void delete(string path)
		{
			new j4o.io.File(path).delete();
		}

		public override bool exists(string path)
		{
			j4o.io.File existingFile = new j4o.io.File(path);
			return existingFile.exists() && existingFile.length() > 0;
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
