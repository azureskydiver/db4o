/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

using System;
using System.Collections;
using Db4objects.Db4o;
using Db4objects.Db4o.Ext;
using Db4objects.Db4o.Foundation;
using Db4objects.Db4o.Internal;
using Db4objects.Db4o.Internal.Activation;
using Db4objects.Db4o.Internal.Btree;
using Db4objects.Db4o.Internal.Handlers;
using Db4objects.Db4o.Internal.Marshall;
using Db4objects.Db4o.Internal.Slots;
using Db4objects.Db4o.Marshall;

namespace Db4objects.Db4o.Internal
{
	/// <exclude></exclude>
	public class UUIDFieldMetadata : VirtualFieldMetadata
	{
		private const int LINK_LENGTH = Const4.LONG_LENGTH + Const4.ID_LENGTH;

		internal UUIDFieldMetadata(ObjectContainerBase container) : base(Handlers4.LONG_ID
			, new LongHandler(container))
		{
			SetName(Const4.VIRTUAL_FIELD_PREFIX + "uuid");
		}

		/// <exception cref="FieldIndexException"></exception>
		public override void AddFieldIndex(MarshallerFamily mf, ClassMetadata yapClass, StatefulBuffer
			 writer, Slot oldSlot)
		{
			bool isnew = (oldSlot == null);
			int offset = writer._offset;
			int db4oDatabaseIdentityID = writer.ReadInt();
			long uuid = writer.ReadLong();
			writer._offset = offset;
			LocalObjectContainer yf = (LocalObjectContainer)writer.GetStream();
			if ((uuid == 0 || db4oDatabaseIdentityID == 0) && writer.GetID() > 0 && !isnew)
			{
				UUIDFieldMetadata.DatabaseIdentityIDAndUUID identityAndUUID = ReadDatabaseIdentityIDAndUUID
					(yf, yapClass, oldSlot, false);
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

		/// <exception cref="Db4oIOException"></exception>
		private UUIDFieldMetadata.DatabaseIdentityIDAndUUID ReadDatabaseIdentityIDAndUUID
			(ObjectContainerBase stream, ClassMetadata classMetadata, Slot oldSlot, bool checkClass
			)
		{
			if (DTrace.enabled)
			{
				DTrace.REREAD_OLD_UUID.LogLength(oldSlot.Address(), oldSlot.Length());
			}
			BufferImpl reader = stream.BufferByAddress(oldSlot.Address(), oldSlot.Length());
			if (checkClass)
			{
				ClassMetadata realClass = ClassMetadata.ReadClass(stream, reader);
				if (realClass != classMetadata)
				{
					return null;
				}
			}
			if (classMetadata.FindOffset(reader, this) == HandlerVersion.INVALID)
			{
				return null;
			}
			return new UUIDFieldMetadata.DatabaseIdentityIDAndUUID(reader.ReadInt(), reader.ReadLong
				());
		}

		public override void Delete(MarshallerFamily mf, StatefulBuffer a_bytes, bool isUpdate
			)
		{
			if (isUpdate)
			{
				a_bytes.IncrementOffset(LinkLength());
				return;
			}
			a_bytes.IncrementOffset(Const4.INT_LENGTH);
			long longPart = a_bytes.ReadLong();
			if (longPart > 0)
			{
				ObjectContainerBase stream = a_bytes.GetStream();
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

		public override BTree GetIndex(Transaction transaction)
		{
			EnsureIndex(transaction);
			return base.GetIndex(transaction);
		}

		/// <exception cref="FieldIndexException"></exception>
		protected override void RebuildIndexForObject(LocalObjectContainer stream, ClassMetadata
			 yapClass, int objectId)
		{
			UUIDFieldMetadata.DatabaseIdentityIDAndUUID data = ReadDatabaseIdentityIDAndUUID(
				stream, yapClass, ((LocalTransaction)stream.SystemTransaction()).GetCurrentSlotOfID
				(objectId), true);
			if (null == data)
			{
				return;
			}
			AddIndexEntry(stream.GetLocalSystemTransaction(), objectId, data.uuid);
		}

		private void EnsureIndex(Transaction transaction)
		{
			if (null == transaction)
			{
				throw new ArgumentNullException();
			}
			if (null != base.GetIndex(transaction))
			{
				return;
			}
			LocalObjectContainer file = ((LocalObjectContainer)transaction.Container());
			SystemData sd = file.SystemData();
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

		internal override void Instantiate1(Transaction a_trans, ObjectReference a_yapObject
			, IBuffer a_bytes)
		{
			int dbID = a_bytes.ReadInt();
			ObjectContainerBase stream = a_trans.Container();
			stream.ShowInternalClasses(true);
			try
			{
				Db4oDatabase db = (Db4oDatabase)stream.GetByID2(a_trans, dbID);
				if (db != null && db.i_signature == null)
				{
					stream.Activate(a_trans, db, new FixedActivationDepth(2));
				}
				VirtualAttributes va = a_yapObject.VirtualAttributes();
				va.i_database = db;
				va.i_uuid = a_bytes.ReadLong();
			}
			finally
			{
				stream.ShowInternalClasses(false);
			}
		}

		protected override int LinkLength()
		{
			return LINK_LENGTH;
		}

		internal override void Marshall(Transaction trans, ObjectReference @ref, IWriteBuffer
			 buffer, bool isMigrating, bool isNew)
		{
			VirtualAttributes attr = @ref.VirtualAttributes();
			ObjectContainerBase container = trans.Container();
			bool doAddIndexEntry = isNew && container.MaintainsIndices();
			int dbID = 0;
			bool linkToDatabase = (attr != null && attr.i_database == null) ? true : !isMigrating;
			if (linkToDatabase)
			{
				Db4oDatabase db = ((IInternalObjectContainer)container).Identity();
				if (db == null)
				{
					attr = null;
				}
				else
				{
					if (attr.i_database == null)
					{
						attr.i_database = db;
						if (container is LocalObjectContainer)
						{
							attr.i_uuid = container.GenerateTimeStampId();
							doAddIndexEntry = true;
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
			buffer.WriteInt(dbID);
			if (attr == null)
			{
				buffer.WriteLong(0);
				return;
			}
			buffer.WriteLong(attr.i_uuid);
			if (doAddIndexEntry)
			{
				AddIndexEntry(trans, @ref.GetID(), attr.i_uuid);
			}
		}

		internal override void MarshallIgnore(IWriteBuffer buffer)
		{
			buffer.WriteInt(0);
			buffer.WriteLong(0);
		}

		public HardObjectReference GetHardObjectReferenceBySignature(Transaction transaction
			, long longPart, byte[] signature)
		{
			IBTreeRange range = Search(transaction, longPart);
			IEnumerator keys = range.Keys();
			while (keys.MoveNext())
			{
				FieldIndexKey current = (FieldIndexKey)keys.Current;
				HardObjectReference hardRef = GetHardObjectReferenceById(transaction, current.ParentID
					(), signature);
				if (null != hardRef)
				{
					return hardRef;
				}
			}
			return HardObjectReference.INVALID;
		}

		protected HardObjectReference GetHardObjectReferenceById(Transaction transaction, 
			int parentId, byte[] signature)
		{
			HardObjectReference hardRef = transaction.Container().GetHardObjectReferenceById(
				transaction, parentId);
			if (hardRef._reference == null)
			{
				return null;
			}
			VirtualAttributes vad = hardRef._reference.VirtualAttributes(transaction);
			if (!Arrays4.AreEqual(signature, vad.i_database.i_signature))
			{
				return null;
			}
			return hardRef;
		}

		public override void DefragField(MarshallerFamily mf, DefragmentContextImpl context
			)
		{
			context.CopyID();
			context.IncrementOffset(Const4.LONG_LENGTH);
		}
	}
}
