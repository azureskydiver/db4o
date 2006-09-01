namespace com.db4o
{
	/// <exclude></exclude>
	public abstract class YapFile : com.db4o.YapStream
	{
		private com.db4o.ext.Db4oDatabase _identity;

		protected com.db4o.header.FileHeader0 _fileHeader;

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

		public virtual void BlockSize(int blockSize, long fileLength)
		{
			i_writeAt = BlocksFor(fileLength);
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
			_freespaceManager = com.db4o.inside.freespace.FreespaceManager.CreateNew(this, ConfigImpl
				().FreespaceSystem());
			BlockSize(ConfigImpl().BlockSize(), HEADER_LENGTH);
			InitNewClassCollection();
			InitializeEssentialClasses();
			GenerateNewIdentity();
			_fileHeader = com.db4o.header.FileHeader0.ForNewFile(this);
			_freespaceManager.Start(_fileHeader.FreespaceAddress());
			if (com.db4o.Debug.freespace && com.db4o.Debug.freespaceChecker)
			{
				_fmChecker.Start(0);
			}
		}

		public abstract void Copy(int oldAddress, int oldAddressOffset, int newAddress, int
			 newAddressOffset, int length);

		public override long CurrentVersion()
		{
			return _timeStampIdGenerator.LastTimeStampId();
		}

		internal virtual void InitNewClassCollection()
		{
			i_classCollection.InitTables(1);
		}

		public com.db4o.inside.btree.BTree CreateBTreeClassIndex(int id)
		{
			return new com.db4o.inside.btree.BTree(i_trans, id, new com.db4o.YInt(this));
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

		public abstract long FileLength();

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
				i_prefetchedIDs.Traverse(new _AnonymousInnerClass169(this));
			}
			i_prefetchedIDs = null;
		}

		private sealed class _AnonymousInnerClass169 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass169(YapFile _enclosing)
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

		public virtual void GenerateNewIdentity()
		{
			SetIdentity(com.db4o.ext.Db4oDatabase.Generate());
		}

		internal override void GetAll(com.db4o.Transaction ta, com.db4o.QueryResultImpl a_res
			)
		{
			com.db4o.Tree[] duplicates = new com.db4o.Tree[1];
			com.db4o.YapClassCollectionIterator i = i_classCollection.Iterator();
			while (i.MoveNext())
			{
				com.db4o.YapClass yapClass = i.CurrentClass();
				if (yapClass.GetName() != null)
				{
					com.db4o.reflect.ReflectClass claxx = yapClass.ClassReflector();
					if (claxx == null || !(i_handlers.ICLASS_INTERNAL.IsAssignableFrom(claxx)))
					{
						com.db4o.inside.classindex.ClassIndexStrategy index = yapClass.Index();
						index.TraverseAll(ta, new _AnonymousInnerClass210(this, duplicates, a_res));
					}
				}
			}
			a_res.Reset();
		}

		private sealed class _AnonymousInnerClass210 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass210(YapFile _enclosing, com.db4o.Tree[] duplicates, com.db4o.QueryResultImpl
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

		internal int GetPointerSlot()
		{
			int id = GetSlot(com.db4o.YapConst.POINTER_LENGTH);
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
						com.db4o.foundation.Iterator4 i = wrongOnes.Iterator();
						while (i.MoveNext())
						{
							int[] adrLength = (int[])i.Current();
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
			return _identity;
		}

		public virtual void SetIdentity(com.db4o.ext.Db4oDatabase identity)
		{
			_identity = identity;
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
			lock (Lock())
			{
				_timeStampIdGenerator.SetMinimumNext(a_minimumVersion);
			}
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
			SetDefaultBlockSize();
			_fileHeader = new com.db4o.header.FileHeader0();
			_fileHeader.Read0(this);
			i_classCollection.SetID(_fileHeader.ClassCollectionID());
			i_classCollection.Read(i_systemTrans);
			_freespaceManager = com.db4o.inside.freespace.FreespaceManager.CreateNew(this, _fileHeader
				.FreespaceSystem());
			_freespaceManager.Read(_fileHeader.FreeSpaceID());
			_freespaceManager.Start(_fileHeader.FreespaceAddress());
			if (ConfigImpl().FreespaceSystem() != 0 || _fileHeader.FreespaceSystem() == com.db4o.inside.freespace.FreespaceManager
				.FM_LEGACY_RAM)
			{
				if (_freespaceManager.SystemType() != ConfigImpl().FreespaceSystem())
				{
					com.db4o.inside.freespace.FreespaceManager newFM = com.db4o.inside.freespace.FreespaceManager
						.CreateNew(this, ConfigImpl().FreespaceSystem());
					int fmSlot = _fileHeader.NewFreespaceSlot(ConfigImpl().FreespaceSystem());
					newFM.Start(fmSlot);
					_freespaceManager.Migrate(newFM);
					com.db4o.inside.freespace.FreespaceManager oldFM = _freespaceManager;
					_freespaceManager = newFM;
					oldFM.FreeSelf();
					_freespaceManager.BeginCommit();
					_freespaceManager.EndCommit();
					_fileHeader.WriteVariablePart1();
				}
			}
			_fileHeader.ReadBootRecord(this);
			WriteHeader(false);
			com.db4o.Transaction trans = _fileHeader.InterruptedTransaction();
			if (trans != null)
			{
				if (!ConfigImpl().CommitRecoveryDisabled())
				{
					trans.WriteOld();
				}
			}
			if (com.db4o.inside.convert.Converter.Convert(this, _fileHeader))
			{
				GetTransaction().Commit();
			}
		}

		private void SetDefaultBlockSize()
		{
			BlockSize(1, HEADER_LENGTH);
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
					i_semaphores.ForEachKeyForIdentity(new _AnonymousInnerClass521(this), ta);
					j4o.lang.JavaSystem.NotifyAll(i_semaphores);
				}
			}
		}

		private sealed class _AnonymousInnerClass521 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass521(YapFile _enclosing)
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

		public sealed override void SetDirtyInSystemTransaction(com.db4o.YapMeta a_object
			)
		{
			a_object.SetStateDirty();
			a_object.CacheDirty(i_dirty);
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

		public abstract void WriteBytes(com.db4o.YapReader a_Bytes, int address, int addressOffset
			);

		internal sealed override void WriteDirty()
		{
			com.db4o.YapMeta dirty;
			com.db4o.foundation.Iterator4 i = i_dirty.Iterator();
			while (i.MoveNext())
			{
				dirty = (com.db4o.YapMeta)i.Current();
				dirty.Write(i_systemTrans);
				dirty.NotCachedDirty();
			}
			i_dirty.Clear();
			WriteVariableHeader();
		}

		protected virtual void WriteVariableHeader()
		{
			_fileHeader.SetLastTimeStampID(_timeStampIdGenerator.LastTimeStampId());
			_fileHeader.SetIdentity(Identity());
			_fileHeader.WriteVariablePart2();
			_timeStampIdGenerator.SetClean();
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
			_fileHeader.WriteFixedPart(shuttingDown, writer, BlockSize(), i_classCollection.GetID
				(), freespaceID);
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

		public abstract void WriteXBytes(int a_address, int a_length);

		internal virtual com.db4o.YapWriter XBytes(int a_address, int a_length)
		{
			throw com.db4o.inside.Exceptions4.VirtualException();
		}

		internal sealed override void WriteTransactionPointer(int address)
		{
			_fileHeader.WriteTransactionPointer(GetSystemTransaction(), address);
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

		public virtual void SetNextTimeStampId(long val)
		{
			_timeStampIdGenerator.SetMinimumNext(val);
			_timeStampIdGenerator.SetClean();
		}

		public virtual com.db4o.MetaIndex GetUUIDMetaIndex()
		{
			if (_fileHeader == null)
			{
				return null;
			}
			return _fileHeader.GetUUIDMetaIndex();
		}
	}
}
