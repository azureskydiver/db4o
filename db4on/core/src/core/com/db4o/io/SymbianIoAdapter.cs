namespace com.db4o.io
{
	/// <summary>
	/// Workaround for two Symbian/Epoc I/O bugs:
	/// - seek() cannot move beyond the current file length.
	/// </summary>
	/// <remarks>
	/// Workaround for two Symbian/Epoc I/O bugs:
	/// - seek() cannot move beyond the current file length.
	/// Fix: Write padding bytes up to the seek target if necessary
	/// - Under certain (rare) conditions, calls to RAF.length() seems
	/// to garble up following reads.
	/// Fix: Use a second RAF handle to the file for length() calls
	/// only.
	/// TODO:
	/// - BasicClusterTest C/S fails (in AllTests context only)
	/// </remarks>
	/// <exclude></exclude>
	public class SymbianIoAdapter : com.db4o.io.RandomAccessFileAdapter
	{
		private byte[] _seekBytes = new byte[500];

		private string _path;

		private long _pos;

		private long _length;

		protected SymbianIoAdapter(string path, bool lockFile, long initialLength) : base
			(path, lockFile, initialLength)
		{
			_path = path;
			_pos = 0;
			setLength();
		}

		private void setLength()
		{
			_length = retrieveLength();
		}

		private long retrieveLength()
		{
			j4o.io.RandomAccessFile file = new j4o.io.RandomAccessFile(_path, "r");
			try
			{
				return file.length();
			}
			catch (System.IO.IOException exc)
			{
				file.close();
				throw exc;
			}
		}

		public SymbianIoAdapter() : base()
		{
		}

		public override com.db4o.io.IoAdapter open(string path, bool lockFile, long initialLength
			)
		{
			return new com.db4o.io.SymbianIoAdapter(path, lockFile, initialLength);
		}

		public override long getLength()
		{
			setLength();
			return _length;
		}

		public override int read(byte[] bytes, int length)
		{
			int ret = base.read(bytes, length);
			_pos += ret;
			return ret;
		}

		public override void write(byte[] buffer, int length)
		{
			base.write(buffer, length);
			_pos += length;
			if (_pos > _length)
			{
				setLength();
			}
		}

		public override void seek(long pos)
		{
			if (pos > _length)
			{
				setLength();
			}
			if (pos > _length)
			{
				int len = (int)(pos - _length);
				base.seek(_length);
				_pos = _length;
				if (len < 500)
				{
					write(_seekBytes, len);
				}
				else
				{
					write(new byte[len]);
				}
			}
			base.seek(pos);
			_pos = pos;
		}
	}
}
