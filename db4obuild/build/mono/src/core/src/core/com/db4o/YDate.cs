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
	internal sealed class YDate : com.db4o.YLong
	{
		private static readonly j4o.util.Date PROTO = new j4o.util.Date(0);

		public YDate(com.db4o.YapStream stream) : base(stream)
		{
		}

		public override void copyValue(object a_from, object a_to)
		{
			try
			{
				((j4o.util.Date)a_to).setTime(((j4o.util.Date)a_from).getTime());
			}
			catch (System.Exception e)
			{
			}
		}

		public override object defaultValue()
		{
			return PROTO;
		}

		public override int getID()
		{
			return 10;
		}

		protected override j4o.lang.Class primitiveJavaClass()
		{
			return null;
		}

		internal override object primitiveNull()
		{
			return null;
		}

		internal override object read1(com.db4o.YapReader a_bytes)
		{
			long ret = readLong(a_bytes);
			if (ret == long.MaxValue)
			{
				return null;
			}
			return new j4o.util.Date(ret);
		}

		public override void write(object a_object, com.db4o.YapWriter a_bytes)
		{
			if (a_object == null)
			{
				writeLong(long.MaxValue, a_bytes);
			}
			else
			{
				writeLong(((j4o.util.Date)a_object).getTime(), a_bytes);
			}
		}

		internal static string now()
		{
			return com.db4o.Platform.format(new j4o.util.Date(), true);
		}

		internal override long val(object obj)
		{
			return ((j4o.util.Date)obj).getTime();
		}

		internal override bool isEqual1(object obj)
		{
			return obj is j4o.util.Date && val(obj) == i_compareTo;
		}

		internal override bool isGreater1(object obj)
		{
			return obj is j4o.util.Date && val(obj) > i_compareTo;
		}

		internal override bool isSmaller1(object obj)
		{
			return obj is j4o.util.Date && val(obj) < i_compareTo;
		}
	}
}
