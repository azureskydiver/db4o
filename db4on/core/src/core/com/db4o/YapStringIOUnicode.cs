namespace com.db4o
{
	/// <exclude></exclude>
	public sealed class YapStringIOUnicode : com.db4o.YapStringIO
	{
		internal override int BytesPerChar()
		{
			return 2;
		}

		internal override byte EncodingByte()
		{
			return com.db4o.YapConst.UNICODE;
		}

		public override int Length(string a_string)
		{
			return (a_string.Length * 2) + com.db4o.YapConst.OBJECT_LENGTH + com.db4o.YapConst
				.YAPINT_LENGTH;
		}

		public override string Read(com.db4o.YapReader bytes, int a_length)
		{
			CheckBufferLength(a_length);
			for (int ii = 0; ii < a_length; ii++)
			{
				chars[ii] = (char)((bytes._buffer[bytes._offset++] & unchecked((int)(0xff))) | ((
					bytes._buffer[bytes._offset++] & unchecked((int)(0xff))) << 8));
			}
			return new string(chars, 0, a_length);
		}

		internal override string Read(byte[] a_bytes)
		{
			int len = a_bytes.Length / 2;
			CheckBufferLength(len);
			int j = 0;
			for (int ii = 0; ii < len; ii++)
			{
				chars[ii] = (char)((a_bytes[j++] & unchecked((int)(0xff))) | ((a_bytes[j++] & unchecked(
					(int)(0xff))) << 8));
			}
			return new string(chars, 0, len);
		}

		internal override int ShortLength(string a_string)
		{
			return (a_string.Length * 2) + com.db4o.YapConst.YAPINT_LENGTH;
		}

		public override void Write(com.db4o.YapReader bytes, string _string)
		{
			int len = WritetoBuffer(_string);
			for (int i = 0; i < len; i++)
			{
				bytes._buffer[bytes._offset++] = (byte)(chars[i] & unchecked((int)(0xff)));
				bytes._buffer[bytes._offset++] = (byte)(chars[i] >> 8);
			}
		}

		internal override byte[] Write(string _string)
		{
			int len = WritetoBuffer(_string);
			byte[] bytes = new byte[len * 2];
			int j = 0;
			for (int i = 0; i < len; i++)
			{
				bytes[j++] = (byte)(chars[i] & unchecked((int)(0xff)));
				bytes[j++] = (byte)(chars[i] >> 8);
			}
			return bytes;
		}
	}
}
