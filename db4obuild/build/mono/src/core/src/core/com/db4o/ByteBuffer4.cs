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
namespace com.db4o
{
	/// <summary>
	/// Transport buffer for C/S mode to simulate a
	/// socket connection in memory.
	/// </summary>
	/// <remarks>
	/// Transport buffer for C/S mode to simulate a
	/// socket connection in memory.
	/// </remarks>
	internal class ByteBuffer4
	{
		private const int DISCARD_BUFFER_SIZE = 500;

		private byte[] i_cache;

		private bool i_closed = false;

		private int i_readOffset;

		protected int i_timeout;

		private int i_writeOffset;

		private readonly object i_lock = new object();

		public ByteBuffer4(int timeout)
		{
			i_timeout = timeout;
		}

		private int available()
		{
			return i_writeOffset - i_readOffset;
		}

		private void checkDiscardCache()
		{
			if (i_readOffset == i_writeOffset && i_cache.Length > DISCARD_BUFFER_SIZE)
			{
				i_cache = null;
				i_readOffset = 0;
				i_writeOffset = 0;
			}
		}

		internal virtual void close()
		{
			i_closed = true;
		}

		private void makefit(int length)
		{
			if (i_cache == null)
			{
				i_cache = new byte[length];
			}
			else
			{
				if (i_writeOffset + length > i_cache.Length)
				{
					if (i_writeOffset + length - i_readOffset <= i_cache.Length)
					{
						byte[] temp = new byte[i_cache.Length];
						j4o.lang.JavaSystem.arraycopy(i_cache, i_readOffset, temp, 0, i_cache.Length - i_readOffset
							);
						i_cache = temp;
						i_writeOffset -= i_readOffset;
						i_readOffset = 0;
					}
					else
					{
						byte[] temp = new byte[i_writeOffset + length];
						j4o.lang.JavaSystem.arraycopy(i_cache, 0, temp, 0, i_cache.Length);
						i_cache = temp;
					}
				}
			}
		}

		public virtual int read()
		{
			lock (i_lock)
			{
				waitForAvailable();
				int ret = i_cache[i_readOffset++];
				checkDiscardCache();
				return ret;
			}
		}

		public virtual int read(byte[] a_bytes, int a_offset, int a_length)
		{
			lock (i_lock)
			{
				waitForAvailable();
				int avail = available();
				if (avail < a_length)
				{
					a_length = avail;
				}
				j4o.lang.JavaSystem.arraycopy(i_cache, i_readOffset, a_bytes, a_offset, a_length);
				i_readOffset += a_length;
				checkDiscardCache();
				return avail;
			}
		}

		public virtual void setTimeout(int timeout)
		{
			i_timeout = timeout;
		}

		private void waitForAvailable()
		{
			while (available() == 0)
			{
				try
				{
					j4o.lang.JavaSystem.wait(i_lock, i_timeout);
				}
				catch (System.Exception e)
				{
					throw new j4o.io.IOException(com.db4o.Messages.get(55));
				}
			}
			if (i_closed)
			{
				throw new j4o.io.IOException(com.db4o.Messages.get(35));
			}
		}

		public virtual void write(byte[] bytes)
		{
			lock (i_lock)
			{
				makefit(bytes.Length);
				j4o.lang.JavaSystem.arraycopy(bytes, 0, i_cache, i_writeOffset, bytes.Length);
				i_writeOffset += bytes.Length;
				j4o.lang.JavaSystem.notify(i_lock);
			}
		}

		public virtual void write(int i)
		{
			lock (i_lock)
			{
				makefit(1);
				i_cache[i_writeOffset++] = (byte)i;
				j4o.lang.JavaSystem.notify(i_lock);
			}
		}
	}
}
