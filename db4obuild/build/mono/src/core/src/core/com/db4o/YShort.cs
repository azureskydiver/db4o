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
	internal sealed class YShort : com.db4o.YapJavaClass
	{
		internal const int LENGTH = com.db4o.YapConst.SHORT_BYTES + com.db4o.YapConst.ADDED_LENGTH;

		private static readonly short i_primitive = System.Convert.ToInt16((short)0);

		public YShort(com.db4o.YapStream stream) : base(stream)
		{
		}

		public override object defaultValue()
		{
			return i_primitive;
		}

		public override int getID()
		{
			return 8;
		}

		public override int linkLength()
		{
			return LENGTH;
		}

		protected override j4o.lang.Class primitiveJavaClass()
		{
			return j4o.lang.Class.getClassForType(typeof(short));
		}

		internal override object primitiveNull()
		{
			return i_primitive;
		}

		internal override object read1(com.db4o.YapReader a_bytes)
		{
			short ret = readShort(a_bytes);
			if (ret == short.MaxValue)
			{
				return null;
			}
			return System.Convert.ToInt16(ret);
		}

		internal static short readShort(com.db4o.YapReader a_bytes)
		{
			int ret = 0;
			for (int i = 0; i < com.db4o.YapConst.SHORT_BYTES; i++)
			{
				ret = (ret << 8) + (a_bytes._buffer[a_bytes._offset++] & 0xff);
			}
			return (short)ret;
		}

		public override void write(object a_object, com.db4o.YapWriter a_bytes)
		{
			if (!com.db4o.Deploy.csharp && a_object == null)
			{
				writeShort(short.MaxValue, a_bytes);
			}
			else
			{
				writeShort(((short)a_object), a_bytes);
			}
		}

		internal static void writeShort(int a_short, com.db4o.YapWriter a_bytes)
		{
			for (int i = 0; i < com.db4o.YapConst.SHORT_BYTES; i++)
			{
				a_bytes._buffer[a_bytes._offset++] = (byte)(a_short >> ((com.db4o.YapConst.SHORT_BYTES
					 - 1 - i) * 8));
			}
		}

		private short i_compareTo;

		private short val(object obj)
		{
			return ((short)obj);
		}

		internal override void prepareComparison1(object obj)
		{
			i_compareTo = val(obj);
		}

		internal override bool isEqual1(object obj)
		{
			return obj is short && val(obj) == i_compareTo;
		}

		internal override bool isGreater1(object obj)
		{
			return obj is short && val(obj) > i_compareTo;
		}

		internal override bool isSmaller1(object obj)
		{
			return obj is short && val(obj) < i_compareTo;
		}
	}
}
