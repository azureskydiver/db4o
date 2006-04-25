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

		public virtual byte blockSize()
		{
			return 1;
		}

		internal virtual void blockSize(int blockSize)
		{
		}

		public override com.db4o.PBootRecord bootRecord()
		{
			return _bootRecord;
		}

		internal override bool close2()
		{
			bool ret = base.close2();
			i_dirty = null;
			return ret;
		}

		internal override void commit1()
		{
			checkClosed();
			i_entryCounter++;
			try
			{
				write(false);
			}
			catch (System.Exception t)
			{
				fatalException(t);
			}
			i_entryCounter--;
		}

		internal virtual void configureNewFile()
		{
			_freespaceManager = com.db4o.inside.freespace.FreespaceManager.createNew(this, i_config
				.freespaceSystem());
			blockSize(i_config.blockSize());
			i_writeAt = blocksFor(HEADER_LENGTH);
			_configBlock = new com.db4o.YapConfigBlock(this);
			_configBlock.write();
			_configBlock.go();
			initNewClassCollection();
			initializeEssentialClasses();
			initBootRecord();
			_freespaceManager.start(_configBlock._freespaceAddress);
			if (com.db4o.Debug.freespace && com.db4o.Debug.freespaceChecker)
			{
				_fmChecker.start(0);
			}
		}

		internal override long currentVersion()
		{
			return _bootRecord.currentVersion();
		}

		internal virtual void initNewClassCollection()
		{
			i_classCollection.initTables(1);
		}

		internal sealed override com.db4o.ClassIndex createClassIndex(com.db4o.YapClass yapClass
			)
		{
			return new com.db4o.ClassIndex(yapClass);
		}

		internal sealed override com.db4o.inside.btree.BTree createBTreeClassIndex(com.db4o.YapClass
			 a_yapClass, int id)
		{
			com.db4o.inside.btree.BTree btree = new com.db4o.inside.btree.BTree(id, new com.db4o.YInt
				(this), null);
			if (id == 0)
			{
				btree.write(getSystemTransaction());
			}
			return btree;
		}

		internal sealed override com.db4o.QueryResultImpl createQResult(com.db4o.Transaction
			 a_ta)
		{
			return new com.db4o.QueryResultImpl(a_ta);
		}

		internal sealed override bool delete5(com.db4o.Transaction ta, com.db4o.YapObject
			 yo, int a_cascade, bool userCall)
		{
			int id = yo.getID();
			com.db4o.YapWriter reader = readWriterByID(ta, id);
			if (reader != null)
			{
				object obj = yo.getObject();
				if (obj != null)
				{
					if ((!showInternalClasses()) && com.db4o.YapConst.CLASS_INTERNAL.isAssignableFrom
						(j4o.lang.Class.getClassForObject(obj)))
					{
						return false;
					}
				}
				reader.setCascadeDeletes(a_cascade);
				reader.slotDelete();
				com.db4o.YapClass yc = yo.getYapClass();
				yc.delete(reader, obj);
				return true;
			}
			return false;
		}

		internal abstract long fileLength();

		internal abstract string fileName();

		public virtual void free(com.db4o.inside.slots.Slot slot)
		{
			if (slot == null)
			{
				return;
			}
			if (slot._address == 0)
			{
				return;
			}
			free(slot._address, slot._length);
		}

		public virtual void free(int a_address, int a_length)
		{
			_freespaceManager.free(a_address, a_length);
			if (com.db4o.Debug.freespace && com.db4o.Debug.freespaceChecker)
			{
				_fmChecker.free(a_address, a_length);
			}
		}

		internal void freePrefetchedPointers()
		{
			if (i_prefetchedIDs != null)
			{
				i_prefetchedIDs.traverse(new _AnonymousInnerClass180(this));
			}
			i_prefetchedIDs = null;
		}

		private sealed class _AnonymousInnerClass180 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass180(YapFile _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void visit(object a_object)
			{
				this._enclosing.free(((com.db4o.TreeInt)a_object)._key, com.db4o.YapConst.POINTER_LENGTH
					);
			}

			private readonly YapFile _enclosing;
		}

		internal void freeSpaceBeginCommit()
		{
			if (_freespaceManager == null)
			{
				return;
			}
			_freespaceManager.beginCommit();
		}

		internal void freeSpaceEndCommit()
		{
			if (_freespaceManager == null)
			{
				return;
			}
			_freespaceManager.endCommit();
		}

		internal override void getAll(com.db4o.Transaction ta, com.db4o.QueryResultImpl a_res
			)
		{
			com.db4o.Tree[] duplicates = new com.db4o.Tree[1];
			com.db4o.YapClassCollectionIterator i = i_classCollection.iterator();
			while (i.hasNext())
			{
				com.db4o.YapClass yapClass = i.readNextClass();
				if (yapClass.getName() != null)
				{
					com.db4o.reflect.ReflectClass claxx = yapClass.classReflector();
					if (claxx == null || !(i_handlers.ICLASS_INTERNAL.isAssignableFrom(claxx)))
					{
						if (com.db4o.Debug.useOldClassIndex && !com.db4o.Debug.useBTrees)
						{
							com.db4o.Tree tree = yapClass.getIndex(ta);
							if (tree != null)
							{
								tree.traverse(new _AnonymousInnerClass240(this, duplicates, a_res));
							}
						}
					}
				}
			}
			a_res.reset();
		}

		private sealed class _AnonymousInnerClass240 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass240(YapFile _enclosing, com.db4o.Tree[] duplicates, com.db4o.QueryResultImpl
				 a_res)
			{
				this._enclosing = _enclosing;
				this.duplicates = duplicates;
				this.a_res = a_res;
			}

			public void visit(object obj)
			{
				int id = ((com.db4o.TreeInt)obj)._key;
				com.db4o.TreeInt newNode = new com.db4o.TreeInt(id);
				duplicates[0] = com.db4o.Tree.add(duplicates[0], newNode);
				if (newNode.size() != 0)
				{
					a_res.add(id);
				}
			}

			private readonly YapFile _enclosing;

			private readonly com.db4o.Tree[] duplicates;

			private readonly com.db4o.QueryResultImpl a_res;
		}

		internal int getPointerSlot()
		{
			int id = getSlot(com.db4o.YapConst.POINTER_LENGTH);
			i_systemTrans.writePointer(id, 0, 0);
			if (id <= i_handlers.maxTypeID())
			{
				return getPointerSlot();
			}
			return id;
		}

		public virtual int blocksFor(long bytes)
		{
			int blockLen = blockSize();
			int result = (int)(bytes / blockLen);
			if (bytes % blockLen != 0)
			{
				result++;
			}
			return result;
		}

		public virtual int getSlot(int a_length)
		{
			return getSlot1(a_length);
			int address = getSlot1(a_length);
			com.db4o.DTrace.GET_SLOT.logLength(address, a_length);
			return address;
		}

		private int getSlot1(int bytes)
		{
			if (_freespaceManager != null)
			{
				int freeAddress = _freespaceManager.getSlot(bytes);
				if (com.db4o.Debug.freespace && com.db4o.Debug.freespaceChecker)
				{
					if (freeAddress > 0)
					{
						com.db4o.foundation.Collection4 wrongOnes = new com.db4o.foundation.Collection4();
						int freeCheck = _fmChecker.getSlot(bytes);
						while (freeCheck != freeAddress && freeCheck > 0)
						{
							wrongOnes.add(new int[] { freeCheck, bytes });
							freeCheck = _fmChecker.getSlot(bytes);
						}
						com.db4o.foundation.Iterator4 i = wrongOnes.iterator();
						while (i.hasNext())
						{
							int[] adrLength = (int[])i.next();
							_fmChecker.free(adrLength[0], adrLength[1]);
						}
						if (freeCheck == 0)
						{
							_freespaceManager.debug();
							_fmChecker.debug();
						}
					}
				}
				if (freeAddress > 0)
				{
					return freeAddress;
				}
			}
			int blocksNeeded = blocksFor(bytes);
			if (com.db4o.Debug.xbytes && com.db4o.Deploy.overwrite)
			{
				writeXBytes(i_writeAt, blocksNeeded * blockSize());
			}
			int address = i_writeAt;
			i_writeAt += blocksNeeded;
			return address;
		}

		internal virtual void ensureLastSlotWritten()
		{
			if (i_writeAt > blocksFor(fileLength()))
			{
				com.db4o.YapWriter writer = getWriter(i_systemTrans, i_writeAt - 1, blockSize());
				writer.write();
			}
		}

		public override com.db4o.ext.Db4oDatabase identity()
		{
			if (_bootRecord == null)
			{
				return null;
			}
			return _bootRecord.i_db;
		}

		internal override void initialize2()
		{
			i_dirty = new com.db4o.foundation.Collection4();
			base.initialize2();
		}

		private void initBootRecord()
		{
			showInternalClasses(true);
			_bootRecord = new com.db4o.PBootRecord();
			_bootRecord.i_stream = this;
			_bootRecord.init(i_config);
			setInternal(i_systemTrans, _bootRecord, false);
			_configBlock._bootRecordID = getID1(i_systemTrans, _bootRecord);
			_configBlock.write();
			showInternalClasses(false);
		}

		internal override bool isServer()
		{
			return i_isServer;
		}

		internal com.db4o.YapWriter newObject(com.db4o.Transaction a_trans, com.db4o.YapMeta
			 a_object)
		{
			int length = a_object.ownLength();
			com.db4o.inside.slots.Pointer4 ptr = newSlot(a_trans, length);
			a_object.setID(ptr._id);
			com.db4o.YapWriter writer = new com.db4o.YapWriter(a_trans, length);
			writer.useSlot(ptr._id, ptr._address, length);
			return writer;
		}

		public com.db4o.inside.slots.Pointer4 newSlot(com.db4o.Transaction a_trans, int a_length
			)
		{
			int id = getPointerSlot();
			int address = getSlot(a_length);
			a_trans.setPointer(id, address, a_length);
			return new com.db4o.inside.slots.Pointer4(id, address);
		}

		internal sealed override int newUserObject()
		{
			return getPointerSlot();
		}

		internal virtual void prefetchedIDConsumed(int a_id)
		{
			i_prefetchedIDs = i_prefetchedIDs.removeLike(new com.db4o.TreeIntObject(a_id));
		}

		internal virtual int prefetchID()
		{
			int id = getPointerSlot();
			i_prefetchedIDs = com.db4o.Tree.add(i_prefetchedIDs, new com.db4o.TreeInt(id));
			return id;
		}

		public virtual com.db4o.inside.slots.ReferencedSlot produceFreeOnCommitEntry(int 
			id)
		{
			com.db4o.Tree node = com.db4o.TreeInt.find(_freeOnCommit, id);
			if (node != null)
			{
				return (com.db4o.inside.slots.ReferencedSlot)node;
			}
			com.db4o.inside.slots.ReferencedSlot slot = new com.db4o.inside.slots.ReferencedSlot
				(id);
			_freeOnCommit = com.db4o.Tree.add(_freeOnCommit, slot);
			return slot;
		}

		public virtual void reduceFreeOnCommitReferences(com.db4o.inside.slots.ReferencedSlot
			 slot)
		{
			if (slot.removeReferenceIsLast())
			{
				_freeOnCommit = _freeOnCommit.removeNode(slot);
			}
		}

		public virtual void freeDuringCommit(com.db4o.inside.slots.ReferencedSlot referencedSlot
			, com.db4o.inside.slots.Slot slot)
		{
			_freeOnCommit = referencedSlot.free(this, _freeOnCommit, slot);
		}

		public override void raiseVersion(long a_minimumVersion)
		{
			_bootRecord.raiseVersion(a_minimumVersion);
		}

		public override com.db4o.YapWriter readWriterByID(com.db4o.Transaction a_ta, int 
			a_id)
		{
			return (com.db4o.YapWriter)readReaderOrWriterByID(a_ta, a_id, false);
		}

		public override com.db4o.YapReader readReaderByID(com.db4o.Transaction a_ta, int 
			a_id)
		{
			return readReaderOrWriterByID(a_ta, a_id, true);
		}

		private com.db4o.YapReader readReaderOrWriterByID(com.db4o.Transaction a_ta, int 
			a_id, bool useReader)
		{
			if (a_id == 0)
			{
				return null;
			}
			try
			{
				com.db4o.inside.slots.Slot slot = a_ta.getSlotInformation(a_id);
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
					reader = getWriter(a_ta, slot._address, slot._length);
					((com.db4o.YapWriter)reader).setID(a_id);
				}
				reader.readEncrypt(this, slot._address);
				return reader;
			}
			catch (System.Exception e)
			{
			}
			return null;
		}

		internal virtual void readThis()
		{
			com.db4o.YapWriter myreader = getWriter(i_systemTrans, 0, HEADER_LENGTH);
			myreader.read();
			byte firstFileByte = myreader.readByte();
			byte blockLen = 1;
			if (firstFileByte != com.db4o.YapConst.YAPBEGIN)
			{
				if (firstFileByte != com.db4o.YapConst.YAPFILEVERSION)
				{
					com.db4o.inside.Exceptions4.throwRuntimeException(17);
				}
				blockLen = myreader.readByte();
			}
			else
			{
				if (myreader.readByte() != com.db4o.YapConst.YAPFILE)
				{
					com.db4o.inside.Exceptions4.throwRuntimeException(17);
				}
			}
			blockSize(blockLen);
			i_writeAt = blocksFor(fileLength());
			_configBlock = new com.db4o.YapConfigBlock(this);
			_configBlock.read(myreader.readInt());
			myreader.incrementOffset(com.db4o.YapConst.YAPID_LENGTH);
			i_classCollection.setID(myreader.readInt());
			i_classCollection.read(i_systemTrans);
			int freespaceID = myreader.readInt();
			_freespaceManager = com.db4o.inside.freespace.FreespaceManager.createNew(this, _configBlock
				._freespaceSystem);
			_freespaceManager.read(freespaceID);
			_freespaceManager.start(_configBlock._freespaceAddress);
			if (i_config.freespaceSystem() != 0 || _configBlock._freespaceSystem == com.db4o.inside.freespace.FreespaceManager
				.FM_LEGACY_RAM)
			{
				if (_freespaceManager.systemType() != i_config.freespaceSystem())
				{
					com.db4o.inside.freespace.FreespaceManager newFM = com.db4o.inside.freespace.FreespaceManager
						.createNew(this, i_config.freespaceSystem());
					int fmSlot = _configBlock.newFreespaceSlot(i_config.freespaceSystem());
					newFM.start(fmSlot);
					_freespaceManager.migrate(newFM);
					com.db4o.inside.freespace.FreespaceManager oldFM = _freespaceManager;
					_freespaceManager = newFM;
					oldFM.freeSelf();
					_freespaceManager.beginCommit();
					_freespaceManager.endCommit();
					_configBlock.write();
				}
			}
			showInternalClasses(true);
			object bootRecord = null;
			if (_configBlock._bootRecordID > 0)
			{
				bootRecord = getByID1(i_systemTrans, _configBlock._bootRecordID);
			}
			if (bootRecord is com.db4o.PBootRecord)
			{
				_bootRecord = (com.db4o.PBootRecord)bootRecord;
				_bootRecord.checkActive();
				_bootRecord.i_stream = this;
				if (_bootRecord.initConfig(i_config))
				{
					i_classCollection.reReadYapClass(getYapClass(i_handlers.ICLASS_PBOOTRECORD, false
						));
					setInternal(i_systemTrans, _bootRecord, false);
				}
			}
			else
			{
				initBootRecord();
			}
			showInternalClasses(false);
			writeHeader(false);
			com.db4o.Transaction trans = _configBlock.getTransactionToCommit();
			if (trans != null)
			{
				if (!i_config.commitRecoveryDisabled())
				{
					trans.writeOld();
				}
			}
		}

		public override void releaseSemaphore(string name)
		{
			releaseSemaphore(checkTransaction(null), name);
		}

		internal virtual void releaseSemaphore(com.db4o.Transaction ta, string name)
		{
			if (i_semaphores != null)
			{
				lock (i_semaphores)
				{
					if (i_semaphores != null && ta == i_semaphores.get(name))
					{
						i_semaphores.remove(name);
						j4o.lang.JavaSystem.notifyAll(i_semaphores);
					}
				}
			}
		}

		internal override void releaseSemaphores(com.db4o.Transaction ta)
		{
			if (i_semaphores != null)
			{
				lock (i_semaphores)
				{
					i_semaphores.forEachKeyForIdentity(new _AnonymousInnerClass622(this), ta);
					j4o.lang.JavaSystem.notifyAll(i_semaphores);
				}
			}
		}

		private sealed class _AnonymousInnerClass622 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass622(YapFile _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void visit(object a_object)
			{
				this._enclosing.i_semaphores.remove(a_object);
			}

			private readonly YapFile _enclosing;
		}

		internal sealed override void rollback1()
		{
			checkClosed();
			i_entryCounter++;
			getTransaction().rollback();
			i_entryCounter--;
		}

		internal sealed override void setDirty(com.db4o.UseSystemTransaction a_object)
		{
			((com.db4o.YapMeta)a_object).setStateDirty();
			((com.db4o.YapMeta)a_object).cacheDirty(i_dirty);
		}

		public override bool setSemaphore(string name, int timeout)
		{
			return setSemaphore(checkTransaction(null), name, timeout);
		}

		internal virtual bool setSemaphore(com.db4o.Transaction ta, string name, int timeout
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
				object obj = i_semaphores.get(name);
				if (obj == null)
				{
					i_semaphores.put(name, ta);
					return true;
				}
				if (ta == obj)
				{
					return true;
				}
				long endtime = j4o.lang.JavaSystem.currentTimeMillis() + timeout;
				long waitTime = timeout;
				while (waitTime > 0)
				{
					try
					{
						j4o.lang.JavaSystem.wait(i_semaphores, waitTime);
					}
					catch (System.Exception e)
					{
					}
					if (i_classCollection == null)
					{
						return false;
					}
					obj = i_semaphores.get(name);
					if (obj == null)
					{
						i_semaphores.put(name, ta);
						return true;
					}
					waitTime = endtime - j4o.lang.JavaSystem.currentTimeMillis();
				}
				return false;
			}
		}

		internal virtual void setServer(bool flag)
		{
			i_isServer = flag;
		}

		public abstract void copy(int oldAddress, int oldAddressOffset, int newAddress, int
			 newAddressOffset, int length);

		public abstract void syncFiles();

		public override string ToString()
		{
			return fileName();
		}

		internal com.db4o.YapWriter updateObject(com.db4o.Transaction a_trans, com.db4o.YapMeta
			 a_object)
		{
			int length = a_object.ownLength();
			int id = a_object.getID();
			int address = getSlot(length);
			a_trans.slotFreeOnRollbackCommitSetPointer(id, address, length);
			com.db4o.YapWriter writer = a_trans.i_stream.getWriter(a_trans, length);
			writer.useSlot(id, address, length);
			return writer;
		}

		internal override void write(bool shuttingDown)
		{
			i_trans.commit();
			if (shuttingDown)
			{
				writeHeader(shuttingDown);
			}
		}

		internal abstract bool writeAccessTime();

		internal abstract void writeBytes(com.db4o.YapReader a_Bytes, int address, int addressOffset
			);

		internal sealed override void writeDirty()
		{
			com.db4o.YapMeta dirty;
			com.db4o.foundation.Iterator4 i = i_dirty.iterator();
			while (i.hasNext())
			{
				dirty = (com.db4o.YapMeta)i.next();
				dirty.write(i_systemTrans);
				dirty.notCachedDirty();
			}
			i_dirty.clear();
			writeBootRecord();
		}

		internal sealed override void writeEmbedded(com.db4o.YapWriter a_parent, com.db4o.YapWriter
			 a_child)
		{
			int length = a_child.getLength();
			int address = getSlot(length);
			a_child.getTransaction().slotFreeOnRollback(address, address, length);
			a_child.address(address);
			a_child.writeEncrypt();
			int offsetBackup = a_parent._offset;
			a_parent._offset = a_child.getID();
			a_parent.writeInt(address);
			a_parent._offset = offsetBackup;
		}

		internal virtual void writeHeader(bool shuttingDown)
		{
			int freespaceID = _freespaceManager.write(shuttingDown);
			if (shuttingDown)
			{
				_freespaceManager = null;
			}
			if (com.db4o.Debug.freespace && com.db4o.Debug.freespaceChecker)
			{
				freespaceID = _fmChecker.write(shuttingDown);
			}
			com.db4o.YapWriter writer = getWriter(i_systemTrans, 0, HEADER_LENGTH);
			writer.append(com.db4o.YapConst.YAPFILEVERSION);
			writer.append(blockSize());
			writer.writeInt(_configBlock._address);
			writer.writeInt(0);
			writer.writeInt(i_classCollection.getID());
			writer.writeInt(freespaceID);
			if (com.db4o.Debug.xbytes && com.db4o.Deploy.overwrite)
			{
				writer.setID(com.db4o.YapConst.IGNORE_ID);
			}
			writer.write();
			if (shuttingDown)
			{
				ensureLastSlotWritten();
			}
			syncFiles();
		}

		internal sealed override void writeNew(com.db4o.YapClass a_yapClass, com.db4o.YapWriter
			 aWriter)
		{
			writeObject(null, aWriter, aWriter.getAddress());
			if (maintainsIndices())
			{
				a_yapClass.addToIndex(this, aWriter.getTransaction(), aWriter.getID());
			}
		}

		internal void writeObject(com.db4o.YapMeta a_object, com.db4o.YapReader a_writer, 
			int address)
		{
			i_handlers.encrypt(a_writer);
			writeBytes(a_writer, address, 0);
		}

		internal virtual void writeBootRecord()
		{
			_bootRecord.store(1);
		}

		public abstract void writeXBytes(int a_address, int a_length);

		internal virtual com.db4o.YapWriter xBytes(int a_address, int a_length)
		{
			throw com.db4o.YapConst.virtualException();
		}

		internal sealed override void writeTransactionPointer(int a_address)
		{
			com.db4o.YapWriter bytes = new com.db4o.YapWriter(i_systemTrans, _configBlock._address
				, com.db4o.YapConst.YAPINT_LENGTH * 2);
			bytes.moveForward(com.db4o.YapConfigBlock.TRANSACTION_OFFSET);
			bytes.writeInt(a_address);
			bytes.writeInt(a_address);
			if (com.db4o.Debug.xbytes && com.db4o.Deploy.overwrite)
			{
				bytes.setID(com.db4o.YapConst.IGNORE_ID);
			}
			bytes.write();
		}

		internal sealed override void writeUpdate(com.db4o.YapClass a_yapClass, com.db4o.YapWriter
			 a_bytes)
		{
			com.db4o.Transaction trans = a_bytes.getTransaction();
			int id = a_bytes.getID();
			int length = a_bytes.getLength();
			int address = getSlot(length);
			a_bytes.address(address);
			trans.slotFreeOnRollbackSetPointer(id, address, length);
			i_handlers.encrypt(a_bytes);
			a_bytes.write();
		}
	}
}
