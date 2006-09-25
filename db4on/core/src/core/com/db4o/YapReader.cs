namespace com.db4o
{
	/// <summary>public for .NET conversion reasons.</summary>
	/// <remarks>public for .NET conversion reasons.</remarks>
	/// <exclude></exclude>
	public class YapReader
	{
		public byte[] _buffer;

		public int _offset;

		internal YapReader()
		{
		}

		public YapReader(int a_length)
		{
			_buffer = new byte[a_length];
		}

		public virtual void Seek(int offset)
		{
			_offset = offset;
		}

		public virtual void Append(byte a_byte)
		{
			_buffer[_offset++] = a_byte;
		}

		internal virtual void Append(byte[] a_bytes)
		{
			System.Array.Copy(a_bytes, 0, _buffer, _offset, a_bytes.Length);
			_offset += a_bytes.Length;
		}

		internal bool ContainsTheSame(com.db4o.YapReader other)
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

		public virtual int GetLength()
		{
			return _buffer.Length;
		}

		public virtual void IncrementOffset(int a_by)
		{
			_offset += a_by;
		}

		/// <summary>non-encrypted read, used for indexes</summary>
		/// <param name="a_stream"></param>
		/// <param name="a_address"></param>
		public virtual void Read(com.db4o.YapStream a_stream, int a_address, int addressOffset
			)
		{
			a_stream.ReadBytes(_buffer, a_address, addressOffset, GetLength());
		}

		public void ReadBegin(byte a_identifier)
		{
		}

		public virtual com.db4o.foundation.BitMap4 ReadBitMap(int bitCount)
		{
			com.db4o.foundation.BitMap4 map = new com.db4o.foundation.BitMap4(_buffer, _offset
				, bitCount);
			_offset += map.MarshalledLength();
			return map;
		}

		public virtual byte ReadByte()
		{
			return _buffer[_offset++];
		}

		internal virtual byte[] ReadBytes(int a_length)
		{
			byte[] bytes = new byte[a_length];
			ReadBytes(bytes);
			return bytes;
		}

		internal virtual void ReadBytes(byte[] bytes)
		{
			int length = bytes.Length;
			System.Array.Copy(_buffer, _offset, bytes, 0, length);
			_offset += length;
		}

		public com.db4o.YapReader ReadEmbeddedObject(com.db4o.Transaction a_trans)
		{
			return a_trans.Stream().ReadReaderByAddress(ReadInt(), ReadInt());
		}

		public virtual void ReadEncrypt(com.db4o.YapStream a_stream, int a_address)
		{
			a_stream.ReadBytes(_buffer, a_address, GetLength());
			a_stream.i_handlers.Decrypt(this);
		}

		public virtual void ReadEnd()
		{
			if (com.db4o.Deploy.debug && com.db4o.Deploy.brackets)
			{
				if (ReadByte() != com.db4o.YapConst.YAPEND)
				{
					throw new j4o.lang.RuntimeException("YapBytes.readEnd() YAPEND expected");
				}
			}
		}

		public int ReadInt()
		{
			int o = (_offset += 4) - 1;
			return (_buffer[o] & 255) | (_buffer[--o] & 255) << 8 | (_buffer[--o] & 255) << 16
				 | _buffer[--o] << 24;
		}

		public virtual com.db4o.YapReader ReadPayloadReader(int offset, int length)
		{
			com.db4o.YapReader payLoad = new com.db4o.YapReader(length);
			System.Array.Copy(_buffer, offset, payLoad._buffer, 0, length);
			return payLoad;
		}

		internal virtual void ReplaceWith(byte[] a_bytes)
		{
			System.Array.Copy(a_bytes, 0, _buffer, 0, GetLength());
		}

		public override string ToString()
		{
			try
			{
				string str = "";
				for (int i = 0; i < _buffer.Length; i++)
				{
					if (i > 0)
					{
						str += " , ";
					}
					str += _buffer[i];
				}
				return str;
			}
			catch (System.Exception e)
			{
				if (com.db4o.Deploy.debug || com.db4o.Debug.atHome)
				{
					j4o.lang.JavaSystem.PrintStackTrace(e);
				}
			}
			return "";
		}

		public virtual void WriteBegin(byte a_identifier)
		{
		}

		public virtual void WriteBitMap(com.db4o.foundation.BitMap4 nullBitMap)
		{
			nullBitMap.WriteTo(_buffer, _offset);
			_offset += nullBitMap.MarshalledLength();
		}

		public virtual void WriteEncrypt(com.db4o.YapFile file, int address, int addressOffset
			)
		{
			file.i_handlers.Encrypt(this);
			Write(file, address, addressOffset);
			file.i_handlers.Decrypt(this);
		}

		public virtual void Write(com.db4o.YapFile file, int address, int addressOffset)
		{
			file.WriteBytes(this, address, addressOffset);
		}

		public virtual void WriteEnd()
		{
			if (com.db4o.Deploy.debug && com.db4o.Deploy.brackets)
			{
				Append(com.db4o.YapConst.YAPEND);
			}
		}

		public void WriteInt(int a_int)
		{
			int o = _offset + 4;
			_offset = o;
			byte[] b = _buffer;
			b[--o] = (byte)a_int;
			b[--o] = (byte)(a_int >>= 8);
			b[--o] = (byte)(a_int >>= 8);
			b[--o] = (byte)(a_int >>= 8);
		}

		public virtual void WriteIDOf(com.db4o.Transaction trans, object obj)
		{
			if (obj == null)
			{
				WriteInt(0);
				return;
			}
			if (obj is com.db4o.YapMeta)
			{
				WriteIDOf(trans, (com.db4o.YapMeta)obj);
				return;
			}
			WriteInt(((int)obj));
		}

		public virtual void WriteIDOf(com.db4o.Transaction trans, com.db4o.YapMeta yapMeta
			)
		{
			if (yapMeta == null)
			{
				WriteInt(0);
				return;
			}
			yapMeta.WriteOwnID(trans, this);
		}

		public virtual void WriteShortString(com.db4o.Transaction trans, string a_string)
		{
			trans.Stream().i_handlers.i_stringHandler.WriteShort(a_string, this);
		}

		public virtual void CopyTo(com.db4o.YapReader to, int fromOffset, int toOffset, int
			 length)
		{
			System.Array.Copy(_buffer, fromOffset, to._buffer, toOffset, length);
		}
	}
}
