/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
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
