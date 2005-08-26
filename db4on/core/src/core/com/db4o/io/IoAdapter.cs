namespace com.db4o.io
{
	public abstract class IoAdapter
	{
		private int _blockSize;

		protected long regularAddress(int blockAddress, int blockAddressOffset)
		{
			return (long)blockAddress * _blockSize + blockAddressOffset;
		}

		public virtual void blockCopy(int oldAddress, int oldAddressOffset, int newAddress
			, int newAddressOffset, int length)
		{
			copy(regularAddress(oldAddress, oldAddressOffset), regularAddress(newAddress, newAddressOffset
				), length);
		}

		public virtual void blockSeek(int address)
		{
			blockSeek(address, 0);
		}

		public virtual void blockSeek(int address, int offset)
		{
			seek(regularAddress(address, offset));
		}

		public virtual void blockSize(int blockSize)
		{
			_blockSize = blockSize;
		}

		public abstract void close();

		public virtual void copy(long oldAddress, long newAddress, int length)
		{
			byte[] copyBytes = new byte[length];
			seek(oldAddress);
			read(copyBytes);
			seek(newAddress);
			write(copyBytes);
		}

		public virtual bool exists(string path)
		{
			j4o.io.File existingFile = new j4o.io.File(path);
			return existingFile.exists() && existingFile.length() > 0;
		}

		public abstract long getLength();

		public abstract com.db4o.io.IoAdapter open(string path, bool lockFile, long initialLength
			);

		public virtual int read(byte[] buffer)
		{
			return read(buffer, buffer.Length);
		}

		public abstract int read(byte[] bytes, int length);

		public abstract void seek(long pos);

		public abstract void sync();

		public virtual void write(byte[] bytes)
		{
			write(bytes, bytes.Length);
		}

		public abstract void write(byte[] buffer, int length);

		public virtual int blockSize()
		{
			return _blockSize;
		}
	}
}