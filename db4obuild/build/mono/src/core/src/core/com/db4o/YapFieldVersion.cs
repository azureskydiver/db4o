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
	internal class YapFieldVersion : com.db4o.YapFieldVirtual
	{
		internal YapFieldVersion() : base()
		{
			i_name = PREFIX + "version";
		}

		internal override void addFieldIndex(com.db4o.YapWriter a_writer, bool a_new)
		{
			com.db4o.YLong.writeLong(((com.db4o.YapFile)a_writer.getStream()).i_bootRecord.version
				(), a_writer);
		}

		internal override void instantiate1(com.db4o.Transaction a_trans, com.db4o.YapObject
			 a_yapObject, com.db4o.YapReader a_bytes)
		{
			a_yapObject.i_virtualAttributes.i_version = com.db4o.YLong.readLong(a_bytes);
		}

		internal override void marshall1(com.db4o.YapObject a_yapObject, com.db4o.YapWriter
			 a_bytes, bool a_migrating, bool a_new)
		{
			if (!a_migrating)
			{
				com.db4o.YapStream stream = a_bytes.getStream().i_parent;
				if (stream is com.db4o.YapFile && ((com.db4o.YapFile)stream).i_bootRecord != null
					)
				{
					a_yapObject.i_virtualAttributes.i_version = ((com.db4o.YapFile)stream).i_bootRecord
						.version();
				}
			}
			if (a_yapObject.i_virtualAttributes == null)
			{
				com.db4o.YLong.writeLong(0, a_bytes);
			}
			else
			{
				com.db4o.YLong.writeLong(a_yapObject.i_virtualAttributes.i_version, a_bytes);
			}
		}

		public override int linkLength()
		{
			return com.db4o.YapConst.YAPLONG_LENGTH;
		}
	}
}
