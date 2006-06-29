namespace com.db4o.io
{
	/// <summary>IoAdapter for in-memory operation.</summary>
	/// <remarks>
	/// IoAdapter for in-memory operation.
	/// <br /><br />Configure db4o to operate with this in-memory IoAdapter with
	/// <code>MemoryIoAdapter memoryIoAdapter = new MemoryIoAdapter();<br />
	/// Db4o.configure().io(memoryIoAdapter);</code><br />
	/// <br /><br />Use the normal #openFile() and #openServer() commands to
	/// open ObjectContainers and ObjectServers. The names specified as
	/// file names will be used to identify the
	/// <code>byte[]</code> content of the in-memory files in
	/// the _memoryFiles Hashtable in the adapter. After working with an
	/// in-memory ObjectContainer/ObjectServer the <code>byte[]</code> content
	/// is available in the MemoryIoAdapter by using
	/// <see cref="com.db4o.io.MemoryIoAdapter.Get">com.db4o.io.MemoryIoAdapter.Get</see>
	/// . To add old existing database
	/// <code>byte[]</code> content to a MemoryIoAdapter use
	/// <see cref="com.db4o.io.MemoryIoAdapter.Put">com.db4o.io.MemoryIoAdapter.Put</see>
	/// . To reduce memory consumption of memory
	/// file names that will no longer be used call
	/// <see cref="com.db4o.io.MemoryIoAdapter.Put">com.db4o.io.MemoryIoAdapter.Put</see>
	/// and pass an empty byte array.
	/// </remarks>
	public class MemoryIoAdapter : com.db4o.io.IoAdapter
	{
		private byte[] _bytes;

		private int _length;

		private int _seekPos;

		private com.db4o.foundation.Hashtable4 _memoryFiles;

		private int _growBy;

		public MemoryIoAdapter()
		{
			_memoryFiles = new com.db4o.foundation.Hashtable4(1);
			_growBy = 10000;
		}

		private MemoryIoAdapter(com.db4o.io.MemoryIoAdapter adapter, string name, byte[] 
			bytes)
		{
			_bytes = bytes;
			_length = bytes.Length;
			_growBy = adapter._growBy;
		}

		private MemoryIoAdapter(com.db4o.io.MemoryIoAdapter adapter, string name, int initialLength
			) : this(adapter, name, new byte[initialLength])
		{
		}

		/// <summary>
		/// creates an in-memory database with the passed content bytes and
		/// adds it to the adapter for the specified name.
		/// </summary>
		/// <remarks>
		/// creates an in-memory database with the passed content bytes and
		/// adds it to the adapter for the specified name.
		/// </remarks>
		/// <param name="name">the name to be use for #openFile() or #openServer() calls</param>
		/// <param name="bytes">the database content</param>
		public virtual void Put(string name, byte[] bytes)
		{
			if (bytes == null)
			{
				bytes = new byte[0];
			}
			_memoryFiles.Put(name, new com.db4o.io.MemoryIoAdapter(this, name, bytes));
		}

		/// <summary>returns the content bytes for a database with the given name.</summary>
		/// <remarks>returns the content bytes for a database with the given name.</remarks>
		/// <param name="name">the name to be use for #openFile() or #openServer() calls</param>
		/// <returns>the content bytes</returns>
		public virtual byte[] Get(string name)
		{
			com.db4o.io.MemoryIoAdapter mia = (com.db4o.io.MemoryIoAdapter)_memoryFiles.Get(name
				);
			if (mia == null)
			{
				return null;
			}
			return mia._bytes;
		}

		/// <summary>
		/// configures the length a memory file should grow, if no more
		/// free slots are found within.
		/// </summary>
		/// <remarks>
		/// configures the length a memory file should grow, if no more
		/// free slots are found within.
		/// <br /><br />Specify a large value (100,000 or more) for best performance.
		/// Specify a small value (100) for the smallest memory consumption. The
		/// default setting is 10,000.
		/// </remarks>
		/// <param name="length">the length in bytes</param>
		public virtual void GrowBy(int length)
		{
			if (length < 1)
			{
				length = 1;
			}
			_growBy = length;
		}

		/// <summary>for internal processing only.</summary>
		/// <remarks>for internal processing only.</remarks>
		public override void Close()
		{
		}

		public override void Delete(string path)
		{
			_memoryFiles.Remove(path);
		}

		/// <summary>for internal processing only.</summary>
		/// <remarks>for internal processing only.</remarks>
		public override bool Exists(string path)
		{
			com.db4o.io.MemoryIoAdapter mia = (com.db4o.io.MemoryIoAdapter)_memoryFiles.Get(path
				);
			if (mia == null)
			{
				return false;
			}
			return mia._length > 0;
		}

		/// <summary>for internal processing only.</summary>
		/// <remarks>for internal processing only.</remarks>
		public override long GetLength()
		{
			return _length;
		}

		/// <summary>for internal processing only.</summary>
		/// <remarks>for internal processing only.</remarks>
		public override com.db4o.io.IoAdapter Open(string path, bool lockFile, long initialLength
			)
		{
			com.db4o.io.MemoryIoAdapter mia = (com.db4o.io.MemoryIoAdapter)_memoryFiles.Get(path
				);
			if (mia == null)
			{
				mia = new com.db4o.io.MemoryIoAdapter(this, path, (int)initialLength);
				_memoryFiles.Put(path, mia);
			}
			return mia;
		}

		/// <summary>for internal processing only.</summary>
		/// <remarks>for internal processing only.</remarks>
		public override int Read(byte[] bytes, int length)
		{
			System.Array.Copy(_bytes, _seekPos, bytes, 0, length);
			_seekPos += length;
			return length;
		}

		/// <summary>for internal processing only.</summary>
		/// <remarks>for internal processing only.</remarks>
		public override void Seek(long pos)
		{
			_seekPos = (int)pos;
		}

		/// <summary>for internal processing only.</summary>
		/// <remarks>for internal processing only.</remarks>
		public override void Sync()
		{
		}

		/// <summary>for internal processing only.</summary>
		/// <remarks>for internal processing only.</remarks>
		public override void Write(byte[] buffer, int length)
		{
			if (_seekPos + length > _bytes.Length)
			{
				int growBy = _growBy;
				if (_seekPos + length > growBy)
				{
					growBy = _seekPos + length;
				}
				byte[] temp = new byte[_bytes.Length + growBy];
				System.Array.Copy(_bytes, 0, temp, 0, _length);
				_bytes = temp;
			}
			System.Array.Copy(buffer, 0, _bytes, _seekPos, length);
			_seekPos += length;
			if (_seekPos > _length)
			{
				_length = _seekPos;
			}
		}
	}
}
