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
	/// YapString
	/// Legacy rename for C# obfuscator production trouble
	/// </summary>
	/// <exclude></exclude>
	public sealed class YapString : com.db4o.YapIndependantType
	{
		public com.db4o.YapStringIO i_stringIo;

		public YapString(com.db4o.YapStream stream, com.db4o.YapStringIO stringIO) : base
			(stream)
		{
			i_stringIo = stringIO;
		}

		public override void appendEmbedded3(com.db4o.YapWriter a_bytes)
		{
			com.db4o.YapWriter bytes = a_bytes.readEmbeddedObject();
			if (bytes != null)
			{
				a_bytes.addEmbedded(bytes);
			}
		}

		public override bool canHold(com.db4o.reflect.ReflectClass claxx)
		{
			return claxx == classReflector();
		}

		public override void cascadeActivation(com.db4o.Transaction a_trans, object a_object
			, int a_depth, bool a_activate)
		{
		}

		internal static string cipher(long l)
		{
			string str1 = System.Convert.ToString(l);
			string str2 = "";
			for (int i = 0; i < j4o.lang.JavaSystem.getLengthOf(str1); i++)
			{
				str2 += j4o.lang.JavaSystem.getCharAt(str1, i);
			}
			return str2;
		}

		public override com.db4o.reflect.ReflectClass classReflector()
		{
			return _stream.i_handlers.ICLASS_STRING;
		}

		public override bool equals(com.db4o.YapDataType a_dataType)
		{
			return (this == a_dataType);
		}

		public override int getID()
		{
			return 9;
		}

		internal byte getIdentifier()
		{
			return com.db4o.YapConst.YAPSTRING;
		}

		public override com.db4o.YapClass getYapClass(com.db4o.YapStream a_stream)
		{
			return a_stream.i_handlers.i_yapClasses[getID() - 1];
		}

		internal static string invert(string str)
		{
			j4o.lang.StringBuffer buf = new j4o.lang.StringBuffer();
			try
			{
				for (int i = j4o.lang.JavaSystem.getLengthOf(str) - 1; i >= 0; i--)
				{
					buf.append(j4o.lang.JavaSystem.getCharAt(str, i));
				}
			}
			catch (System.Exception e)
			{
				j4o.lang.JavaSystem.printStackTrace(e);
			}
			return buf.ToString();
		}

		public override object indexObject(com.db4o.Transaction a_trans, object a_object)
		{
			if (a_object != null)
			{
				int[] slot = (int[])a_object;
				return a_trans.i_stream.readObjectReaderByAddress(slot[0], slot[1]);
			}
			return null;
		}

		internal static string licenseEncrypt(string str)
		{
			str = str.ToLower();
			string ret = "";
			for (int i = 0; i < j4o.lang.JavaSystem.getLengthOf(str); i++)
			{
				char ch = (char)(j4o.lang.JavaSystem.getCharAt(str, i) + ((char)i) + 1);
				ret += ch;
			}
			return ret;
		}

		public override object read(com.db4o.YapWriter a_bytes)
		{
			i_lastIo = a_bytes.readEmbeddedObject();
			return read1(i_lastIo);
		}

		internal object read1(com.db4o.YapReader bytes)
		{
			if (bytes == null)
			{
				return null;
			}
			string ret = readShort(bytes);
			return ret;
		}

		public override com.db4o.YapDataType readArrayWrapper(com.db4o.Transaction a_trans
			, com.db4o.YapReader[] a_bytes)
		{
			return null;
		}

		public override void readCandidates(com.db4o.YapReader a_bytes, com.db4o.QCandidates
			 a_candidates)
		{
		}

		public override object readIndexEntry(com.db4o.YapReader a_reader)
		{
			return new int[] { a_reader.readInt(), a_reader.readInt() };
		}

		public override object readQuery(com.db4o.Transaction a_trans, com.db4o.YapReader
			 a_reader, bool a_toArray)
		{
			com.db4o.YapReader reader = a_reader.readEmbeddedObject(a_trans);
			if (a_toArray)
			{
				if (reader != null)
				{
					return reader.toString(a_trans);
				}
			}
			return reader;
		}

		internal string readShort(com.db4o.YapReader a_bytes)
		{
			int length = a_bytes.readInt();
			if (length > com.db4o.YapConst.MAXIMUM_BLOCK_SIZE)
			{
				throw new com.db4o.CorruptionException();
			}
			if (length > 0)
			{
				return i_stringIo.read(a_bytes, length);
			}
			return "";
		}

		internal void setStringIo(com.db4o.YapStringIO a_io)
		{
			i_stringIo = a_io;
		}

		public override bool supportsIndex()
		{
			return true;
		}

		public override void writeIndexEntry(com.db4o.YapWriter a_writer, object a_object
			)
		{
			if (a_object == null)
			{
				a_writer.writeInt(0);
				a_writer.writeInt(0);
			}
			else
			{
				int[] slot = (int[])a_object;
				a_writer.writeInt(slot[0]);
				a_writer.writeInt(slot[1]);
			}
		}

		public override void writeNew(object a_object, com.db4o.YapWriter a_bytes)
		{
			if (a_object == null)
			{
				a_bytes.writeEmbeddedNull();
			}
			else
			{
				string str = (string)a_object;
				int length = i_stringIo.length(str);
				com.db4o.YapWriter bytes = new com.db4o.YapWriter(a_bytes.getTransaction(), length
					);
				bytes.writeInt(j4o.lang.JavaSystem.getLengthOf(str));
				i_stringIo.write(bytes, str);
				bytes.setID(a_bytes._offset);
				i_lastIo = bytes;
				a_bytes.getStream().writeEmbedded(a_bytes, bytes);
				a_bytes.incrementOffset(com.db4o.YapConst.YAPID_LENGTH);
				a_bytes.writeInt(length);
			}
		}

		internal void writeShort(string a_string, com.db4o.YapReader a_bytes)
		{
			if (a_string == null)
			{
				a_bytes.writeInt(0);
			}
			else
			{
				a_bytes.writeInt(j4o.lang.JavaSystem.getLengthOf(a_string));
				i_stringIo.write(a_bytes, a_string);
			}
		}

		internal static string fromIntArray(int[] ints)
		{
			j4o.lang.StringBuffer buf = new j4o.lang.StringBuffer();
			try
			{
				for (int i = 0; i < ints.Length; i++)
				{
					buf.append((char)ints[i]);
				}
			}
			catch (System.Exception e)
			{
				j4o.lang.JavaSystem.printStackTrace(e);
			}
			return buf.ToString();
		}

		public override int getType()
		{
			return com.db4o.YapConst.TYPE_SIMPLE;
		}

		private com.db4o.YapReader i_compareTo;

		private com.db4o.YapReader val(object obj)
		{
			if (obj is com.db4o.YapReader)
			{
				return (com.db4o.YapReader)obj;
			}
			if (obj is string)
			{
				string str = (string)obj;
				com.db4o.YapReader reader = new com.db4o.YapReader(i_stringIo.length(str));
				writeShort(str, reader);
				return reader;
			}
			return null;
		}

		public override void prepareLastIoComparison(com.db4o.Transaction a_trans, object
			 obj)
		{
			if (obj == null)
			{
				i_compareTo = null;
			}
			else
			{
				i_compareTo = i_lastIo;
			}
		}

		public override com.db4o.YapComparable prepareComparison(object obj)
		{
			if (obj == null)
			{
				i_compareTo = null;
				return com.db4o.Null.INSTANCE;
			}
			i_compareTo = val(obj);
			return this;
		}

		public override int compareTo(object obj)
		{
			if (i_compareTo == null)
			{
				if (obj == null)
				{
					return 0;
				}
				return 1;
			}
			return compare(i_compareTo, val(obj));
		}

		public override bool isEqual(object obj)
		{
			if (i_compareTo == null)
			{
				return obj == null;
			}
			return i_compareTo.containsTheSame(val(obj));
		}

		public override bool isGreater(object obj)
		{
			if (i_compareTo == null)
			{
				return obj != null;
			}
			return compare(i_compareTo, val(obj)) > 0;
		}

		public override bool isSmaller(object obj)
		{
			if (i_compareTo == null)
			{
				return false;
			}
			return compare(i_compareTo, val(obj)) < 0;
		}

		/// <summary>
		/// returns: -x for left is greater and +x for right is greater
		/// TODO: You will need collators here for different languages.
		/// </summary>
		/// <remarks>
		/// returns: -x for left is greater and +x for right is greater
		/// TODO: You will need collators here for different languages.
		/// </remarks>
		internal int compare(com.db4o.YapReader a_compare, com.db4o.YapReader a_with)
		{
			if (a_compare == null)
			{
				if (a_with == null)
				{
					return 0;
				}
				return 1;
			}
			if (a_with == null)
			{
				return -1;
			}
			return compare(a_compare._buffer, a_with._buffer);
		}

		internal static int compare(byte[] compare, byte[] with)
		{
			int min = compare.Length < with.Length ? compare.Length : with.Length;
			int start = com.db4o.YapConst.YAPINT_LENGTH;
			for (int i = start; i < min; i++)
			{
				if (compare[i] != with[i])
				{
					return with[i] - compare[i];
				}
			}
			return with.Length - compare.Length;
		}
	}
}
