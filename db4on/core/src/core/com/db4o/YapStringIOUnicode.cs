namespace com.db4o
{
	/// <exclude></exclude>
	public sealed class YapStringIOUnicode : com.db4o.YapStringIO
	{
		internal override int bytesPerChar()
		{
			return 2;
		}

		internal override byte encodingByte()
		{
			return com.db4o.YapConst.UNICODE;
		}

		internal override int length(string a_string)
		{
			return (j4o.lang.JavaSystem.getLengthOf(a_string) * 2) + com.db4o.YapConst.OBJECT_LENGTH
				 + com.db4o.YapConst.YAPINT_LENGTH;
		}

		public override string read(com.db4o.YapReader bytes, int a_length)
		{
			checkBufferLength(a_length);
			for (int ii = 0; ii < a_length; ii++)
			{
				chars[ii] = (char)((bytes._buffer[bytes._offset++] & 0xff) | ((bytes._buffer[bytes
					._offset++] & 0xff) << 8));
			}
			return new string(chars, 0, a_length);
		}

		internal override string read(byte[] a_bytes)
		{
			int len = a_bytes.Length / 2;
			checkBufferLength(len);
			int j = 0;
			for (int ii = 0; ii < len; ii++)
			{
				chars[ii] = (char)((a_bytes[j++] & 0xff) | ((a_bytes[j++] & 0xff) << 8));
			}
			return new string(chars, 0, len);
		}

		internal override int shortLength(string a_string)
		{
			return (j4o.lang.JavaSystem.getLengthOf(a_string) * 2) + com.db4o.YapConst.YAPINT_LENGTH;
		}

		internal override void write(com.db4o.YapReader bytes, string _string)
		{
			int len = writetoBuffer(_string);
			for (int i = 0; i < len; i++)
			{
				bytes._buffer[bytes._offset++] = (byte)(chars[i] & 0xff);
				bytes._buffer[bytes._offset++] = (byte)(chars[i] >> 8);
			}
		}

		internal override byte[] write(string _string)
		{
			int len = writetoBuffer(_string);
			byte[] bytes = new byte[len * 2];
			int j = 0;
			for (int i = 0; i < len; i++)
			{
				bytes[j++] = (byte)(chars[i] & 0xff);
				bytes[j++] = (byte)(chars[i] >> 8);
			}
			return bytes;
		}
	}
}
