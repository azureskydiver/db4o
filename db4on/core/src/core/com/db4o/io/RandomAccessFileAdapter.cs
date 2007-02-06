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
				_delegate.Seek(initialLength - 1);
				_delegate.Write(new byte[] { 0 });
			}
			if (lockFile)
			{
				com.db4o.@internal.Platform4.LockFile(_delegate);
			}
		}

		public override void Close()
		{
			try
			{
				com.db4o.@internal.Platform4.UnlockFile(_delegate);
			}
			catch
			{
			}
			_delegate.Close();
		}

		public override void Delete(string path)
		{
			new j4o.io.File(path).Delete();
		}

		public override bool Exists(string path)
		{
			j4o.io.File existingFile = new j4o.io.File(path);
			return existingFile.Exists() && existingFile.Length() > 0;
		}

		public override long GetLength()
		{
			return _delegate.Length();
		}

		public override com.db4o.io.IoAdapter Open(string path, bool lockFile, long initialLength
			)
		{
			return new com.db4o.io.RandomAccessFileAdapter(path, lockFile, initialLength);
		}

		public override int Read(byte[] bytes, int length)
		{
			return _delegate.Read(bytes, 0, length);
		}

		public override void Seek(long pos)
		{
			_delegate.Seek(pos);
		}

		public override void Sync()
		{
			_delegate.GetFD().Sync();
		}

		public override void Write(byte[] buffer, int length)
		{
			_delegate.Write(buffer, 0, length);
		}
	}
}
