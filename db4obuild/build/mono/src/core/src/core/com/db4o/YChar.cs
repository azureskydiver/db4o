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
	internal sealed class YChar : com.db4o.YapJavaClass
	{
		internal const int LENGTH = com.db4o.YapConst.CHAR_BYTES + com.db4o.YapConst.ADDED_LENGTH;

		private static readonly char i_primitive = System.Convert.ToChar((char)0);

		public YChar(com.db4o.YapStream stream) : base(stream)
		{
		}

		public override object defaultValue()
		{
			return i_primitive;
		}

		public override int getID()
		{
			return 7;
		}

		public override int linkLength()
		{
			return LENGTH;
		}

		protected override j4o.lang.Class primitiveJavaClass()
		{
			return j4o.lang.Class.getClassForType(typeof(char));
		}

		internal override object primitiveNull()
		{
			return i_primitive;
		}

		internal override object read1(com.db4o.YapReader a_bytes)
		{
			byte b1 = a_bytes.readByte();
			byte b2 = a_bytes.readByte();
			char ret = (char)((b1 & 0xff) | ((b2 & 0xff) << 8));
			return System.Convert.ToChar(ret);
		}

		public override void write(object a_object, com.db4o.YapWriter a_bytes)
		{
			char l_char;
			if (!com.db4o.Deploy.csharp && a_object == null)
			{
				l_char = char.MaxValue;
			}
			else
			{
				l_char = ((char)a_object);
			}
			a_bytes.append((byte)(l_char & 0xff));
			a_bytes.append((byte)(l_char >> 8));
		}

		private char i_compareTo;

		private char val(object obj)
		{
			return ((char)obj);
		}

		internal override void prepareComparison1(object obj)
		{
			i_compareTo = val(obj);
		}

		internal override bool isEqual1(object obj)
		{
			return obj is char && val(obj) == i_compareTo;
		}

		internal override bool isGreater1(object obj)
		{
			return obj is char && val(obj) > i_compareTo;
		}

		internal override bool isSmaller1(object obj)
		{
			return obj is char && val(obj) < i_compareTo;
		}
	}
}
