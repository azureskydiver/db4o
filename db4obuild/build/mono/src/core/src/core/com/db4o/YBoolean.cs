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
	internal sealed class YBoolean : com.db4o.YapJavaClass
	{
		internal const int LENGTH = 1 + com.db4o.YapConst.ADDED_LENGTH;

		private const byte TRUE = (byte)'T';

		private const byte FALSE = (byte)'F';

		private const byte NULL = (byte)'N';

		private static readonly bool i_primitive = System.Convert.ToBoolean(false);

		public YBoolean(com.db4o.YapStream stream) : base(stream)
		{
		}

		public override int getID()
		{
			return 4;
		}

		public override object defaultValue()
		{
			return i_primitive;
		}

		public override int linkLength()
		{
			return LENGTH;
		}

		protected override j4o.lang.Class primitiveJavaClass()
		{
			return j4o.lang.Class.getClassForType(typeof(bool));
		}

		internal override object primitiveNull()
		{
			return i_primitive;
		}

		internal override object read1(com.db4o.YapReader a_bytes)
		{
			byte ret = a_bytes.readByte();
			if (ret == TRUE)
			{
				return System.Convert.ToBoolean(true);
			}
			if (ret == FALSE)
			{
				return System.Convert.ToBoolean(false);
			}
			return null;
		}

		public override void write(object a_object, com.db4o.YapWriter a_bytes)
		{
			byte set;
			if (a_object == null)
			{
				set = NULL;
			}
			else
			{
				if (((bool)a_object))
				{
					set = TRUE;
				}
				else
				{
					set = FALSE;
				}
			}
			a_bytes.append(set);
		}

		private bool i_compareTo;

		private bool val(object obj)
		{
			return ((bool)obj);
		}

		internal override void prepareComparison1(object obj)
		{
			i_compareTo = val(obj);
		}

		internal override bool isEqual1(object obj)
		{
			return obj is bool && val(obj) == i_compareTo;
		}

		internal override bool isGreater1(object obj)
		{
			if (i_compareTo)
			{
				return false;
			}
			return obj is bool && val(obj);
		}

		internal override bool isSmaller1(object obj)
		{
			if (!i_compareTo)
			{
				return false;
			}
			return obj is bool && !val(obj);
		}
	}
}
