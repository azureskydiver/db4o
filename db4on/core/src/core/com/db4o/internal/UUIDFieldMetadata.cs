namespace com.db4o.@internal
{
	/// <exclude></exclude>
	public class UUIDFieldMetadata : com.db4o.@internal.VirtualFieldMetadata
	{
		private const int LINK_LENGTH = com.db4o.@internal.Const4.LONG_LENGTH + com.db4o.@internal.Const4
			.ID_LENGTH;

		internal UUIDFieldMetadata(com.db4o.@internal.ObjectContainerBase stream) : base(
			)
		{
			i_name = com.db4o.@internal.Const4.VIRTUAL_FIELD_PREFIX + "uuid";
			i_handler = new com.db4o.@internal.handlers.LongHandler(stream);
		}

		public override void AddFieldIndex(com.db4o.@internal.marshall.MarshallerFamily mf
			, com.db4o.@internal.ClassMetadata yapClass, com.db4o.@internal.StatefulBuffer writer
			, com.db4o.@internal.slots.Slot oldSlot)
		{
			bool isnew = (oldSlot == null);
			int offset = writer._offset;
			int db4oDatabaseIdentityID = writer.ReadInt();
			long uuid = writer.ReadLong();
			writer._offset = offset;
			com.db4o.@internal.LocalObjectContainer yf = (com.db4o.@internal.LocalObjectContainer
				)writer.GetStream();
			if ((uuid == 0 || db4oDatabaseIdentityID == 0) && writer.GetID() > 0 && !isnew)
			{
				com.db4o.@internal.UUIDFieldMetadata.DatabaseIdentityIDAndUUID identityAndUUID = 
					ReadDatabaseIdentityIDAndUUID(yf, yapClass, oldSlot, false);
				db4oDatabaseIdentityID = identityAndUUID.databaseIdentityID;
				uuid = identityAndUUID.uuid;
			}
			if (db4oDatabaseIdentityID == 0)
			{
				db4oDatabaseIdentityID = yf.Identity().GetID(writer.GetTransaction());
			}
			if (uuid == 0)
			{
				uuid = yf.GenerateTimeStampId();
			}
			writer.WriteInt(db4oDatabaseIdentityID);
			writer.WriteLong(uuid);
			if (isnew)
			{
				AddIndexEntry(writer, uuid);
			}
		}

		internal class DatabaseIdentityIDAndUUID
		{
			public int databaseIdentityID;

			public long uuid;

			public DatabaseIdentityIDAndUUID(int databaseIdentityID_, long uuid_)
			{
				databaseIdentityID = databaseIdentityID_;
				uuid = uuid_;
			}
		}

		private com.db4o.@internal.UUIDFieldMetadata.DatabaseIdentityIDAndUUID ReadDatabaseIdentityIDAndUUID
			(com.db4o.@internal.ObjectContainerBase stream, com.db4o.@internal.ClassMetadata
			 yapClass, com.db4o.@internal.slots.Slot oldSlot, bool checkClass)
		{
			com.db4o.@internal.Buffer reader = stream.ReadReaderByAddress(oldSlot.GetAddress(
				), oldSlot.GetLength());
			if (checkClass)
			{
				com.db4o.@internal.ClassMetadata realClass = com.db4o.@internal.ClassMetadata.ReadClass
					(stream, reader);
				if (realClass != yapClass)
				{
					return null;
				}
			}
			if (null == yapClass.FindOffset(reader, this))
			{
				return null;
			}
			return new com.db4o.@internal.UUIDFieldMetadata.DatabaseIdentityIDAndUUID(reader.
				ReadInt(), reader.ReadLong());
		}

		public override void Delete(com.db4o.@internal.marshall.MarshallerFamily mf, com.db4o.@internal.StatefulBuffer
			 a_bytes, bool isUpdate)
		{
			if (isUpdate)
			{
				a_bytes.IncrementOffset(LinkLength());
				return;
			}
			a_bytes.IncrementOffset(com.db4o.@internal.Const4.INT_LENGTH);
			long longPart = a_bytes.ReadLong();
			if (longPart > 0)
			{
				com.db4o.@internal.ObjectContainerBase stream = a_bytes.GetStream();
				if (stream.MaintainsIndices())
				{
					RemoveIndexEntry(a_bytes.GetTransaction(), a_bytes.GetID(), longPart);
				}
			}
		}

		public override bool HasIndex()
		{
			return true;
		}

		public override com.db4o.@internal.btree.BTree GetIndex(com.db4o.@internal.Transaction
			 transaction)
		{
			EnsureIndex(transaction);
			return base.GetIndex(transaction);
		}

		protected override void RebuildIndexForObject(com.db4o.@internal.LocalObjectContainer
			 stream, com.db4o.@internal.ClassMetadata yapClass, int objectId)
		{
			com.db4o.@internal.UUIDFieldMetadata.DatabaseIdentityIDAndUUID data = ReadDatabaseIdentityIDAndUUID
				(stream, yapClass, ((com.db4o.@internal.LocalTransaction)stream.GetSystemTransaction
				()).GetCurrentSlotOfID(objectId), true);
			if (null == data)
			{
				return;
			}
			AddIndexEntry(stream.GetSystemTransaction(), objectId, data.uuid);
		}

		private void EnsureIndex(com.db4o.@internal.Transaction transaction)
		{
			if (null == transaction)
			{
				throw new System.ArgumentNullException();
			}
			if (null != base.GetIndex(transaction))
			{
				return;
			}
			com.db4o.@internal.LocalObjectContainer file = ((com.db4o.@internal.LocalObjectContainer
				)transaction.Stream());
			com.db4o.@internal.SystemData sd = file.SystemData();
			if (sd == null)
			{
				return;
			}
			InitIndex(transaction, sd.UuidIndexId());
			if (sd.UuidIndexId() == 0)
			{
				sd.UuidIndexId(base.GetIndex(transaction).GetID());
				file.GetFileHeader().WriteVariablePart(file, 1);
			}
		}

		internal override void Instantiate1(com.db4o.@internal.Transaction a_trans, com.db4o.@internal.ObjectReference
			 a_yapObject, com.db4o.@internal.Buffer a_bytes)
		{
			int dbID = a_bytes.ReadInt();
			com.db4o.@internal.ObjectContainerBase stream = a_trans.Stream();
			stream.ShowInternalClasses(true);
			com.db4o.ext.Db4oDatabase db = (com.db4o.ext.Db4oDatabase)stream.GetByID2(a_trans
				, dbID);
			if (db != null && db.i_signature == null)
			{
				stream.Activate2(a_trans, db, 2);
			}
			com.db4o.@internal.VirtualAttributes va = a_yapObject.VirtualAttributes();
			va.i_database = db;
			va.i_uuid = a_bytes.ReadLong();
			stream.ShowInternalClasses(false);
		}

		public override int LinkLength()
		{
			return LINK_LENGTH;
		}

		internal override void Marshall1(com.db4o.@internal.ObjectReference a_yapObject, 
			com.db4o.@internal.StatefulBuffer a_bytes, bool a_migrating, bool a_new)
		{
			com.db4o.@internal.ObjectContainerBase stream = a_bytes.GetStream();
			com.db4o.@internal.Transaction trans = a_bytes.GetTransaction();
			bool indexEntry = a_new && stream.MaintainsIndices();
			int dbID = 0;
			com.db4o.@internal.VirtualAttributes attr = a_yapObject.VirtualAttributes();
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
						if (stream is com.db4o.@internal.LocalObjectContainer)
						{
							attr.i_uuid = stream.GenerateTimeStampId();
							indexEntry = true;
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
				a_bytes.WriteLong(attr.i_uuid);
				if (indexEntry)
				{
					AddIndexEntry(a_bytes, attr.i_uuid);
				}
			}
			else
			{
				a_bytes.WriteLong(0);
			}
		}

		internal override void MarshallIgnore(com.db4o.@internal.Buffer writer)
		{
			writer.WriteInt(0);
			writer.WriteLong(0);
		}

		public virtual object[] ObjectAndYapObjectBySignature(com.db4o.@internal.Transaction
			 transaction, long longPart, byte[] signature)
		{
			com.db4o.@internal.btree.BTreeRange range = Search(transaction, longPart);
			System.Collections.IEnumerator keys = range.Keys();
			while (keys.MoveNext())
			{
				com.db4o.@internal.btree.FieldIndexKey current = (com.db4o.@internal.btree.FieldIndexKey
					)keys.Current;
				object[] objectAndYapObject = GetObjectAndYapObjectByID(transaction, current.ParentID
					(), signature);
				if (null != objectAndYapObject)
				{
					return objectAndYapObject;
				}
			}
			return new object[2];
		}

		protected virtual object[] GetObjectAndYapObjectByID(com.db4o.@internal.Transaction
			 transaction, int parentId, byte[] signature)
		{
			object[] arr = transaction.Stream().GetObjectAndYapObjectByID(transaction, parentId
				);
			if (arr[1] == null)
			{
				return null;
			}
			com.db4o.@internal.ObjectReference yod = (com.db4o.@internal.ObjectReference)arr[
				1];
			com.db4o.@internal.VirtualAttributes vad = yod.VirtualAttributes(transaction);
			if (!com.db4o.foundation.Arrays4.AreEqual(signature, vad.i_database.i_signature))
			{
				return null;
			}
			return arr;
		}

		public override void DefragField(com.db4o.@internal.marshall.MarshallerFamily mf, 
			com.db4o.@internal.ReaderPair readers)
		{
			readers.CopyID();
			readers.IncrementOffset(com.db4o.@internal.Const4.LONG_LENGTH);
		}
	}
}
