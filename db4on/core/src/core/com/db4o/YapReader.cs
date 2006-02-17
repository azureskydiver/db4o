namespace com.db4o
{
	/// <summary>public for .NET conversion reasons.</summary>
	/// <remarks>public for .NET conversion reasons.</remarks>
	/// <exclude></exclude>
	public class YapReader
	{
		internal byte[] _buffer;

		public int _offset;

		internal YapReader()
		{
		}

		public YapReader(int a_length)
		{
			_buffer = new byte[a_length];
		}

		internal virtual void append(byte a_byte)
		{
			_buffer[_offset++] = a_byte;
		}

		internal virtual void append(byte[] a_bytes)
		{
			j4o.lang.JavaSystem.arraycopy(a_bytes, 0, _buffer, _offset, a_bytes.Length);
			_offset += a_bytes.Length;
		}

		internal bool containsTheSame(com.db4o.YapReader other)
		{
			if (other != null)
			{
				byte[] otherBytes = other._buffer;
				if (_buffer == null)
				{
					return otherBytes == null;
				}
				if (otherBytes != null && _buffer.Length == otherBytes.Length)
				{
					int len = _buffer.Length;
					for (int i = 0; i < len; i++)
					{
						if (_buffer[i] != otherBytes[i])
						{
							return false;
						}
					}
					return true;
				}
			}
			return false;
		}

		public virtual int getLength()
		{
			return _buffer.Length;
		}

		public virtual void incrementOffset(int a_by)
		{
			_offset += a_by;
		}

		/// <summary>non-encrypted read, used for indexes</summary>
		/// <param name="a_stream"></param>
		/// <param name="a_address"></param>
		public virtual void read(com.db4o.YapStream a_stream, int a_address, int addressOffset
			)
		{
			a_stream.readBytes(_buffer, a_address, addressOffset, getLength());
		}

		internal virtual void readBegin(byte a_identifier)
		{
		}

		public virtual void readBegin(int a_id, byte a_identifier)
		{
		}

		public virtual byte readByte()
		{
			return _buffer[_offset++];
		}

		internal virtual byte[] readBytes(int a_length)
		{
			byte[] bytes = new byte[a_length];
			readBytes(bytes);
			return bytes;
		}

		internal virtual void readBytes(byte[] bytes)
		{
			int length = bytes.Length;
			j4o.lang.JavaSystem.arraycopy(_buffer, _offset, bytes, 0, length);
			_offset += length;
		}

		internal com.db4o.YapReader readEmbeddedObject(com.db4o.Transaction a_trans)
		{
			return a_trans.i_stream.readObjectReaderByAddress(readInt(), readInt());
		}

		internal virtual void readEncrypt(com.db4o.YapStream a_stream, int a_address)
		{
			a_stream.readBytes(_buffer, a_address, getLength());
			a_stream.i_handlers.decrypt(this);
		}

		internal virtual void readEnd()
		{
			if (com.db4o.Deploy.debug && com.db4o.Deploy.brackets)
			{
				if (readByte() != com.db4o.YapConst.YAPEND)
				{
					throw new j4o.lang.RuntimeException("YapBytes.readEnd() YAPEND expected");
				}
			}
		}

		public int readInt()
		{
			int o = (_offset += 4) - 1;
			return (_buffer[o] & 255) | (_buffer[--o] & 255) << 8 | (_buffer[--o] & 255) << 16
				 | _buffer[--o] << 24;
		}

		internal virtual void replaceWith(byte[] a_bytes)
		{
			j4o.lang.JavaSystem.arraycopy(a_bytes, 0, _buffer, 0, getLength());
		}

		internal virtual string toString(com.db4o.Transaction a_trans)
		{
			try
			{
				return (string)a_trans.i_stream.i_handlers.i_stringHandler.read1(this);
			}
			catch (System.Exception e)
			{
				if (com.db4o.Deploy.debug || com.db4o.Debug.atHome)
				{
					j4o.lang.JavaSystem.printStackTrace(e);
				}
			}
			return "";
		}

		internal virtual void writeBegin(byte a_identifier)
		{
		}

		internal virtual void writeBegin(byte a_identifier, int a_length)
		{
		}

		internal virtual void writeEnd()
		{
			if (com.db4o.Deploy.debug && com.db4o.Deploy.brackets)
			{
				append(com.db4o.YapConst.YAPEND);
			}
		}

		public void writeInt(int a_int)
		{
			int o = _offset + 4;
			_offset = o;
			byte[] b = _buffer;
			b[--o] = (byte)a_int;
			b[--o] = (byte)(a_int >>= 8);
			b[--o] = (byte)(a_int >>= 8);
			b[--o] = (byte)(a_int >>= 8);
		}
	}
}
