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
	internal sealed class YDouble : com.db4o.YLong
	{
		private static readonly double i_primitive = System.Convert.ToDouble(0);

		public YDouble(com.db4o.YapStream stream) : base(stream)
		{
		}

		public override object defaultValue()
		{
			return i_primitive;
		}

		public override int getID()
		{
			return 5;
		}

		protected override j4o.lang.Class primitiveJavaClass()
		{
			return j4o.lang.Class.getClassForType(typeof(double));
		}

		internal override object primitiveNull()
		{
			return i_primitive;
		}

		internal override object read1(com.db4o.YapReader a_bytes)
		{
			long ret = readLong(a_bytes);
			if (ret == long.MaxValue)
			{
				return null;
			}
			return System.Convert.ToDouble(com.db4o.Platform.longToDouble(ret));
		}

		public override void write(object a_object, com.db4o.YapWriter a_bytes)
		{
			if (!com.db4o.Deploy.csharp && a_object == null)
			{
				writeLong(long.MaxValue, a_bytes);
			}
			else
			{
				writeLong(com.db4o.Platform.doubleToLong(((double)a_object)), a_bytes);
			}
		}

		private double i_compareToDouble;

		private double dval(object obj)
		{
			return ((double)obj);
		}

		internal override void prepareComparison1(object obj)
		{
			i_compareToDouble = dval(obj);
		}

		internal override bool isEqual1(object obj)
		{
			return obj is double && dval(obj) == i_compareToDouble;
		}

		internal override bool isGreater1(object obj)
		{
			return obj is double && dval(obj) > i_compareToDouble;
		}

		internal override bool isSmaller1(object obj)
		{
			return obj is double && dval(obj) < i_compareToDouble;
		}
	}
}
