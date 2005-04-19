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
	internal class YapFieldUUID : com.db4o.YapFieldVirtual
	{
		internal YapFieldUUID(com.db4o.YapStream stream) : base()
		{
			i_name = com.db4o.YapConst.VIRTUAL_FIELD_PREFIX + "uuid";
			i_handler = new com.db4o.YLong(stream);
		}

		internal override void addFieldIndex(com.db4o.YapWriter a_writer, bool a_new)
		{
			int offset = a_writer._offset;
			int id = a_writer.readInt();
			long uuid = com.db4o.YLong.readLong(a_writer);
			a_writer._offset = offset;
			com.db4o.YapFile yf = (com.db4o.YapFile)a_writer.getStream();
			if (id == 0)
			{
				a_writer.writeInt(yf.identity().getID(a_writer.getTransaction()));
			}
			else
			{
				a_writer.incrementOffset(com.db4o.YapConst.YAPINT_LENGTH);
			}
			if (uuid == 0)
			{
				uuid = yf.i_bootRecord.newUUID();
			}
			com.db4o.YLong.writeLong(uuid, a_writer);
			if (a_new)
			{
				addIndexEntry(System.Convert.ToInt64(uuid), a_writer);
			}
		}

		internal override com.db4o.IxField getIndex(com.db4o.Transaction a_trans)
		{
			com.db4o.YapFile stream = (com.db4o.YapFile)a_trans.i_stream;
			if (i_index == null)
			{
				com.db4o.PBootRecord bootRecord = stream.i_bootRecord;
				i_index = new com.db4o.IxField(stream.getSystemTransaction(), this, bootRecord.getUUIDMetaIndex
					());
			}
			return i_index;
		}

		internal override void instantiate1(com.db4o.Transaction a_trans, com.db4o.YapObject
			 a_yapObject, com.db4o.YapReader a_bytes)
		{
			int dbID = a_bytes.readInt();
			com.db4o.YapStream stream = a_trans.i_stream;
			stream.showInternalClasses(true);
			com.db4o.ext.Db4oDatabase db = (com.db4o.ext.Db4oDatabase)stream.getByID2(a_trans
				, dbID);
			if (db != null && db.i_signature == null)
			{
				stream.activate2(a_trans, db, 2);
			}
			a_yapObject.i_virtualAttributes.i_database = db;
			a_yapObject.i_virtualAttributes.i_uuid = com.db4o.YLong.readLong(a_bytes);
			stream.showInternalClasses(false);
		}

		public override int linkLength()
		{
			return com.db4o.YapConst.YAPLONG_LENGTH + com.db4o.YapConst.YAPID_LENGTH;
		}

		internal override void marshall1(com.db4o.YapObject a_yapObject, com.db4o.YapWriter
			 a_bytes, bool a_migrating, bool a_new)
		{
			com.db4o.YapStream stream = a_bytes.getStream();
			com.db4o.Transaction trans = a_bytes.getTransaction();
			bool indexEntry = a_new && stream.maintainsIndices();
			int dbID = 0;
			if (!a_migrating)
			{
				if (a_yapObject.i_virtualAttributes.i_database == null)
				{
					a_yapObject.i_virtualAttributes.i_database = stream.identity();
					if (stream is com.db4o.YapFile && ((com.db4o.YapFile)stream).i_bootRecord != null
						)
					{
						com.db4o.PBootRecord bootRecord = ((com.db4o.YapFile)stream).i_bootRecord;
						a_yapObject.i_virtualAttributes.i_uuid = bootRecord.newUUID();
						indexEntry = true;
					}
				}
				com.db4o.ext.Db4oDatabase db = a_yapObject.i_virtualAttributes.i_database;
				if (db != null)
				{
					dbID = db.getID(trans);
				}
			}
			else
			{
				com.db4o.ext.Db4oDatabase db = null;
				if (a_yapObject.i_virtualAttributes != null && a_yapObject.i_virtualAttributes.i_database
					 != null)
				{
					db = a_yapObject.i_virtualAttributes.i_database;
					dbID = db.getID(trans);
				}
			}
			a_bytes.writeInt(dbID);
			if (a_yapObject.i_virtualAttributes != null)
			{
				com.db4o.YLong.writeLong(a_yapObject.i_virtualAttributes.i_uuid, a_bytes);
				if (indexEntry)
				{
					addIndexEntry(System.Convert.ToInt64(a_yapObject.i_virtualAttributes.i_uuid), a_bytes
						);
				}
			}
			else
			{
				com.db4o.YLong.writeLong(0, a_bytes);
			}
		}
	}
}
