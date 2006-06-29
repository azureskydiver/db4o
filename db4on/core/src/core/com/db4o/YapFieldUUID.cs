namespace com.db4o
{
	internal class YapFieldUUID : com.db4o.YapFieldVirtual
	{
		private const int LINK_LENGTH = com.db4o.YapConst.YAPLONG_LENGTH + com.db4o.YapConst
			.YAPID_LENGTH;

		internal YapFieldUUID(com.db4o.YapStream stream) : base()
		{
			i_name = com.db4o.YapConst.VIRTUAL_FIELD_PREFIX + "uuid";
			i_handler = new com.db4o.YLong(stream);
		}

		public override void AddFieldIndex(com.db4o.inside.marshall.MarshallerFamily mf, 
			com.db4o.YapWriter writer, bool isnew)
		{
			int offset = writer._offset;
			int id = writer.ReadInt();
			long uuid = com.db4o.YLong.ReadLong(writer);
			writer._offset = offset;
			com.db4o.YapFile yf = (com.db4o.YapFile)writer.GetStream();
			if (id == 0)
			{
				writer.WriteInt(yf.Identity().GetID(writer.GetTransaction()));
			}
			else
			{
				writer.IncrementOffset(com.db4o.YapConst.YAPINT_LENGTH);
			}
			if (uuid == 0)
			{
				uuid = yf.BootRecord().NewUUID();
			}
			com.db4o.YLong.WriteLong(uuid, writer);
			if (isnew)
			{
				AddIndexEntry(writer, uuid);
			}
		}

		public override void Delete(com.db4o.inside.marshall.MarshallerFamily mf, com.db4o.YapWriter
			 a_bytes, bool isUpdate)
		{
			if (isUpdate)
			{
				a_bytes.IncrementOffset(LinkLength());
				return;
			}
			a_bytes.IncrementOffset(com.db4o.YapConst.YAPINT_LENGTH);
			long longPart = com.db4o.YLong.ReadLong(a_bytes);
			if (longPart > 0)
			{
				com.db4o.YapStream stream = a_bytes.GetStream();
				if (stream.MaintainsIndices())
				{
					RemoveIndexEntry(a_bytes.GetTransaction(), a_bytes.GetID(), longPart);
				}
			}
		}

		internal override com.db4o.inside.ix.Index4 GetIndex(com.db4o.Transaction a_trans
			)
		{
			if (i_index != null)
			{
				return i_index;
			}
			com.db4o.YapFile stream = (com.db4o.YapFile)a_trans.i_stream;
			if (i_index == null)
			{
				i_index = new com.db4o.inside.ix.Index4(stream.GetSystemTransaction(), GetHandler
					(), stream.BootRecord().GetUUIDMetaIndex(), false);
			}
			return i_index;
		}

		internal override bool HasIndex()
		{
			return true;
		}

		internal override void Instantiate1(com.db4o.Transaction a_trans, com.db4o.YapObject
			 a_yapObject, com.db4o.YapReader a_bytes)
		{
			int dbID = a_bytes.ReadInt();
			com.db4o.YapStream stream = a_trans.i_stream;
			stream.ShowInternalClasses(true);
			com.db4o.ext.Db4oDatabase db = (com.db4o.ext.Db4oDatabase)stream.GetByID2(a_trans
				, dbID);
			if (db != null && db.i_signature == null)
			{
				stream.Activate2(a_trans, db, 2);
			}
			a_yapObject.i_virtualAttributes.i_database = db;
			a_yapObject.i_virtualAttributes.i_uuid = com.db4o.YLong.ReadLong(a_bytes);
			stream.ShowInternalClasses(false);
		}

		public override int LinkLength()
		{
			return LINK_LENGTH;
		}

		internal override void Marshall1(com.db4o.YapObject a_yapObject, com.db4o.YapWriter
			 a_bytes, bool a_migrating, bool a_new)
		{
			com.db4o.YapStream stream = a_bytes.GetStream();
			com.db4o.Transaction trans = a_bytes.GetTransaction();
			bool indexEntry = a_new && stream.MaintainsIndices();
			int dbID = 0;
			com.db4o.VirtualAttributes attr = a_yapObject.i_virtualAttributes;
			bool linkToDatabase = !a_migrating;
			if (attr != null && attr.i_database == null)
			{
				linkToDatabase = true;
			}
			if (linkToDatabase)
			{
				com.db4o.ext.Db4oDatabase db = stream.Identity();
				if (db == null)
				{
					attr = null;
				}
				else
				{
					if (attr.i_database == null)
					{
						attr.i_database = db;
						if (stream is com.db4o.YapFile)
						{
							com.db4o.PBootRecord br = stream.BootRecord();
							if (br != null)
							{
								attr.i_uuid = br.NewUUID();
								indexEntry = true;
							}
						}
					}
					db = attr.i_database;
					if (db != null)
					{
						dbID = db.GetID(trans);
					}
				}
			}
			else
			{
				if (attr != null)
				{
					dbID = attr.i_database.GetID(trans);
				}
			}
			a_bytes.WriteInt(dbID);
			if (attr != null)
			{
				com.db4o.YLong.WriteLong(attr.i_uuid, a_bytes);
				if (indexEntry)
				{
					AddIndexEntry(a_bytes, attr.i_uuid);
				}
			}
			else
			{
				com.db4o.YLong.WriteLong(0, a_bytes);
			}
		}

		internal override void MarshallIgnore(com.db4o.YapWriter writer)
		{
			writer.WriteInt(0);
			com.db4o.YLong.WriteLong(0, writer);
		}
	}
}
