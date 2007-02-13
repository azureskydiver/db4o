namespace com.db4o.defragment
{
	/// <exclude></exclude>
	public class DefragContextImpl : com.db4o.@internal.mapping.DefragContext
	{
		public abstract class DbSelector
		{
			internal DbSelector()
			{
			}

			internal abstract com.db4o.@internal.LocalObjectContainer Db(com.db4o.defragment.DefragContextImpl
				 context);

			internal virtual com.db4o.@internal.Transaction Transaction(com.db4o.defragment.DefragContextImpl
				 context)
			{
				return Db(context).GetSystemTransaction();
			}
		}

		private sealed class _AnonymousInnerClass33 : com.db4o.defragment.DefragContextImpl.DbSelector
		{
			public _AnonymousInnerClass33()
			{
			}

			internal override com.db4o.@internal.LocalObjectContainer Db(com.db4o.defragment.DefragContextImpl
				 context)
			{
				return context._sourceDb;
			}
		}

		public static readonly com.db4o.defragment.DefragContextImpl.DbSelector SOURCEDB = 
			new _AnonymousInnerClass33();

		private sealed class _AnonymousInnerClass39 : com.db4o.defragment.DefragContextImpl.DbSelector
		{
			public _AnonymousInnerClass39()
			{
			}

			internal override com.db4o.@internal.LocalObjectContainer Db(com.db4o.defragment.DefragContextImpl
				 context)
			{
				return context._targetDb;
			}
		}

		public static readonly com.db4o.defragment.DefragContextImpl.DbSelector TARGETDB = 
			new _AnonymousInnerClass39();

		private const long CLASSCOLLECTION_POINTER_ADDRESS = 2 + 2 * com.db4o.@internal.Const4
			.INT_LENGTH;

		public readonly com.db4o.@internal.LocalObjectContainer _sourceDb;

		internal readonly com.db4o.@internal.LocalObjectContainer _targetDb;

		private readonly com.db4o.defragment.ContextIDMapping _mapping;

		private com.db4o.defragment.DefragmentListener _listener;

		private com.db4o.foundation.Queue4 _unindexed = new com.db4o.foundation.Queue4();

		public DefragContextImpl(com.db4o.defragment.DefragmentConfig defragConfig, com.db4o.defragment.DefragmentListener
			 listener)
		{
			_listener = listener;
			com.db4o.@internal.Config4Impl originalConfig = (com.db4o.@internal.Config4Impl)defragConfig
				.Db4oConfig();
			com.db4o.config.Configuration sourceConfig = (com.db4o.config.Configuration)originalConfig
				.DeepClone(null);
			sourceConfig.WeakReferences(false);
			sourceConfig.FlushFileBuffers(false);
			sourceConfig.ReadOnly(true);
			_sourceDb = (com.db4o.@internal.LocalObjectContainer)com.db4o.Db4o.OpenFile(sourceConfig
				, defragConfig.BackupPath()).Ext();
			_targetDb = FreshYapFile(defragConfig.OrigPath());
			_mapping = defragConfig.Mapping();
			_mapping.Open();
		}

		internal static com.db4o.@internal.LocalObjectContainer FreshYapFile(string fileName
			)
		{
			new j4o.io.File(fileName).Delete();
			return (com.db4o.@internal.LocalObjectContainer)com.db4o.Db4o.OpenFile(com.db4o.defragment.DefragmentConfig
				.VanillaDb4oConfig(), fileName).Ext();
		}

		public virtual int MappedID(int oldID, int defaultID)
		{
			int mapped = InternalMappedID(oldID, false);
			return (mapped != 0 ? mapped : defaultID);
		}

		public virtual int MappedID(int oldID)
		{
			int mapped = InternalMappedID(oldID, false);
			if (mapped == 0)
			{
				throw new com.db4o.@internal.mapping.MappingNotFoundException(oldID);
			}
			return mapped;
		}

		public virtual int MappedID(int id, bool lenient)
		{
			if (id == 0)
			{
				return 0;
			}
			int mapped = InternalMappedID(id, lenient);
			if (mapped == 0)
			{
				_listener.NotifyDefragmentInfo(new com.db4o.defragment.DefragmentInfo("No mapping found for ID "
					 + id));
				return 0;
			}
			return mapped;
		}

		private int InternalMappedID(int oldID, bool lenient)
		{
			if (oldID == 0)
			{
				return 0;
			}
			if (_sourceDb.Handlers().IsSystemHandler(oldID))
			{
				return oldID;
			}
			return _mapping.MappedID(oldID, lenient);
		}

		public virtual void MapIDs(int oldID, int newID, bool isClassID)
		{
			_mapping.MapIDs(oldID, newID, isClassID);
		}

		public virtual void Close()
		{
			_sourceDb.Close();
			_targetDb.Close();
			_mapping.Close();
		}

		public virtual com.db4o.@internal.Buffer ReaderByID(com.db4o.defragment.DefragContextImpl.DbSelector
			 selector, int id)
		{
			com.db4o.@internal.slots.Slot slot = ReadPointer(selector, id);
			return ReaderByAddress(selector, slot._address, slot._length);
		}

		public virtual com.db4o.@internal.StatefulBuffer SourceWriterByID(int id)
		{
			com.db4o.@internal.slots.Slot slot = ReadPointer(SOURCEDB, id);
			return _sourceDb.ReadWriterByAddress(SOURCEDB.Transaction(this), slot._address, slot
				._length);
		}

		public virtual com.db4o.@internal.Buffer SourceReaderByAddress(int address, int length
			)
		{
			return ReaderByAddress(SOURCEDB, address, length);
		}

		public virtual com.db4o.@internal.Buffer TargetReaderByAddress(int address, int length
			)
		{
			return ReaderByAddress(TARGETDB, address, length);
		}

		public virtual com.db4o.@internal.Buffer ReaderByAddress(com.db4o.defragment.DefragContextImpl.DbSelector
			 selector, int address, int length)
		{
			return selector.Db(this).ReadReaderByAddress(address, length);
		}

		public virtual com.db4o.@internal.StatefulBuffer TargetWriterByAddress(int address
			, int length)
		{
			return _targetDb.ReadWriterByAddress(TARGETDB.Transaction(this), address, length);
		}

		public virtual int AllocateTargetSlot(int length)
		{
			return _targetDb.GetSlot(length);
		}

		public virtual void TargetWriteBytes(com.db4o.@internal.ReaderPair readers, int address
			)
		{
			readers.Write(_targetDb, address);
		}

		public virtual void TargetWriteBytes(com.db4o.@internal.Buffer reader, int address
			)
		{
			_targetDb.WriteBytes(reader, address, 0);
		}

		public virtual com.db4o.ext.StoredClass[] StoredClasses(com.db4o.defragment.DefragContextImpl.DbSelector
			 selector)
		{
			com.db4o.@internal.LocalObjectContainer db = selector.Db(this);
			db.ShowInternalClasses(true);
			com.db4o.ext.StoredClass[] classes = db.StoredClasses();
			return classes;
		}

		public virtual com.db4o.@internal.LatinStringIO StringIO()
		{
			return _sourceDb.StringIO();
		}

		public virtual void TargetCommit()
		{
			_targetDb.Commit();
		}

		public virtual com.db4o.@internal.TypeHandler4 SourceHandler(int id)
		{
			return _sourceDb.HandlerByID(id);
		}

		public virtual int SourceClassCollectionID()
		{
			return _sourceDb.ClassCollection().GetID();
		}

		public static void TargetClassCollectionID(string file, int id)
		{
			j4o.io.RandomAccessFile raf = new j4o.io.RandomAccessFile(file, "rw");
			try
			{
				com.db4o.@internal.Buffer reader = new com.db4o.@internal.Buffer(com.db4o.@internal.Const4
					.INT_LENGTH);
				raf.Seek(CLASSCOLLECTION_POINTER_ADDRESS);
				reader._offset = 0;
				reader.WriteInt(id);
				raf.Write(reader._buffer);
			}
			finally
			{
				raf.Close();
			}
		}

		private com.db4o.foundation.Hashtable4 _classIndices = new com.db4o.foundation.Hashtable4
			(16);

		public virtual int ClassIndexID(com.db4o.@internal.ClassMetadata yapClass)
		{
			return ClassIndex(yapClass).Id();
		}

		public virtual void TraverseAll(com.db4o.@internal.ClassMetadata yapClass, com.db4o.foundation.Visitor4
			 command)
		{
			if (!yapClass.HasIndex())
			{
				return;
			}
			yapClass.Index().TraverseAll(SOURCEDB.Transaction(this), command);
		}

		public virtual void TraverseAllIndexSlots(com.db4o.@internal.ClassMetadata yapClass
			, com.db4o.foundation.Visitor4 command)
		{
			System.Collections.IEnumerator slotIDIter = yapClass.Index().AllSlotIDs(SOURCEDB.
				Transaction(this));
			while (slotIDIter.MoveNext())
			{
				command.Visit(slotIDIter.Current);
			}
		}

		public virtual void TraverseAllIndexSlots(com.db4o.@internal.btree.BTree btree, com.db4o.foundation.Visitor4
			 command)
		{
			System.Collections.IEnumerator slotIDIter = btree.AllNodeIds(SOURCEDB.Transaction
				(this));
			while (slotIDIter.MoveNext())
			{
				command.Visit(slotIDIter.Current);
			}
		}

		public virtual int DatabaseIdentityID(com.db4o.defragment.DefragContextImpl.DbSelector
			 selector)
		{
			com.db4o.@internal.LocalObjectContainer db = selector.Db(this);
			com.db4o.ext.Db4oDatabase identity = db.Identity();
			if (identity == null)
			{
				return 0;
			}
			return identity.GetID(selector.Transaction(this));
		}

		private com.db4o.@internal.classindex.ClassIndexStrategy ClassIndex(com.db4o.@internal.ClassMetadata
			 yapClass)
		{
			com.db4o.@internal.classindex.ClassIndexStrategy classIndex = (com.db4o.@internal.classindex.ClassIndexStrategy
				)_classIndices.Get(yapClass);
			if (classIndex == null)
			{
				classIndex = new com.db4o.@internal.classindex.BTreeClassIndexStrategy(yapClass);
				_classIndices.Put(yapClass, classIndex);
				classIndex.Initialize(_targetDb);
			}
			return classIndex;
		}

		public virtual com.db4o.@internal.Transaction SystemTrans()
		{
			return SOURCEDB.Transaction(this);
		}

		public virtual void CopyIdentity()
		{
			_targetDb.SetIdentity(_sourceDb.Identity());
		}

		public virtual void TargetClassCollectionID(int newClassCollectionID)
		{
			_targetDb.SystemData().ClassCollectionID(newClassCollectionID);
		}

		public virtual com.db4o.@internal.Buffer SourceReaderByID(int sourceID)
		{
			return ReaderByID(SOURCEDB, sourceID);
		}

		public virtual com.db4o.@internal.btree.BTree SourceUuidIndex()
		{
			if (SourceUuidIndexID() == 0)
			{
				return null;
			}
			return _sourceDb.GetUUIDIndex().GetIndex(SystemTrans());
		}

		public virtual void TargetUuidIndexID(int id)
		{
			_targetDb.SystemData().UuidIndexId(id);
		}

		public virtual int SourceUuidIndexID()
		{
			return _sourceDb.SystemData().UuidIndexId();
		}

		public virtual com.db4o.@internal.ClassMetadata YapClass(int id)
		{
			return _sourceDb.GetYapClass(id);
		}

		public virtual void RegisterUnindexed(int id)
		{
			_unindexed.Add(id);
		}

		public virtual System.Collections.IEnumerator UnindexedIDs()
		{
			return _unindexed.Iterator();
		}

		private com.db4o.@internal.slots.Slot ReadPointer(com.db4o.defragment.DefragContextImpl.DbSelector
			 selector, int id)
		{
			com.db4o.@internal.Buffer reader = ReaderByAddress(selector, id, com.db4o.@internal.Const4
				.POINTER_LENGTH);
			int address = reader.ReadInt();
			int length = reader.ReadInt();
			return new com.db4o.@internal.slots.Slot(address, length);
		}
	}
}
