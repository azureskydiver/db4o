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
	internal sealed class YapStringIOUnicode : com.db4o.YapStringIO
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

		internal override string read(com.db4o.YapReader bytes, int a_length)
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
