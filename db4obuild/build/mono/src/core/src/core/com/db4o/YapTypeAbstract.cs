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
	internal abstract class YapTypeAbstract : com.db4o.YapJavaClass, com.db4o.YapType
	{
		public YapTypeAbstract(com.db4o.YapStream stream) : base(stream)
		{
		}

		private int i_linkLength;

		private object i_compareTo;

		public abstract int typeID();

		public abstract void write(object obj, byte[] bytes, int offset);

		public abstract object read(byte[] bytes, int offset);

		public abstract int compare(object compare, object with);

		public abstract bool isEqual(object compare, object with);

		internal virtual void initialize()
		{
			byte[] bytes = new byte[65];
			for (int i = 0; i < bytes.Length; i++)
			{
				bytes[i] = 55;
			}
			write(primitiveNull(), bytes, 0);
			for (int i = 0; i < bytes.Length; i++)
			{
				if (bytes[i] == 55)
				{
					i_linkLength = i;
					break;
				}
			}
		}

		internal override object primitiveNull()
		{
			return defaultValue();
		}

		public override void write(object a_object, com.db4o.YapWriter a_bytes)
		{
			int offset = a_bytes._offset;
			if (a_object != null)
			{
				write(a_object, a_bytes._buffer, a_bytes._offset);
			}
			a_bytes._offset = offset + linkLength();
		}

		public override int getID()
		{
			return typeID();
		}

		public override int linkLength()
		{
			return i_linkLength;
		}

		protected override j4o.lang.Class primitiveJavaClass()
		{
			return null;
		}

		internal override object read1(com.db4o.YapReader a_bytes)
		{
			int offset = a_bytes._offset;
			object ret = read(a_bytes._buffer, a_bytes._offset);
			a_bytes._offset = offset + linkLength();
			return ret;
		}

		internal override void prepareComparison1(object obj)
		{
			i_compareTo = obj;
		}

		internal override bool isEqual1(object obj)
		{
			return isEqual(i_compareTo, obj);
		}

		internal override bool isGreater1(object obj)
		{
			if (classReflector().isInstance(obj) && !isEqual(i_compareTo, obj))
			{
				return compare(i_compareTo, obj) > 0;
			}
			return false;
		}

		internal override bool isSmaller1(object obj)
		{
			if (classReflector().isInstance(obj) && !isEqual(i_compareTo, obj))
			{
				return compare(i_compareTo, obj) < 0;
			}
			return false;
		}
	}
}
