namespace com.db4o.@internal
{
	/// <exclude></exclude>
	public abstract class LocalObjectContainer : com.db4o.@internal.ObjectContainerBase
	{
		protected com.db4o.@internal.fileheader.FileHeader _fileHeader;

		private com.db4o.foundation.Collection4 i_dirty;

		private com.db4o.@internal.freespace.FreespaceManager _freespaceManager;

		private com.db4o.@internal.freespace.FreespaceManager _fmChecker;

		private bool i_isServer = false;

		private com.db4o.foundation.Tree i_prefetchedIDs;

		private com.db4o.foundation.Hashtable4 i_semaphores;

		private int _blockEndAddress;

		private com.db4o.foundation.Tree _freeOnCommit;

		private com.db4o.@internal.SystemData _systemData;

		internal LocalObjectContainer(com.db4o.config.Configuration config, com.db4o.@internal.ObjectContainerBase
			 a_parent) : base(config, a_parent)
		{
		}

		public override com.db4o.@internal.Transaction NewTransaction(com.db4o.@internal.Transaction
			 parentTransaction)
		{
			return new com.db4o.@internal.LocalTransaction(this, parentTransaction);
		}

		public virtual com.db4o.@internal.freespace.FreespaceManager FreespaceManager()
		{
			return _freespaceManager;
		}

		public abstract void BlockSize(int size);

		public virtual void BlockSizeReadFromFile(int size)
		{
			BlockSize(size);
			SetRegularEndAddress(FileLength());
		}

		public virtual void SetRegularEndAddress(long address)
		{
			_blockEndAddress = BlocksFor(address);
		}

		protected override bool Close2()
		{
			bool ret = base.Close2();
			i_dirty = null;
			return ret;
		}

		public override void Commit1()
		{
			try
			{
				Write(false);
			}
			catch (System.Exception t)
			{
				FatalException(t);
			}
		}

		internal virtual void ConfigureNewFile()
		{
			NewSystemData(ConfigImpl().FreespaceSystem());
			SystemData().ConverterVersion(com.db4o.@internal.convert.Converter.VERSION);
			CreateStringIO(_systemData.StringEncoding());
			GenerateNewIdentity();
			_freespaceManager = com.db4o.@internal.freespace.FreespaceManager.CreateNew(this);
			BlockSize(ConfigImpl().BlockSize());
			_fileHeader = new com.db4o.@internal.fileheader.FileHeader1();
			SetRegularEndAddress(_fileHeader.Length());
			InitNewClassCollection();
			InitializeEssentialClasses();
			_fileHeader.InitNew(this);
			_freespaceManager.OnNew(this);
			_freespaceManager.Start(_systemData.FreespaceAddress());
			if (com.db4o.Debug.freespace && com.db4o.Debug.freespaceChecker)
			{
				_fmChecker.Start(0);
			}
		}

		private void NewSystemData(byte freespaceSystem)
		{
			_systemData = new com.db4o.@internal.SystemData();
			_systemData.StringEncoding(ConfigImpl().Encoding());
			_systemData.FreespaceSystem(freespaceSystem);
		}

		public override int ConverterVersion()
		{
			return _systemData.ConverterVersion();
		}

		public abstract void Copy(int oldAddress, int oldAddressOffset, int newAddress, int
			 newAddressOffset, int length);

		public override long CurrentVersion()
		{
			return _timeStampIdGenerator.LastTimeStampId();
		}

		internal virtual void InitNewClassCollection()
		{
			ClassCollection().InitTables(1);
		}

		public com.db4o.@internal.btree.BTree CreateBTreeClassIndex(int id)
		{
			return new com.db4o.@internal.btree.BTree(i_trans, id, new com.db4o.@internal.IDHandler
				(this));
		}

		public com.db4o.@internal.query.result.AbstractQueryResult NewQueryResult(com.db4o.@internal.Transaction
			 trans)
		{
			return NewQueryResult(trans, Config().QueryEvaluationMode());
		}

		public sealed override com.db4o.@internal.query.result.AbstractQueryResult NewQueryResult
			(com.db4o.@internal.Transaction trans, com.db4o.config.QueryEvaluationMode mode)
		{
			if (mode == com.db4o.config.QueryEvaluationMode.IMMEDIATE)
			{
				return new com.db4o.@internal.query.result.IdListQueryResult(trans);
			}
			return new com.db4o.@internal.query.result.HybridQueryResult(trans, mode);
		}

		public sealed override bool Delete4(com.db4o.@internal.Transaction ta, com.db4o.@internal.ObjectReference
			 yo, int a_cascade, bool userCall)
		{
			int id = yo.GetID();
			com.db4o.@internal.StatefulBuffer reader = ReadWriterByID(ta, id);
			if (reader != null)
			{
				object obj = yo.GetObject();
				if (obj != null)
				{
					if ((!ShowInternalClasses()) && com.db4o.@internal.Const4.CLASS_INTERNAL.IsAssignableFrom
						(j4o.lang.JavaSystem.GetClassForObject(obj)))
					{
						return false;
					}
				}
				reader.SetCascadeDeletes(a_cascade);
				reader.SlotDelete();
				com.db4o.@internal.ClassMetadata yc = yo.GetYapClass();
				yc.Delete(reader, obj);
				return true;
			}
			return false;
		}

		public abstract long FileLength();

		internal abstract string FileName();

		public virtual void Free(com.db4o.@internal.slots.Slot slot)
		{
			if (slot == null)
			{
				return;
			}
			if (slot._address == 0)
			{
				return;
			}
			Free(slot._address, slot._length);
		}

		public virtual void Free(int a_address, int a_length)
		{
			if (_freespaceManager == null)
			{
				return;
			}
			_freespaceManager.Free(a_address, a_length);
			if (com.db4o.Debug.freespace && com.db4o.Debug.freespaceChecker)
			{
				_fmChecker.Free(a_address, a_length);
			}
		}

		internal void FreePrefetchedPointers()
		{
			if (i_prefetchedIDs != null)
			{
				i_prefetchedIDs.Traverse(new _AnonymousInnerClass212(this));
			}
			i_prefetchedIDs = null;
		}

		private sealed class _AnonymousInnerClass212 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass212(LocalObjectContainer _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object a_object)
			{
				this._enclosing.Free(((com.db4o.@internal.TreeInt)a_object)._key, com.db4o.@internal.Const4
					.POINTER_LENGTH);
			}

			private readonly LocalObjectContainer _enclosing;
		}

		internal void FreeSpaceBeginCommit()
		{
			if (_freespaceManager == null)
			{
				return;
			}
			_freespaceManager.BeginCommit();
		}

		internal void FreeSpaceEndCommit()
		{
			if (_freespaceManager == null)
			{
				return;
			}
			_freespaceManager.EndCommit();
		}

		public virtual void GenerateNewIdentity()
		{
			lock (i_lock)
			{
				SetIdentity(com.db4o.ext.Db4oDatabase.Generate());
			}
		}

		public override com.db4o.@internal.query.result.AbstractQueryResult GetAll(com.db4o.@internal.Transaction
			 trans)
		{
			return GetAll(trans, Config().QueryEvaluationMode());
		}

		public virtual com.db4o.@internal.query.result.AbstractQueryResult GetAll(com.db4o.@internal.Transaction
			 trans, com.db4o.config.QueryEvaluationMode mode)
		{
			com.db4o.@internal.query.result.AbstractQueryResult queryResult = NewQueryResult(
				trans, mode);
			queryResult.LoadFromClassIndexes(ClassCollection().Iterator());
			return queryResult;
		}

		internal int GetPointerSlot()
		{
			int id = GetSlot(com.db4o.@internal.Const4.POINTER_LENGTH);
			i_systemTrans.WritePointer(id, 0, 0);
			if (i_handlers.IsSystemHandler(id))
			{
				return GetPointerSlot();
			}
			return id;
		}

		public virtual int GetSlot(int a_length)
		{
			return GetSlot1(a_length);
			int address = GetSlot1(a_length);
			com.db4o.DTrace.GET_SLOT.LogLength(address, a_length);
			return address;
		}

		private int GetSlot1(int bytes)
		{
			if (_freespaceManager != null)
			{
				int freeAddress = _freespaceManager.GetSlot(bytes);
				if (com.db4o.Debug.freespace && com.db4o.Debug.freespaceChecker)
				{
					if (freeAddress > 0)
					{
						com.db4o.foundation.Collection4 wrongOnes = new com.db4o.foundation.Collection4();
						int freeCheck = _fmChecker.GetSlot(bytes);
						while (freeCheck != freeAddress && freeCheck > 0)
						{
							wrongOnes.Add(new int[] { freeCheck, bytes });
							freeCheck = _fmChecker.GetSlot(bytes);
						}
						System.Collections.IEnumerator i = wrongOnes.GetEnumerator();
						while (i.MoveNext())
						{
							int[] adrLength = (int[])i.Current;
							_fmChecker.Free(adrLength[0], adrLength[1]);
						}
						if (freeCheck == 0)
						{
							_freespaceManager.Debug();
							_fmChecker.Debug();
						}
					}
				}
				if (freeAddress > 0)
				{
					return freeAddress;
				}
			}
			int blocksNeeded = BlocksFor(bytes);
			if (com.db4o.Debug.xbytes && com.db4o.Deploy.overwrite)
			{
				DebugWriteXBytes(_blockEndAddress, blocksNeeded * BlockSize());
			}
			return AppendBlocks(blocksNeeded);
		}

		protected virtual int AppendBlocks(int blockCount)
		{
			int blockedAddress = _blockEndAddress;
			_blockEndAddress += blockCount;
			return blockedAddress;
		}

		internal virtual void EnsureLastSlotWritten()
		{
			if (_blockEndAddress > BlocksFor(FileLength()))
			{
				com.db4o.@internal.StatefulBuffer writer = GetWriter(i_systemTrans, _blockEndAddress
					 - 1, BlockSize());
				writer.Write();
			}
		}

		public override com.db4o.ext.Db4oDatabase Identity()
		{
			return _systemData.Identity();
		}

		public virtual void SetIdentity(com.db4o.ext.Db4oDatabase identity)
		{
			_systemData.Identity(identity);
			_timeStampIdGenerator.Next();
		}

		internal override void Initialize2()
		{
			i_dirty = new com.db4o.foundation.Collection4();
			base.Initialize2();
		}

		internal override bool IsServer()
		{
			return i_isServer;
		}

		public com.db4o.@internal.slots.Pointer4 NewSlot(com.db4o.@internal.Transaction a_trans
			, int a_length)
		{
			int id = GetPointerSlot();
			int address = GetSlot(a_length);
			a_trans.SetPointer(id, address, a_length);
			return new com.db4o.@internal.slots.Pointer4(id, address);
		}

		public sealed override int NewUserObject()
		{
			return GetPointerSlot();
		}

		public virtual void PrefetchedIDConsumed(int a_id)
		{
			i_prefetchedIDs = i_prefetchedIDs.RemoveLike(new com.db4o.@internal.TreeIntObject
				(a_id));
		}

		public virtual int PrefetchID()
		{
			int id = GetPointerSlot();
			i_prefetchedIDs = com.db4o.foundation.Tree.Add(i_prefetchedIDs, new com.db4o.@internal.TreeInt
				(id));
			return id;
		}

		public virtual com.db4o.@internal.slots.ReferencedSlot ProduceFreeOnCommitEntry(int
			 id)
		{
			com.db4o.foundation.Tree node = com.db4o.@internal.TreeInt.Find(_freeOnCommit, id
				);
			if (node != null)
			{
				return (com.db4o.@internal.slots.ReferencedSlot)node;
			}
			com.db4o.@internal.slots.ReferencedSlot slot = new com.db4o.@internal.slots.ReferencedSlot
				(id);
			_freeOnCommit = com.db4o.foundation.Tree.Add(_freeOnCommit, slot);
			return slot;
		}

		public virtual void ReduceFreeOnCommitReferences(com.db4o.@internal.slots.ReferencedSlot
			 slot)
		{
			if (slot.RemoveReferenceIsLast())
			{
				_freeOnCommit = _freeOnCommit.RemoveNode(slot);
			}
		}

		public virtual void FreeDuringCommit(com.db4o.@internal.slots.ReferencedSlot referencedSlot
			, com.db4o.@internal.slots.Slot slot)
		{
			_freeOnCommit = referencedSlot.Free(this, _freeOnCommit, slot);
		}

		public override void RaiseVersion(long a_minimumVersion)
		{
			lock (Lock())
			{
				_timeStampIdGenerator.SetMinimumNext(a_minimumVersion);
			}
		}

		public override com.db4o.@internal.StatefulBuffer ReadWriterByID(com.db4o.@internal.Transaction
			 a_ta, int a_id)
		{
			return (com.db4o.@internal.StatefulBuffer)ReadReaderOrWriterByID(a_ta, a_id, false
				);
		}

		public override com.db4o.@internal.StatefulBuffer[] ReadWritersByIDs(com.db4o.@internal.Transaction
			 a_ta, int[] ids)
		{
			com.db4o.@internal.StatefulBuffer[] yapWriters = new com.db4o.@internal.StatefulBuffer
				[ids.Length];
			for (int i = 0; i < ids.Length; ++i)
			{
				yapWriters[i] = (com.db4o.@internal.StatefulBuffer)ReadReaderOrWriterByID(a_ta, ids
					[i], false);
			}
			return yapWriters;
		}

		public override com.db4o.@internal.Buffer ReadReaderByID(com.db4o.@internal.Transaction
			 a_ta, int a_id)
		{
			return ReadReaderOrWriterByID(a_ta, a_id, true);
		}

		private com.db4o.@internal.Buffer ReadReaderOrWriterByID(com.db4o.@internal.Transaction
			 a_ta, int a_id, bool useReader)
		{
			if (a_id == 0)
			{
				return null;
			}
			try
			{
				com.db4o.@internal.slots.Slot slot = ((com.db4o.@internal.LocalTransaction)a_ta).
					GetCurrentSlotOfID(a_id);
				if (slot == null)
				{
					return null;
				}
				if (slot._address == 0)
				{
					return null;
				}
				com.db4o.@internal.Buffer reader = null;
				if (useReader)
				{
					reader = new com.db4o.@internal.Buffer(slot._length);
				}
				else
				{
					reader = GetWriter(a_ta, slot._address, slot._length);
					((com.db4o.@internal.StatefulBuffer)reader).SetID(a_id);
				}
				reader.ReadEncrypt(this, slot._address);
				return reader;
			}
			catch (System.Exception e)
			{
			}
			return null;
		}

		protected override bool DoFinalize()
		{
			return _fileHeader != null;
		}

		internal virtual void ReadThis()
		{
			NewSystemData(com.db4o.@internal.freespace.FreespaceManager.FM_LEGACY_RAM);
			BlockSizeReadFromFile(1);
			_fileHeader = com.db4o.@internal.fileheader.FileHeader.ReadFixedPart(this);
			CreateStringIO(_systemData.StringEncoding());
			ClassCollection().SetID(_systemData.ClassCollectionID());
			ClassCollection().Read(i_systemTrans);
			com.db4o.@internal.convert.Converter.Convert(new com.db4o.@internal.convert.ConversionStage.ClassCollectionAvailableStage
				(this));
			ReadHeaderVariablePart();
			_freespaceManager = com.db4o.@internal.freespace.FreespaceManager.CreateNew(this, 
				_systemData.FreespaceSystem());
			_freespaceManager.Read(_systemData.FreespaceID());
			_freespaceManager.Start(_systemData.FreespaceAddress());
			if (_freespaceManager.RequiresMigration(ConfigImpl().FreespaceSystem(), _systemData
				.FreespaceSystem()))
			{
				com.db4o.@internal.freespace.FreespaceManager oldFreespaceManager = _freespaceManager;
				_freespaceManager = com.db4o.@internal.freespace.FreespaceManager.CreateNew(this, 
					_systemData.FreespaceSystem());
				_freespaceManager.Start(NewFreespaceSlot(_systemData.FreespaceSystem()));
				com.db4o.@internal.freespace.FreespaceManager.Migrate(oldFreespaceManager, _freespaceManager
					);
				_fileHeader.WriteVariablePart(this, 1);
			}
			WriteHeader(false);
			com.db4o.@internal.LocalTransaction trans = (com.db4o.@internal.LocalTransaction)
				_fileHeader.InterruptedTransaction();
			if (trans != null)
			{
				if (!ConfigImpl().CommitRecoveryDisabled())
				{
					trans.WriteOld();
				}
			}
			if (com.db4o.@internal.convert.Converter.Convert(new com.db4o.@internal.convert.ConversionStage.SystemUpStage
				(this)))
			{
				_systemData.ConverterVersion(com.db4o.@internal.convert.Converter.VERSION);
				_fileHeader.WriteVariablePart(this, 1);
				GetTransaction().Commit();
			}
		}

		private void ReadHeaderVariablePart()
		{
			_fileHeader.ReadVariablePart(this);
			SetNextTimeStampId(SystemData().LastTimeStampID());
		}

		public virtual int NewFreespaceSlot(byte freespaceSystem)
		{
			_systemData.FreespaceAddress(com.db4o.@internal.freespace.FreespaceManager.InitSlot
				(this));
			_systemData.FreespaceSystem(freespaceSystem);
			return _systemData.FreespaceAddress();
		}

		public virtual void EnsureFreespaceSlot()
		{
			if (SystemData().FreespaceAddress() == 0)
			{
				NewFreespaceSlot(SystemData().FreespaceSystem());
			}
		}

		public override void ReleaseSemaphore(string name)
		{
			ReleaseSemaphore(CheckTransaction(null), name);
		}

		public virtual void ReleaseSemaphore(com.db4o.@internal.Transaction ta, string name
			)
		{
			if (i_semaphores != null)
			{
				lock (i_semaphores)
				{
					if (i_semaphores != null && ta == i_semaphores.Get(name))
					{
						i_semaphores.Remove(name);
						j4o.lang.JavaSystem.NotifyAll(i_semaphores);
					}
				}
			}
		}

		public override void ReleaseSemaphores(com.db4o.@internal.Transaction ta)
		{
			if (i_semaphores != null)
			{
				com.db4o.foundation.Hashtable4 semaphores = i_semaphores;
				lock (semaphores)
				{
					semaphores.ForEachKeyForIdentity(new _AnonymousInnerClass576(this, semaphores), ta
						);
					j4o.lang.JavaSystem.NotifyAll(semaphores);
				}
			}
		}

		private sealed class _AnonymousInnerClass576 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass576(LocalObjectContainer _enclosing, com.db4o.foundation.Hashtable4
				 semaphores)
			{
				this._enclosing = _enclosing;
				this.semaphores = semaphores;
			}

			public void Visit(object a_object)
			{
				semaphores.Remove(a_object);
			}

			private readonly LocalObjectContainer _enclosing;

			private readonly com.db4o.foundation.Hashtable4 semaphores;
		}

		public sealed override void Rollback1()
		{
			GetTransaction().Rollback();
		}

		public sealed override void SetDirtyInSystemTransaction(com.db4o.@internal.PersistentBase
			 a_object)
		{
			a_object.SetStateDirty();
			a_object.CacheDirty(i_dirty);
		}

		public override bool SetSemaphore(string name, int timeout)
		{
			return SetSemaphore(CheckTransaction(null), name, timeout);
		}

		public virtual bool SetSemaphore(com.db4o.@internal.Transaction ta, string name, 
			int timeout)
		{
			if (name == null)
			{
				throw new System.ArgumentNullException();
			}
			lock (i_lock)
			{
				if (i_semaphores == null)
				{
					i_semaphores = new com.db4o.foundation.Hashtable4(10);
				}
			}
			lock (i_semaphores)
			{
				object obj = i_semaphores.Get(name);
				if (obj == null)
				{
					i_semaphores.Put(name, ta);
					return true;
				}
				if (ta == obj)
				{
					return true;
				}
				long endtime = j4o.lang.JavaSystem.CurrentTimeMillis() + timeout;
				long waitTime = timeout;
				while (waitTime > 0)
				{
					try
					{
						j4o.lang.JavaSystem.Wait(i_semaphores, waitTime);
					}
					catch (System.Exception e)
					{
					}
					if (ClassCollection() == null)
					{
						return false;
					}
					obj = i_semaphores.Get(name);
					if (obj == null)
					{
						i_semaphores.Put(name, ta);
						return true;
					}
					waitTime = endtime - j4o.lang.JavaSystem.CurrentTimeMillis();
				}
				return false;
			}
		}

		public virtual void SetServer(bool flag)
		{
			i_isServer = flag;
		}

		public abstract void SyncFiles();

		public override string ToString()
		{
			return FileName();
		}

		public override void Write(bool shuttingDown)
		{
			i_trans.Commit();
			if (shuttingDown)
			{
				WriteHeader(shuttingDown);
			}
		}

		public abstract bool WriteAccessTime(int address, int offset, long time);

		public abstract void WriteBytes(com.db4o.@internal.Buffer a_Bytes, int address, int
			 addressOffset);

		public sealed override void WriteDirty()
		{
			WriteCachedDirty();
			WriteVariableHeader();
		}

		private void WriteCachedDirty()
		{
			System.Collections.IEnumerator i = i_dirty.GetEnumerator();
			while (i.MoveNext())
			{
				com.db4o.@internal.PersistentBase dirty = (com.db4o.@internal.PersistentBase)i.Current;
				dirty.Write(i_systemTrans);
				dirty.NotCachedDirty();
			}
			i_dirty.Clear();
		}

		protected virtual void WriteVariableHeader()
		{
			if (!_timeStampIdGenerator.IsDirty())
			{
				return;
			}
			_systemData.LastTimeStampID(_timeStampIdGenerator.LastTimeStampId());
			_fileHeader.WriteVariablePart(this, 2);
			_timeStampIdGenerator.SetClean();
		}

		public sealed override void WriteEmbedded(com.db4o.@internal.StatefulBuffer a_parent
			, com.db4o.@internal.StatefulBuffer a_child)
		{
			int length = a_child.GetLength();
			int address = GetSlot(length);
			a_child.GetTransaction().SlotFreeOnRollback(address, address, length);
			a_child.Address(address);
			a_child.WriteEncrypt();
			int offsetBackup = a_parent._offset;
			a_parent._offset = a_child.GetID();
			a_parent.WriteInt(address);
			a_parent._offset = offsetBackup;
		}

		internal virtual void WriteHeader(bool shuttingDown)
		{
			int freespaceID = _freespaceManager.Write(shuttingDown);
			if (shuttingDown)
			{
				_freespaceManager = null;
			}
			if (com.db4o.Debug.freespace && com.db4o.Debug.freespaceChecker)
			{
				freespaceID = _fmChecker.Write(shuttingDown);
			}
			com.db4o.@internal.StatefulBuffer writer = GetWriter(i_systemTrans, 0, _fileHeader
				.Length());
			_fileHeader.WriteFixedPart(this, shuttingDown, writer, BlockSize(), freespaceID);
			if (shuttingDown)
			{
				EnsureLastSlotWritten();
			}
			SyncFiles();
		}

		public sealed override void WriteNew(com.db4o.@internal.ClassMetadata a_yapClass, 
			com.db4o.@internal.StatefulBuffer aWriter)
		{
			aWriter.WriteEncrypt(this, aWriter.GetAddress(), 0);
			if (a_yapClass == null)
			{
				return;
			}
			if (MaintainsIndices())
			{
				a_yapClass.AddToIndex(this, aWriter.GetTransaction(), aWriter.GetID());
			}
		}

		public abstract void DebugWriteXBytes(int a_address, int a_length);

		internal virtual com.db4o.@internal.Buffer XBytes(int a_address, int a_length)
		{
			com.db4o.@internal.Buffer bytes = GetWriter(i_systemTrans, a_address, a_length);
			for (int i = 0; i < a_length; i++)
			{
				bytes.Append(com.db4o.@internal.Const4.XBYTE);
			}
			return bytes;
		}

		public sealed override void WriteTransactionPointer(int address)
		{
			_fileHeader.WriteTransactionPointer(GetSystemTransaction(), address);
		}

		public void GetSlotForUpdate(com.db4o.@internal.StatefulBuffer forWriter)
		{
			com.db4o.@internal.Transaction trans = forWriter.GetTransaction();
			int id = forWriter.GetID();
			int length = forWriter.GetLength();
			int address = GetSlot(length);
			forWriter.Address(address);
			trans.SlotFreeOnRollbackSetPointer(id, address, length);
		}

		public sealed override void WriteUpdate(com.db4o.@internal.ClassMetadata a_yapClass
			, com.db4o.@internal.StatefulBuffer a_bytes)
		{
			if (a_bytes.GetAddress() == 0)
			{
				GetSlotForUpdate(a_bytes);
			}
			a_bytes.WriteEncrypt();
		}

		public virtual void SetNextTimeStampId(long val)
		{
			_timeStampIdGenerator.SetMinimumNext(val);
			_timeStampIdGenerator.SetClean();
		}

		public override com.db4o.ext.SystemInfo SystemInfo()
		{
			return new com.db4o.@internal.SystemInfoFileImpl(this);
		}

		public virtual com.db4o.@internal.fileheader.FileHeader GetFileHeader()
		{
			return _fileHeader;
		}

		public virtual void InstallDebugFreespaceManager(com.db4o.@internal.freespace.FreespaceManager
			 manager)
		{
			_freespaceManager = manager;
		}

		public virtual com.db4o.@internal.SystemData SystemData()
		{
			return _systemData;
		}

		public override long[] GetIDsForClass(com.db4o.@internal.Transaction trans, com.db4o.@internal.ClassMetadata
			 clazz)
		{
			com.db4o.foundation.IntArrayList ids = new com.db4o.foundation.IntArrayList();
			clazz.Index().TraverseAll(trans, new _AnonymousInnerClass798(this, ids));
			return ids.AsLong();
		}

		private sealed class _AnonymousInnerClass798 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass798(LocalObjectContainer _enclosing, com.db4o.foundation.IntArrayList
				 ids)
			{
				this._enclosing = _enclosing;
				this.ids = ids;
			}

			public void Visit(object obj)
			{
				ids.Add(((int)obj));
			}

			private readonly LocalObjectContainer _enclosing;

			private readonly com.db4o.foundation.IntArrayList ids;
		}

		public override com.db4o.@internal.query.result.QueryResult ClassOnlyQuery(com.db4o.@internal.Transaction
			 trans, com.db4o.@internal.ClassMetadata clazz)
		{
			if (!clazz.HasIndex())
			{
				return null;
			}
			com.db4o.@internal.query.result.AbstractQueryResult queryResult = NewQueryResult(
				trans);
			queryResult.LoadFromClassIndex(clazz);
			return queryResult;
		}

		public override com.db4o.@internal.query.result.QueryResult ExecuteQuery(com.db4o.@internal.query.processor.QQuery
			 query)
		{
			com.db4o.@internal.query.result.AbstractQueryResult queryResult = NewQueryResult(
				query.GetTransaction());
			queryResult.LoadFromQuery(query);
			return queryResult;
		}
	}
}
