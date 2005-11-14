namespace com.db4o.io
{
	/// <summary>
	/// Base class for database file adapters, both for file and memory
	/// databases.
	/// </summary>
	/// <remarks>
	/// Base class for database file adapters, both for file and memory
	/// databases.
	/// </remarks>
	public abstract class IoAdapter
	{
		private int _blockSize;

		/// <summary>converts address and address offset to an absolute address</summary>
		protected long regularAddress(int blockAddress, int blockAddressOffset)
		{
			return (long)blockAddress * _blockSize + blockAddressOffset;
		}

		/// <summary>copies a block within a file in block mode</summary>
		public virtual void blockCopy(int oldAddress, int oldAddressOffset, int newAddress
			, int newAddressOffset, int length)
		{
			copy(regularAddress(oldAddress, oldAddressOffset), regularAddress(newAddress, newAddressOffset
				), length);
		}

		/// <summary>sets the read/write pointer in the file using block mode</summary>
		public virtual void blockSeek(int address)
		{
			blockSeek(address, 0);
		}

		/// <summary>sets the read/write pointer in the file using block mode</summary>
		public virtual void blockSeek(int address, int offset)
		{
			seek(regularAddress(address, offset));
		}

		/// <summary>outside call to set the block size of this adapter</summary>
		public virtual void blockSize(int blockSize)
		{
			_blockSize = blockSize;
		}

		/// <summary>implement to close the adapter</summary>
		public abstract void close();

		/// <summary>copies a block within a file in absolute mode</summary>
		public virtual void copy(long oldAddress, long newAddress, int length)
		{
			byte[] copyBytes = new byte[length];
			seek(oldAddress);
			read(copyBytes);
			seek(newAddress);
			write(copyBytes);
		}

		/// <summary>checks whether a file exists</summary>
		public virtual bool exists(string path)
		{
			j4o.io.File existingFile = new j4o.io.File(path);
			return existingFile.exists() && existingFile.length() > 0;
		}

		/// <summary>implement to return the absolute length of the file</summary>
		public abstract long getLength();

		/// <summary>implement to open the file</summary>
		public abstract com.db4o.io.IoAdapter open(string path, bool lockFile, long initialLength
			);

		/// <summary>reads a buffer at the seeked address</summary>
		public virtual int read(byte[] buffer)
		{
			return read(buffer, buffer.Length);
		}

		/// <summary>implement to read a buffer at the seeked address</summary>
		public abstract int read(byte[] bytes, int length);

		/// <summary>implement to set the read/write pointer in the file, absolute mode</summary>
		public abstract void seek(long pos);

		/// <summary>implement to flush the file contents to storage</summary>
		public abstract void sync();

		/// <summary>writes a buffer to the seeked address</summary>
		public virtual void write(byte[] bytes)
		{
			write(bytes, bytes.Length);
		}

		/// <summary>implement to write a buffer at the seeked address</summary>
		public abstract void write(byte[] buffer, int length);

		/// <summary>returns the block size currently used</summary>
		public virtual int blockSize()
		{
			return _blockSize;
		}
	}
}
