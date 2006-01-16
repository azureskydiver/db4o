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

		public override void appendEmbedded2(com.db4o.YapWriter a_bytes)
		{
			a_bytes.incrementOffset(linkLength());
		}

		public override bool alive()
		{
			return true;
		}

		internal override bool canAddToQuery(string fieldName)
		{
			return fieldName.Equals(getName());
		}

		internal override void collectConstraints(com.db4o.Transaction a_trans, com.db4o.QConObject
			 a_parent, object a_template, com.db4o.foundation.Visitor4 a_visitor)
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
				i_index = new com.db4o.inside.ix.Index4(a_stream.getSystemTransaction(), getHandler
					(), a_metaIndex, false);
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
			com.db4o.Transaction trans = a_bytes.i_trans;
			com.db4o.YapStream stream = trans.i_stream;
			com.db4o.YapHandlers handlers = stream.i_handlers;
			bool migrating = false;
			if (stream._replicationCallState != com.db4o.inside.replication.ReplicationHandler
				.NONE)
			{
				if (stream._replicationCallState == com.db4o.inside.replication.ReplicationHandler
					.OLD)
				{
					migrating = true;
					if (a_yapObject.i_virtualAttributes == null)
					{
						object obj = a_yapObject.getObject();
						com.db4o.YapObject migrateYapObject = null;
						com.db4o.inside.replication.MigrationConnection mgc = handlers.i_migration;
						if (mgc != null)
						{
							migrateYapObject = mgc.referenceFor(obj);
							if (migrateYapObject == null)
							{
								migrateYapObject = mgc.peer(stream).getYapObject(obj);
							}
						}
						if (migrateYapObject != null && migrateYapObject.i_virtualAttributes != null && migrateYapObject
							.i_virtualAttributes.i_database != null)
						{
							migrating = true;
							a_yapObject.i_virtualAttributes = migrateYapObject.i_virtualAttributes.shallowClone
								();
							if (migrateYapObject.i_virtualAttributes.i_database != null)
							{
								migrateYapObject.i_virtualAttributes.i_database.bind(trans);
							}
						}
					}
				}
				else
				{
					com.db4o.inside.replication.ReplicationHandler handler = handlers._replicationHandler;
					object parentObject = a_yapObject.getObject();
					com.db4o.ext.Db4oDatabase db = handler.providerFor(parentObject);
					if (db != null)
					{
						migrating = true;
						if (a_yapObject.i_virtualAttributes == null)
						{
							a_yapObject.i_virtualAttributes = new com.db4o.VirtualAttributes();
						}
						com.db4o.VirtualAttributes va = a_yapObject.i_virtualAttributes;
						va.i_version = handler.versionFor(parentObject);
						va.i_uuid = handler.uuidLongFor(parentObject);
						va.i_database = handler.providerFor(parentObject);
					}
				}
			}
			if (a_yapObject.i_virtualAttributes == null)
			{
				a_yapObject.i_virtualAttributes = new com.db4o.VirtualAttributes();
				migrating = false;
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
