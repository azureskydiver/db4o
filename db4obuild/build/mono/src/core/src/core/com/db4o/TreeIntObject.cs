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
	internal class TreeIntObject : com.db4o.TreeInt
	{
		internal object i_object;

		internal TreeIntObject(int a_key) : base(a_key)
		{
		}

		internal TreeIntObject(int a_key, object a_object) : base(a_key)
		{
			i_object = a_object;
		}

		public override object read(com.db4o.YapReader a_bytes)
		{
			int key = a_bytes.readInt();
			object obj = null;
			if (i_object is com.db4o.Tree)
			{
				obj = new com.db4o.TreeReader(a_bytes, (com.db4o.Tree)i_object).read();
			}
			else
			{
				obj = ((com.db4o.Readable)i_object).read(a_bytes);
			}
			return new com.db4o.TreeIntObject(key, obj);
		}

		public override void write(com.db4o.YapWriter a_writer)
		{
			a_writer.writeInt(i_key);
			if (i_object == null)
			{
				a_writer.writeInt(0);
			}
			else
			{
				if (i_object is com.db4o.Tree)
				{
					com.db4o.Tree.write(a_writer, (com.db4o.Tree)i_object);
				}
				else
				{
					((com.db4o.ReadWriteable)i_object).write(a_writer);
				}
			}
		}

		internal override int ownLength()
		{
			if (i_object == null)
			{
				return com.db4o.YapConst.YAPINT_LENGTH * 2;
			}
			else
			{
				return com.db4o.YapConst.YAPINT_LENGTH + ((com.db4o.Readable)i_object).byteCount(
					);
			}
		}

		internal override bool variableLength()
		{
			return true;
		}
	}
}
