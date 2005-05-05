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
	internal abstract class YapFieldVirtual : com.db4o.YapField
	{
		internal YapFieldVirtual() : base(null)
		{
		}

		internal override void addFieldIndex(com.db4o.YapWriter a_writer, bool a_new)
		{
			a_writer.incrementOffset(linkLength());
		}

		public override bool alive()
		{
			return true;
		}

		internal override void collectConstraints(com.db4o.Transaction a_trans, com.db4o.QConObject
			 a_parent, object a_template, com.db4o.Visitor4 a_visitor)
		{
		}

		internal override void deactivate(com.db4o.Transaction a_trans, object a_onObject
			, int a_depth)
		{
		}

		internal override void delete(com.db4o.YapWriter a_bytes)
		{
			a_bytes.incrementOffset(linkLength());
		}

		internal override object getOrCreate(com.db4o.Transaction a_trans, object a_OnObject
			)
		{
			return null;
		}

		internal override int ownLength(com.db4o.YapStream a_stream)
		{
			return a_stream.stringIO().shortLength(i_name);
		}

		internal virtual void initIndex(com.db4o.YapStream a_stream, com.db4o.MetaIndex a_metaIndex
			)
		{
			if (i_index == null)
			{
				i_index = new com.db4o.IxField(a_stream.getSystemTransaction(), this, a_metaIndex
					);
			}
		}

		internal override void instantiate(com.db4o.YapObject a_yapObject, object a_onObject
			, com.db4o.YapWriter a_bytes)
		{
			if (a_yapObject.i_virtualAttributes == null)
			{
				a_yapObject.i_virtualAttributes = new com.db4o.VirtualAttributes();
			}
			instantiate1(a_bytes.getTransaction(), a_yapObject, a_bytes);
		}

		internal abstract void instantiate1(com.db4o.Transaction a_trans, com.db4o.YapObject
			 a_yapObject, com.db4o.YapReader a_bytes);

		internal override void loadHandler(com.db4o.YapStream a_stream)
		{
		}

		internal override void marshall(com.db4o.YapObject a_yapObject, object a_object, 
			com.db4o.YapWriter a_bytes, com.db4o.Config4Class a_config, bool a_new)
		{
			com.db4o.YapStream stream = a_bytes.i_trans.i_stream;
			bool migrating = false;
			if (stream is com.db4o.YapFile)
			{
				if (stream.i_migrateFrom != null)
				{
					migrating = true;
					if (a_yapObject.i_virtualAttributes == null)
					{
						object obj = a_yapObject.getObject();
						com.db4o.YapObject migrateYapObject = null;
						if (stream.i_handlers.i_migration != null)
						{
							migrateYapObject = stream.i_handlers.i_migration.referenceFor(obj);
						}
						if (migrateYapObject == null)
						{
							migrateYapObject = stream.i_migrateFrom.getYapObject(obj);
						}
						if (migrateYapObject != null && migrateYapObject.i_virtualAttributes != null && migrateYapObject
							.i_virtualAttributes.i_database != null)
						{
							migrating = true;
							a_yapObject.i_virtualAttributes = migrateYapObject.i_virtualAttributes.shallowClone
								();
							a_bytes.getTransaction().ensureDb4oDatabase(migrateYapObject.i_virtualAttributes.
								i_database);
						}
					}
				}
				if (a_yapObject.i_virtualAttributes == null)
				{
					a_yapObject.i_virtualAttributes = new com.db4o.VirtualAttributes();
					migrating = false;
				}
			}
			else
			{
				migrating = true;
			}
			marshall1(a_yapObject, a_bytes, migrating, a_new);
		}

		internal abstract void marshall1(com.db4o.YapObject a_yapObject, com.db4o.YapWriter
			 a_bytes, bool a_migrating, bool a_new);

		public override void readVirtualAttribute(com.db4o.Transaction a_trans, com.db4o.YapReader
			 a_reader, com.db4o.YapObject a_yapObject)
		{
			instantiate1(a_trans, a_yapObject, a_reader);
		}

		internal override void writeThis(com.db4o.YapWriter a_writer, com.db4o.YapClass a_onClass
			)
		{
			a_writer.writeShortString(i_name);
		}
	}
}
