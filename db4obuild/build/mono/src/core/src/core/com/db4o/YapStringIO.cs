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
	public class YapStringIO
	{
		protected char[] chars = new char[0];

		internal virtual int bytesPerChar()
		{
			return 1;
		}

		internal virtual byte encodingByte()
		{
			return com.db4o.YapConst.ISO8859;
		}

		internal static com.db4o.YapStringIO forEncoding(byte encodingByte)
		{
			switch (encodingByte)
			{
				case com.db4o.YapConst.ISO8859:
				{
					return new com.db4o.YapStringIO();
				}

				default:
				{
					return new com.db4o.YapStringIOUnicode();
					break;
				}
			}
		}

		internal virtual int length(string a_string)
		{
			return j4o.lang.JavaSystem.getLengthOf(a_string) + com.db4o.YapConst.OBJECT_LENGTH
				 + com.db4o.YapConst.YAPINT_LENGTH;
		}

		protected virtual void checkBufferLength(int a_length)
		{
			if (a_length > chars.Length)
			{
				chars = new char[a_length];
			}
		}

		public virtual string read(com.db4o.YapReader bytes, int a_length)
		{
			checkBufferLength(a_length);
			for (int ii = 0; ii < a_length; ii++)
			{
				chars[ii] = (char)(bytes._buffer[bytes._offset++] & 0xff);
			}
			return new string(chars, 0, a_length);
		}

		internal virtual string read(byte[] a_bytes)
		{
			checkBufferLength(a_bytes.Length);
			for (int i = 0; i < a_bytes.Length; i++)
			{
				chars[i] = (char)(a_bytes[i] & 0xff);
			}
			return new string(chars, 0, a_bytes.Length);
		}

		internal virtual int shortLength(string a_string)
		{
			return j4o.lang.JavaSystem.getLengthOf(a_string) + com.db4o.YapConst.YAPINT_LENGTH;
		}

		protected virtual int writetoBuffer(string str)
		{
			int len = j4o.lang.JavaSystem.getLengthOf(str);
			checkBufferLength(len);
			j4o.lang.JavaSystem.getCharsForString(str, 0, len, chars, 0);
			return len;
		}

		internal virtual void write(com.db4o.YapReader bytes, string _string)
		{
			int len = writetoBuffer(_string);
			for (int i = 0; i < len; i++)
			{
				bytes._buffer[bytes._offset++] = (byte)(chars[i] & 0xff);
			}
		}

		internal virtual byte[] write(string _string)
		{
			int len = writetoBuffer(_string);
			byte[] bytes = new byte[len];
			for (int i = 0; i < len; i++)
			{
				bytes[i] = (byte)(chars[i] & 0xff);
			}
			return bytes;
		}
	}
}
