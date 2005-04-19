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
	/// <exclude></exclude>
	public class YapMemoryFile : com.db4o.YapFile
	{
		private bool i_closed = false;

		internal readonly com.db4o.ext.MemoryFile i_memoryFile;

		private int i_length = 0;

		protected YapMemoryFile(com.db4o.YapStream a_parent, com.db4o.ext.MemoryFile memoryFile
			) : base(a_parent)
		{
			this.i_memoryFile = memoryFile;
			try
			{
				open();
			}
			catch (System.Exception e)
			{
				com.db4o.Db4o.throwRuntimeException(22, e);
			}
			initialize3();
		}

		internal YapMemoryFile(com.db4o.ext.MemoryFile memoryFile) : this(null, memoryFile
			)
		{
		}

		public override void backup(string path)
		{
			com.db4o.Db4o.throwRuntimeException(60);
		}

		internal virtual void checkDemoHop()
		{
		}

		internal override bool close2()
		{
			i_entryCounter++;
			try
			{
				write(true);
			}
			catch (System.Exception t)
			{
				fatalException(t);
			}
			base.close2();
			i_entryCounter--;
			if (i_closed == false)
			{
				byte[] temp = new byte[i_length];
				j4o.lang.JavaSystem.arraycopy(i_memoryFile.getBytes(), 0, temp, 0, i_length);
				i_memoryFile.setBytes(temp);
			}
			i_closed = true;
			return true;
		}

		internal override void copy(int oldAddress, int oldAddressOffset, int newAddress, 
			int newAddressOffset, int length)
		{
			byte[] bytes = memoryFileBytes(newAddress + newAddressOffset + length);
			j4o.lang.JavaSystem.arraycopy(bytes, oldAddress + oldAddressOffset, bytes, newAddress
				 + newAddressOffset, length);
		}

		internal override void emergencyClose()
		{
			base.emergencyClose();
			i_closed = true;
		}

		internal override long fileLength()
		{
			return i_length;
		}

		internal override string fileName()
		{
			return "Memory File";
		}

		internal override bool hasShutDownHook()
		{
			return false;
		}

		internal override bool needsLockFileThread()
		{
			return false;
		}

		private void open()
		{
			byte[] bytes = i_memoryFile.getBytes();
			if (bytes == null || bytes.Length == 0)
			{
				i_memoryFile.setBytes(new byte[i_memoryFile.getInitialSize()]);
				configureNewFile();
				write(false);
				writeHeader(false);
			}
			else
			{
				i_length = bytes.Length;
				readThis();
			}
		}

		internal override void readBytes(byte[] a_bytes, int a_address, int a_length)
		{
			try
			{
				j4o.lang.JavaSystem.arraycopy(i_memoryFile.getBytes(), a_address, a_bytes, 0, a_length
					);
			}
			catch (System.Exception e)
			{
				com.db4o.Db4o.throwRuntimeException(13, e);
			}
		}

		internal override void readBytes(byte[] bytes, int address, int addressOffset, int
			 length)
		{
			readBytes(bytes, address + addressOffset, length);
		}

		internal override void syncFiles()
		{
		}

		internal override bool writeAccessTime()
		{
			return true;
		}

		internal override void writeBytes(com.db4o.YapWriter a_bytes)
		{
			int address = a_bytes.getAddress() + a_bytes.addressOffset();
			int length = a_bytes.getLength();
			j4o.lang.JavaSystem.arraycopy(a_bytes._buffer, 0, memoryFileBytes(address + length
				), address, length);
		}

		private byte[] memoryFileBytes(int a_lastByte)
		{
			byte[] bytes = i_memoryFile.getBytes();
			if (a_lastByte > i_length)
			{
				if (a_lastByte > bytes.Length)
				{
					int increase = a_lastByte - bytes.Length;
					if (increase < i_memoryFile.getIncrementSizeBy())
					{
						increase = i_memoryFile.getIncrementSizeBy();
					}
					byte[] temp = new byte[bytes.Length + increase];
					j4o.lang.JavaSystem.arraycopy(bytes, 0, temp, 0, bytes.Length);
					i_memoryFile.setBytes(temp);
					bytes = temp;
				}
				i_length = a_lastByte;
			}
			return bytes;
		}

		internal override void writeXBytes(int a_address, int a_length)
		{
		}
	}
}
