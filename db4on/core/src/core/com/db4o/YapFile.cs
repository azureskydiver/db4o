namespace com.db4o
{
	/// <exclude></exclude>
	public abstract class YapFile : com.db4o.YapStream
	{
		protected com.db4o.YapConfigBlock _configBlock;

		private com.db4o.PBootRecord _bootRecord;

		private com.db4o.foundation.Collection4 i_dirty;

		private com.db4o.inside.freespace.FreespaceManager _freespaceManager;

		private com.db4o.inside.freespace.FreespaceManager _fmChecker;

		private bool i_isServer = false;

		private com.db4o.Tree i_prefetchedIDs;

		private com.db4o.foundation.Hashtable4 i_semaphores;

		internal int i_writeAt;

		private com.db4o.Tree _freeOnCommit;

		internal YapFile(com.db4o.YapStream a_parent) : base(a_parent)
		{
		}

		internal virtual void BlockSize(int blockSize)
		{
		}

		public override com.db4o.PBootRecord BootRecord()
		{
			return _bootRecord;
		}

		internal override bool Close2()
		{
			bool ret = base.Close2();
			i_dirty = null;
			return ret;
		}

		internal override void Commit1()
		{
			CheckClosed();
			i_entryCounter++;
			try
			{
				Write(false);
			}
			catch (System.Exception t)
			{
				FatalException(t);
			}
			i_entryCounter--;
		}

		internal virtual void ConfigureNewFile()
		{
			_freespaceManager = com.db4o.inside.freespace.FreespaceManager.CreateNew(this, i_config
				.FreespaceSystem());
			BlockSize(i_config.BlockSize());
			i_writeAt = BlocksFor(HEADER_LENGTH);
			_configBlock = new com.db4o.YapConfigBlock(this);
			_configBlock.ConverterVersion(com.db4o.inside.convert.Converter.VERSION);
			_configBlock.Write();
			_configBlock.Go();
			InitNewClassCollection();
			InitializeEssentialClasses();
			InitBootRecord();
			_freespaceManager.Start(_configBlock._freespaceAddress);
			if (com.db4o.Debug.freespace && com.db4o.Debug.freespaceChecker)
			{
				_fmChecker.Start(0);
			}
		}

		internal override long CurrentVersion()
		{
			return _bootRecord.CurrentVersion();
		}

		internal virtual void InitNewClassCollection()
		{
			i_classCollection.InitTables(1);
		}

		internal sealed override com.db4o.ClassIndex CreateClassIndex(com.db4o.YapClass yapClass
			)
		{
			return new com.db4o.ClassIndex(yapClass);
		}

		internal com.db4o.inside.btree.BTree CreateBTreeClassIndex(com.db4o.YapClass a_yapClass
			, int id)
		{
			return new com.db4o.inside.btree.BTree(i_config.BTreeNodeSize(), i_config.BTreeCacheHeight
				(), i_trans, id, new com.db4o.YInt(this), null);
		}

		internal sealed override com.db4o.QueryResultImpl CreateQResult(com.db4o.Transaction
			 a_ta)
		{
			return new com.db4o.QueryResultImpl(a_ta);
		}

		internal sealed override bool Delete5(com.db4o.Transaction ta, com.db4o.YapObject
			 yo, int a_cascade, bool userCall)
		{
			int id = yo.GetID();
			com.db4o.YapWriter reader = ReadWriterByID(ta, id);
			if (reader != null)
			{
				object obj = yo.GetObject();
				if (obj != null)
				{
					if ((!ShowInternalClasses()) && com.db4o.YapConst.CLASS_INTERNAL.IsAssignableFrom
						(j4o.lang.Class.GetClassForObject(obj)))
					{
						return false;
					}
				}
				reader.SetCascadeDeletes(a_cascade);
				reader.SlotDelete();
				com.db4o.YapClass yc = yo.GetYapClass();
				yc.Delete(reader, obj);
				return true;
			}
			return false;
		}

		internal abstract long FileLength();

		internal abstract string FileName();

		public virtual void Free(com.db4o.inside.slots.Slot slot)
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
				i_prefetchedIDs.Traverse(new _AnonymousInnerClass178(this));
			}
			i_prefetchedIDs = null;
		}

		private sealed class _AnonymousInnerClass178 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass178(YapFile _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object a_object)
			{
				this._enclosing.Free(((com.db4o.TreeInt)a_object)._key, com.db4o.YapConst.POINTER_LENGTH
					);
			}

			private readonly YapFile _enclosing;
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

		internal override void GetAll(com.db4o.Transaction ta, com.db4o.QueryResultImpl a_res
			)
		{
			com.db4o.Tree[] duplicates = new com.db4o.Tree[1];
			com.db4o.YapClassCollectionIterator i = i_classCollection.Iterator();
			while (i.HasNext())
			{
				com.db4o.YapClass yapClass = i.ReadNextClass();
				if (yapClass.GetName() != null)
				{
					com.db4o.reflect.ReflectClass claxx = yapClass.ClassReflector();
					if (claxx == null || !(i_handlers.ICLASS_INTERNAL.IsAssignableFrom(claxx)))
					{
						com.db4o.inside.btree.BTree btree = yapClass.Index();
						if (btree != null)
						{
							btree.TraverseKeys(ta, new _AnonymousInnerClass219(this, duplicates, a_res));
						}
						if (com.db4o.Debug.useOldClassIndex && !com.db4o.Debug.useBTrees)
						{
							com.db4o.Tree tree = yapClass.GetIndex(ta);
							if (tree != null)
							{
								tree.Traverse(new _AnonymousInnerClass238(this, duplicates, a_res));
							}
						}
					}
				}
			}
			a_res.Reset();
		}

		private sealed class _AnonymousInnerClass219 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass219(YapFile _enclosing, com.db4o.Tree[] duplicates, com.db4o.QueryResultImpl
				 a_res)
			{
				this._enclosing = _enclosing;
				this.duplicates = duplicates;
				this.a_res = a_res;
			}

			public void Visit(object obj)
			{
				int id = ((int)obj);
				com.db4o.TreeInt newNode = new com.db4o.TreeInt(id);
				duplicates[0] = com.db4o.Tree.Add(duplicates[0], newNode);
				if (newNode.Size() != 0)
				{
					a_res.Add(id);
				}
			}

			private readonly YapFile _enclosing;

			private readonly com.db4o.Tree[] duplicates;

			private readonly com.db4o.QueryResultImpl a_res;
		}

		private sealed class _AnonymousInnerClass238 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass238(YapFile _enclosing, com.db4o.Tree[] duplicates, com.db4o.QueryResultImpl
				 a_res)
			{
				this._enclosing = _enclosing;
				this.duplicates = duplicates;
				this.a_res = a_res;
			}

			public void Visit(object obj)
			{
				int id = ((com.db4o.TreeInt)obj)._key;
				com.db4o.TreeInt newNode = new com.db4o.TreeInt(id);
				duplicates[0] = com.db4o.Tree.Add(duplicates[0], newNode);
				if (newNode.Size() != 0)
				{
					a_res.Add(id);
				}
			}

			private readonly YapFile _enclosing;

			private readonly com.db4o.Tree[] duplicates;

			private readonly com.db4o.QueryResultImpl a_res;
		}

		internal int GetPointerSlot()
		{
			int id = GetSlot(com.db4o.YapConst.POINTER_LENGTH);
			i_systemTrans.WritePointer(id, 0, 0);
			if (id <= i_handlers.MaxTypeID())
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
						com.db4o.foundation.Iterator4 i = wrongOnes.Iterator();
						while (i.HasNext())
						{
							int[] adrLength = (int[])i.Next();
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
				WriteXBytes(i_writeAt, blocksNeeded * BlockSize());
			}
			int address = i_writeAt;
			i_writeAt += blocksNeeded;
			return address;
		}

		internal virtual void EnsureLastSlotWritten()
		{
			if (i_writeAt > BlocksFor(FileLength()))
			{
				com.db4o.YapWriter writer = GetWriter(i_systemTrans, i_writeAt - 1, BlockSize());
				writer.Write();
			}
		}

		public override com.db4o.ext.Db4oDatabase Identity()
		{
			if (_bootRecord == null)
			{
				return null;
			}
			return _bootRecord.i_db;
		}

		internal override void Initialize2()
		{
			i_dirty = new com.db4o.foundation.Collection4();
			base.Initialize2();
		}

		private void InitBootRecord()
		{
			ShowInternalClasses(true);
			_bootRecord = new com.db4o.PBootRecord();
			_bootRecord.i_stream = this;
			_bootRecord.Init(i_config);
			SetInternal(i_systemTrans, _bootRecord, false);
			_configBlock._bootRecordID = GetID1(i_systemTrans, _bootRecord);
			_configBlock.Write();
			ShowInternalClasses(false);
		}

		internal override bool IsServer()
		{
			return i_isServer;
		}

		public com.db4o.inside.slots.Pointer4 NewSlot(com.db4o.Transaction a_trans, int a_length
			)
		{
			int id = GetPointerSlot();
			int address = GetSlot(a_length);
			a_trans.SetPointer(id, address, a_length);
			return new com.db4o.inside.slots.Pointer4(id, address);
		}

		public sealed override int NewUserObject()
		{
			return GetPointerSlot();
		}

		internal virtual void PrefetchedIDConsumed(int a_id)
		{
			i_prefetchedIDs = i_prefetchedIDs.RemoveLike(new com.db4o.TreeIntObject(a_id));
		}

		internal virtual int PrefetchID()
		{
			int id = GetPointerSlot();
			i_prefetchedIDs = com.db4o.Tree.Add(i_prefetchedIDs, new com.db4o.TreeInt(id));
			return id;
		}

		public virtual com.db4o.inside.slots.ReferencedSlot ProduceFreeOnCommitEntry(int 
			id)
		{
			com.db4o.Tree node = com.db4o.TreeInt.Find(_freeOnCommit, id);
			if (node != null)
			{
				return (com.db4o.inside.slots.ReferencedSlot)node;
			}
			com.db4o.inside.slots.ReferencedSlot slot = new com.db4o.inside.slots.ReferencedSlot
				(id);
			_freeOnCommit = com.db4o.Tree.Add(_freeOnCommit, slot);
			return slot;
		}

		public virtual void ReduceFreeOnCommitReferences(com.db4o.inside.slots.ReferencedSlot
			 slot)
		{
			if (slot.RemoveReferenceIsLast())
			{
				_freeOnCommit = _freeOnCommit.RemoveNode(slot);
			}
		}

		public virtual void FreeDuringCommit(com.db4o.inside.slots.ReferencedSlot referencedSlot
			, com.db4o.inside.slots.Slot slot)
		{
			_freeOnCommit = referencedSlot.Free(this, _freeOnCommit, slot);
		}

		public override void RaiseVersion(long a_minimumVersion)
		{
			_bootRecord.RaiseVersion(a_minimumVersion);
		}

		public override com.db4o.YapWriter ReadWriterByID(com.db4o.Transaction a_ta, int 
			a_id)
		{
			return (com.db4o.YapWriter)ReadReaderOrWriterByID(a_ta, a_id, false);
		}

		public override com.db4o.YapReader ReadReaderByID(com.db4o.Transaction a_ta, int 
			a_id)
		{
			return ReadReaderOrWriterByID(a_ta, a_id, true);
		}

		private com.db4o.YapReader ReadReaderOrWriterByID(com.db4o.Transaction a_ta, int 
			a_id, bool useReader)
		{
			if (a_id == 0)
			{
				return null;
			}
			try
			{
				com.db4o.inside.slots.Slot slot = a_ta.GetSlotInformation(a_id);
				if (slot == null)
				{
					return null;
				}
				if (slot._address == 0)
				{
					return null;
				}
				com.db4o.YapReader reader = null;
				if (useReader)
				{
					reader = new com.db4o.YapReader(slot._length);
				}
				else
				{
					reader = GetWriter(a_ta, slot._address, slot._length);
					((com.db4o.YapWriter)reader).SetID(a_id);
				}
				reader.ReadEncrypt(this, slot._address);
				return reader;
			}
			catch (System.Exception e)
			{
			}
			return null;
		}

		internal virtual void ReadThis()
		{
			com.db4o.YapWriter myreader = GetWriter(i_systemTrans, 0, HEADER_LENGTH);
			myreader.Read();
			byte firstFileByte = myreader.ReadByte();
			byte blockLen = 1;
			if (firstFileByte != com.db4o.YapConst.YAPBEGIN)
			{
				if (firstFileByte != com.db4o.YapConst.YAPFILEVERSION)
				{
					com.db4o.inside.Exceptions4.ThrowRuntimeException(17);
				}
				blockLen = myreader.ReadByte();
			}
			else
			{
				if (myreader.ReadByte() != com.db4o.YapConst.YAPFILE)
				{
					com.db4o.inside.Exceptions4.ThrowRuntimeException(17);
				}
			}
			BlockSize(blockLen);
			i_writeAt = BlocksFor(FileLength());
			_configBlock = new com.db4o.YapConfigBlock(this);
			_configBlock.Read(myreader.ReadInt());
			myreader.IncrementOffset(com.db4o.YapConst.YAPID_LENGTH);
			i_classCollection.SetID(myreader.ReadInt());
			i_classCollection.Read(i_systemTrans);
			int freespaceID = myreader.ReadInt();
			_freespaceManager = com.db4o.inside.freespace.FreespaceManager.CreateNew(this, _configBlock
				._freespaceSystem);
			_freespaceManager.Read(freespaceID);
			_freespaceManager.Start(_configBlock._freespaceAddress);
			if (i_config.FreespaceSystem() != 0 || _configBlock._freespaceSystem == com.db4o.inside.freespace.FreespaceManager
				.FM_LEGACY_RAM)
			{
				if (_freespaceManager.SystemType() != i_config.FreespaceSystem())
				{
					com.db4o.inside.freespace.FreespaceManager newFM = com.db4o.inside.freespace.FreespaceManager
						.CreateNew(this, i_config.FreespaceSystem());
					int fmSlot = _configBlock.NewFreespaceSlot(i_config.FreespaceSystem());
					newFM.Start(fmSlot);
					_freespaceManager.Migrate(newFM);
					com.db4o.inside.freespace.FreespaceManager oldFM = _freespaceManager;
					_freespaceManager = newFM;
					oldFM.FreeSelf();
					_freespaceManager.BeginCommit();
					_freespaceManager.EndCommit();
					_configBlock.Write();
				}
			}
			ShowInternalClasses(true);
			object bootRecord = null;
			if (_configBlock._bootRecordID > 0)
			{
				bootRecord = GetByID1(i_systemTrans, _configBlock._bootRecordID);
			}
			if (bootRecord is com.db4o.PBootRecord)
			{
				_bootRecord = (com.db4o.PBootRecord)bootRecord;
				_bootRecord.CheckActive();
				_bootRecord.i_stream = this;
				if (_bootRecord.InitConfig(i_config))
				{
					i_classCollection.ReReadYapClass(GetYapClass(i_handlers.ICLASS_PBOOTRECORD, false
						));
					SetInternal(i_systemTrans, _bootRecord, false);
				}
			}
			else
			{
				InitBootRecord();
			}
			ShowInternalClasses(false);
			WriteHeader(false);
			com.db4o.Transaction trans = _configBlock.GetTransactionToCommit();
			if (trans != null)
			{
				if (!i_config.CommitRecoveryDisabled())
				{
					trans.WriteOld();
				}
			}
			if (com.db4o.inside.convert.Converter.Convert(this, _configBlock))
			{
				GetTransaction().Commit();
			}
		}

		public override void ReleaseSemaphore(string name)
		{
			ReleaseSemaphore(CheckTransaction(null), name);
		}

		internal virtual void ReleaseSemaphore(com.db4o.Transaction ta, string name)
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

		internal override void ReleaseSemaphores(com.db4o.Transaction ta)
		{
			if (i_semaphores != null)
			{
				lock (i_semaphores)
				{
					i_semaphores.ForEachKeyForIdentity(new _AnonymousInnerClass604(this), ta);
					j4o.lang.JavaSystem.NotifyAll(i_semaphores);
				}
			}
		}

		private sealed class _AnonymousInnerClass604 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass604(YapFile _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object a_object)
			{
				this._enclosing.i_semaphores.Remove(a_object);
			}

			private readonly YapFile _enclosing;
		}

		internal sealed override void Rollback1()
		{
			CheckClosed();
			i_entryCounter++;
			GetTransaction().Rollback();
			i_entryCounter--;
		}

		internal sealed override void SetDirty(com.db4o.UseSystemTransaction a_object)
		{
			((com.db4o.YapMeta)a_object).SetStateDirty();
			((com.db4o.YapMeta)a_object).CacheDirty(i_dirty);
		}

		public override bool SetSemaphore(string name, int timeout)
		{
			return SetSemaphore(CheckTransaction(null), name, timeout);
		}

		internal virtual bool SetSemaphore(com.db4o.Transaction ta, string name, int timeout
			)
		{
			if (name == null)
			{
				throw new System.ArgumentNullException();
			}
			if (i_semaphores == null)
			{
				lock (i_lock)
				{
					if (i_semaphores == null)
					{
						i_semaphores = new com.db4o.foundation.Hashtable4(10);
					}
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
					if (i_classCollection == null)
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

		internal virtual void SetServer(bool flag)
		{
			i_isServer = flag;
		}

		public abstract void Copy(int oldAddress, int oldAddressOffset, int newAddress, int
			 newAddressOffset, int length);

		public abstract void SyncFiles();

		public override string ToString()
		{
			return FileName();
		}

		internal override void Write(bool shuttingDown)
		{
			i_trans.Commit();
			if (shuttingDown)
			{
				WriteHeader(shuttingDown);
			}
		}

		internal abstract bool WriteAccessTime();

		internal abstract void WriteBytes(com.db4o.YapReader a_Bytes, int address, int addressOffset
			);

		internal sealed override void WriteDirty()
		{
			com.db4o.YapMeta dirty;
			com.db4o.foundation.Iterator4 i = i_dirty.Iterator();
			while (i.HasNext())
			{
				dirty = (com.db4o.YapMeta)i.Next();
				dirty.Write(i_systemTrans);
				dirty.NotCachedDirty();
			}
			i_dirty.Clear();
			WriteBootRecord();
		}

		public sealed override void WriteEmbedded(com.db4o.YapWriter a_parent, com.db4o.YapWriter
			 a_child)
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
			com.db4o.YapWriter writer = GetWriter(i_systemTrans, 0, HEADER_LENGTH);
			writer.Append(com.db4o.YapConst.YAPFILEVERSION);
			writer.Append(BlockSize());
			writer.WriteInt(_configBlock._address);
			writer.WriteInt(0);
			writer.WriteInt(i_classCollection.GetID());
			writer.WriteInt(freespaceID);
			if (com.db4o.Debug.xbytes && com.db4o.Deploy.overwrite)
			{
				writer.SetID(com.db4o.YapConst.IGNORE_ID);
			}
			writer.Write();
			if (shuttingDown)
			{
				EnsureLastSlotWritten();
			}
			SyncFiles();
		}

		public sealed override void WriteNew(com.db4o.YapClass a_yapClass, com.db4o.YapWriter
			 aWriter)
		{
			WriteObject(null, aWriter, aWriter.GetAddress());
			if (a_yapClass == null)
			{
				return;
			}
			if (MaintainsIndices())
			{
				a_yapClass.AddToIndex(this, aWriter.GetTransaction(), aWriter.GetID());
			}
		}

		internal void WriteObject(com.db4o.YapMeta a_object, com.db4o.YapReader a_writer, 
			int address)
		{
			i_handlers.Encrypt(a_writer);
			WriteBytes(a_writer, address, 0);
		}

		internal virtual void WriteBootRecord()
		{
			_bootRecord.Store(1);
		}

		public abstract void WriteXBytes(int a_address, int a_length);

		internal virtual com.db4o.YapWriter XBytes(int a_address, int a_length)
		{
			throw com.db4o.inside.Exceptions4.VirtualException();
		}

		internal sealed override void WriteTransactionPointer(int a_address)
		{
			com.db4o.YapWriter bytes = new com.db4o.YapWriter(i_systemTrans, _configBlock._address
				, com.db4o.YapConst.YAPINT_LENGTH * 2);
			bytes.MoveForward(com.db4o.YapConfigBlock.TRANSACTION_OFFSET);
			bytes.WriteInt(a_address);
			bytes.WriteInt(a_address);
			if (com.db4o.Debug.xbytes && com.db4o.Deploy.overwrite)
			{
				bytes.SetID(com.db4o.YapConst.IGNORE_ID);
			}
			bytes.Write();
		}

		public void GetSlotForUpdate(com.db4o.YapWriter forWriter)
		{
			com.db4o.Transaction trans = forWriter.GetTransaction();
			int id = forWriter.GetID();
			int length = forWriter.GetLength();
			int address = GetSlot(length);
			forWriter.Address(address);
			trans.SlotFreeOnRollbackSetPointer(id, address, length);
		}

		public sealed override void WriteUpdate(com.db4o.YapClass a_yapClass, com.db4o.YapWriter
			 a_bytes)
		{
			if (a_bytes.GetAddress() == 0)
			{
				GetSlotForUpdate(a_bytes);
			}
			i_handlers.Encrypt(a_bytes);
			a_bytes.Write();
		}
	}
}
