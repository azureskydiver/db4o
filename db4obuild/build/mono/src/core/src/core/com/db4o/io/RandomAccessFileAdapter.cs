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
				com.db4o.Platform.Lock(_delegate);
			}
		}

		public override void close()
		{
			try
			{
				com.db4o.Platform.unlock(_delegate);
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
		}

		public override void write(byte[] buffer, int length)
		{
			_delegate.write(buffer, 0, length);
		}
	}
}
