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
	internal abstract class YapFile : com.db4o.YapStream
	{
		internal com.db4o.YapConfigBlock i_configBlock;

		internal com.db4o.PBootRecord i_bootRecord;

		private com.db4o.Collection4 i_dirty;

		private com.db4o.Tree i_freeByAddress;

		private com.db4o.Tree i_freeBySize;

		private bool i_isServer = false;

		private com.db4o.Tree i_prefetchedIDs;

		private com.db4o.Hashtable4 i_semaphores;

		internal int i_writeAt;

		private readonly com.db4o.TreeIntObject i_finder = new com.db4o.TreeIntObject(0);

		internal YapFile(com.db4o.YapStream a_parent) : base(a_parent)
		{
		}

		internal virtual byte blockSize()
		{
			return 1;
		}

		internal virtual void blockSize(int blockSize)
		{
		}

		internal override bool close2()
		{
			bool ret = base.close2();
			i_freeBySize = null;
			i_freeByAddress = null;
			i_dirty = null;
			return ret;
		}

		internal sealed override void commit1()
		{
			checkClosed();
			try
			{
				i_entryCounter++;
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
			blockSize(i_config.i_blockSize);
			i_writeAt = blocksFor(HEADER_LENGTH);
			i_configBlock = new com.db4o.YapConfigBlock(this, i_config.i_encoding);
			i_configBlock.write();
			i_configBlock.go();
			initNewClassCollection();
			initializeEssentialClasses();
			initBootRecord();
		}

		internal override long currentVersion()
		{
			return i_bootRecord.i_versionGenerator;
		}

		internal virtual void initNewClassCollection()
		{
			i_classCollection.initTables(1);
		}

		internal sealed override com.db4o.ClassIndex createClassIndex(com.db4o.YapClass a_yapClass
			)
		{
			return new com.db4o.ClassIndex();
		}

		internal sealed override com.db4o.QResult createQResult(com.db4o.Transaction a_ta
			)
		{
			return new com.db4o.QResult(a_ta);
		}

		internal sealed override bool delete5(com.db4o.Transaction ta, com.db4o.YapObject
			 yo, int a_cascade)
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
				ta.setPointer(id, 0, 0);
				com.db4o.YapClass yc = yo.getYapClass();
				yc.delete(reader, obj);
				ta.freeOnCommit(id, reader.getAddress(), reader.getLength());
				return true;
			}
			return false;
		}

		internal abstract long fileLength();

		internal abstract string fileName();

		internal virtual void addFreeSlotNodes(int a_address, int a_length)
		{
			com.db4o.FreeSlotNode addressNode = new com.db4o.FreeSlotNode(a_address);
			addressNode.createPeer(a_length);
			i_freeByAddress = com.db4o.Tree.add(i_freeByAddress, addressNode);
			i_freeBySize = com.db4o.Tree.add(i_freeBySize, addressNode.i_peer);
		}

		internal virtual void free(int a_address, int a_length)
		{
			if (a_length > i_config.i_discardFreeSpace)
			{
				a_length = blocksFor(a_length);
				i_finder.i_key = a_address;
				com.db4o.FreeSlotNode sizeNode;
				com.db4o.FreeSlotNode addressnode = (com.db4o.FreeSlotNode)com.db4o.Tree.findSmaller
					(i_freeByAddress, i_finder);
				if ((addressnode != null) && ((addressnode.i_key + addressnode.i_peer.i_key) == a_address
					))
				{
					sizeNode = addressnode.i_peer;
					i_freeBySize = i_freeBySize.removeNode(sizeNode);
					sizeNode.i_key += a_length;
					com.db4o.FreeSlotNode secondAddressNode = (com.db4o.FreeSlotNode)com.db4o.Tree.findGreaterOrEqual
						(i_freeByAddress, i_finder);
					if ((secondAddressNode != null) && (a_address + a_length == secondAddressNode.i_key
						))
					{
						sizeNode.i_key += secondAddressNode.i_peer.i_key;
						i_freeBySize = i_freeBySize.removeNode(secondAddressNode.i_peer);
						i_freeByAddress = i_freeByAddress.removeNode(secondAddressNode);
					}
					sizeNode.removeChildren();
					i_freeBySize = com.db4o.Tree.add(i_freeBySize, sizeNode);
				}
				else
				{
					addressnode = (com.db4o.FreeSlotNode)com.db4o.Tree.findGreaterOrEqual(i_freeByAddress
						, i_finder);
					if ((addressnode != null) && (a_address + a_length == addressnode.i_key))
					{
						sizeNode = addressnode.i_peer;
						i_freeByAddress = i_freeByAddress.removeNode(addressnode);
						i_freeBySize = i_freeBySize.removeNode(sizeNode);
						sizeNode.i_key += a_length;
						addressnode.i_key = a_address;
						addressnode.removeChildren();
						sizeNode.removeChildren();
						i_freeByAddress = com.db4o.Tree.add(i_freeByAddress, addressnode);
						i_freeBySize = com.db4o.Tree.add(i_freeBySize, sizeNode);
					}
					else
					{
						addFreeSlotNodes(a_address, a_length);
					}
				}
			}
		}

		internal void freePrefetchedPointers()
		{
			if (i_prefetchedIDs != null)
			{
				i_prefetchedIDs.traverse(new _AnonymousInnerClass183(this));
			}
			i_prefetchedIDs = null;
		}

		private sealed class _AnonymousInnerClass183 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass183(YapFile _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void visit(object a_object)
			{
				this._enclosing.free(((com.db4o.TreeInt)a_object).i_key, com.db4o.YapConst.POINTER_LENGTH
					);
			}

			private readonly YapFile _enclosing;
		}

		internal override void getAll(com.db4o.Transaction ta, com.db4o.QResult a_res)
		{
			com.db4o.Tree[] duplicates = new com.db4o.Tree[1];
			com.db4o.YapClassCollectionIterator i = i_classCollection.iterator();
			while (i.hasNext())
			{
				com.db4o.YapClass yapClass = i.nextClass();
				if (yapClass.getName() != null)
				{
					com.db4o.reflect.ReflectClass claxx = yapClass.classReflector();
					if (claxx == null || !(i_handlers.ICLASS_INTERNAL.isAssignableFrom(claxx)))
					{
						com.db4o.Tree tree = yapClass.getIndex(ta);
						if (tree != null)
						{
							tree.traverse(new _AnonymousInnerClass207(this, duplicates, a_res));
						}
					}
				}
			}
		}

		private sealed class _AnonymousInnerClass207 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass207(YapFile _enclosing, com.db4o.Tree[] duplicates, com.db4o.QResult
				 a_res)
			{
				this._enclosing = _enclosing;
				this.duplicates = duplicates;
				this.a_res = a_res;
			}

			public void visit(object obj)
			{
				int id = ((com.db4o.TreeInt)obj).i_key;
				com.db4o.TreeInt newNode = new com.db4o.TreeInt(id);
				duplicates[0] = com.db4o.Tree.add(duplicates[0], newNode);
				if (newNode.i_size != 0)
				{
					a_res.add(id);
				}
			}

			private readonly YapFile _enclosing;

			private readonly com.db4o.Tree[] duplicates;

			private readonly com.db4o.QResult a_res;
		}

		internal int getPointerSlot()
		{
			int id = getSlot(com.db4o.YapConst.POINTER_LENGTH);
			i_systemTrans.writePointer(id, 0, 0);
			if (id <= com.db4o.YapHandlers.maxTypeID())
			{
				return getPointerSlot();
			}
			return id;
		}

		private int blocksFor(long bytes)
		{
			int blockLen = blockSize();
			int result = (int)(bytes / blockLen);
			if (bytes % blockLen != 0)
			{
				result++;
			}
			return result;
		}

		internal virtual int getSlot(int a_length)
		{
			int address = getSlot1(a_length);
			return address;
		}

		private int getSlot1(int bytes)
		{
			int blocksNeeded = blocksFor(bytes);
			i_finder.i_key = blocksNeeded;
			i_finder.i_object = null;
			i_freeBySize = com.db4o.FreeSlotNode.removeGreaterOrEqual((com.db4o.FreeSlotNode)
				i_freeBySize, i_finder);
			if (i_finder.i_object == null)
			{
				if (com.db4o.Deploy.debug && com.db4o.Deploy.overwrite)
				{
					writeXBytes(i_writeAt, blocksNeeded * blockSize());
				}
				int slotAddress = i_writeAt;
				i_writeAt += blocksNeeded;
				return slotAddress;
			}
			com.db4o.FreeSlotNode node = (com.db4o.FreeSlotNode)i_finder.i_object;
			int blocksFound = node.i_key;
			int address = node.i_peer.i_key;
			i_freeByAddress = i_freeByAddress.removeNode(node.i_peer);
			if (blocksFound > blocksNeeded)
			{
				addFreeSlotNodes(address + blocksNeeded, blocksFound - blocksNeeded);
			}
			return address;
		}

		public override com.db4o.ext.Db4oDatabase identity()
		{
			return i_bootRecord.i_db;
		}

		internal override void initialize2()
		{
			i_dirty = new com.db4o.Collection4();
			base.initialize2();
		}

		private void initBootRecord()
		{
			showInternalClasses(true);
			i_bootRecord = new com.db4o.PBootRecord();
			i_bootRecord.i_stream = this;
			i_bootRecord.init(i_config);
			setInternal(i_systemTrans, i_bootRecord, false);
			i_configBlock._bootRecordID = getID1(i_systemTrans, i_bootRecord);
			i_configBlock.write();
			showInternalClasses(false);
		}

		internal override bool isServer()
		{
			return i_isServer;
		}

		internal sealed override com.db4o.YapWriter newObject(com.db4o.Transaction a_trans
			, com.db4o.YapMeta a_object)
		{
			int length = a_object.ownLength();
			int[] slot = newSlot(a_trans, length);
			a_object.setID(this, slot[0]);
			com.db4o.YapWriter writer = new com.db4o.YapWriter(a_trans, length);
			writer.useSlot(slot[0], slot[1], length);
			return writer;
		}

		internal int[] newSlot(com.db4o.Transaction a_trans, int a_length)
		{
			int id = getPointerSlot();
			int address = getSlot(a_length);
			a_trans.setPointer(id, address, a_length);
			return new int[] { id, address };
		}

		internal sealed override int newUserObject()
		{
			return getPointerSlot();
		}

		internal virtual void prefetchedIDConsumed(int a_id)
		{
			i_finder.i_key = a_id;
			i_prefetchedIDs = i_prefetchedIDs.removeLike(i_finder);
		}

		internal virtual int prefetchID()
		{
			int id = getPointerSlot();
			i_prefetchedIDs = com.db4o.Tree.add(i_prefetchedIDs, new com.db4o.TreeInt(id));
			return id;
		}

		internal override void raiseVersion(long a_minimumVersion)
		{
			if (i_bootRecord.i_versionGenerator < a_minimumVersion)
			{
				i_bootRecord.i_versionGenerator = a_minimumVersion;
				i_bootRecord.setDirty();
				i_bootRecord.store(1);
			}
		}

		internal override com.db4o.YapWriter readWriterByID(com.db4o.Transaction a_ta, int
			 a_id)
		{
			if (a_id == 0)
			{
				return null;
			}
			int[] addressLength = new int[2];
			try
			{
				a_ta.getSlotInformation(a_id, addressLength);
			}
			catch (System.Exception e)
			{
				return null;
			}
			if (addressLength[0] == 0)
			{
				return null;
			}
			com.db4o.YapWriter reader = getWriter(a_ta, addressLength[0], addressLength[1]);
			reader.setID(a_id);
			reader.readEncrypt(this, addressLength[0]);
			return reader;
		}

		internal override com.db4o.YapReader readReaderByID(com.db4o.Transaction a_ta, int
			 a_id)
		{
			if (a_id == 0)
			{
				return null;
			}
			int[] addressLength = new int[2];
			try
			{
				a_ta.getSlotInformation(a_id, addressLength);
			}
			catch (System.Exception e)
			{
				return null;
			}
			if (addressLength[0] == 0)
			{
				return null;
			}
			com.db4o.YapReader reader = new com.db4o.YapReader(addressLength[1]);
			reader.readEncrypt(this, addressLength[0]);
			return reader;
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
					com.db4o.Db4o.throwRuntimeException(17);
				}
				blockLen = myreader.readByte();
			}
			else
			{
				if (myreader.readByte() != com.db4o.YapConst.YAPFILE)
				{
					com.db4o.Db4o.throwRuntimeException(17);
				}
			}
			blockSize(blockLen);
			i_writeAt = blocksFor(fileLength());
			i_configBlock = new com.db4o.YapConfigBlock(this, blockLen);
			i_configBlock.read(myreader);
			myreader.incrementOffset(com.db4o.YapConst.YAPID_LENGTH);
			i_classCollection.setID(this, myreader.readInt());
			i_classCollection.read(i_systemTrans);
			int freeID = myreader.getAddress() + myreader._offset;
			int freeSlotsID = myreader.readInt();
			i_freeBySize = null;
			i_freeByAddress = null;
			if (freeSlotsID > 0 && (i_config.i_discardFreeSpace != int.MaxValue))
			{
				com.db4o.YapWriter reader = readWriterByID(i_systemTrans, freeSlotsID);
				if (reader != null)
				{
					com.db4o.FreeSlotNode.sizeLimit = i_config.i_discardFreeSpace;
					i_freeBySize = new com.db4o.TreeReader(reader, new com.db4o.FreeSlotNode(0), true
						).read();
					com.db4o.Tree[] addressTree = new com.db4o.Tree[1];
					if (i_freeBySize != null)
					{
						i_freeBySize.traverse(new _AnonymousInnerClass484(this, addressTree));
					}
					i_freeByAddress = addressTree[0];
					free(freeSlotsID, com.db4o.YapConst.POINTER_LENGTH);
					free(reader.getAddress(), reader.getLength());
				}
			}
			showInternalClasses(true);
			object bootRecord = null;
			if (i_configBlock._bootRecordID > 0)
			{
				bootRecord = getByID1(i_systemTrans, i_configBlock._bootRecordID);
			}
			if (bootRecord is com.db4o.PBootRecord)
			{
				i_bootRecord = (com.db4o.PBootRecord)bootRecord;
				i_bootRecord.checkActive();
				i_bootRecord.i_stream = this;
				if (i_bootRecord.initConfig(i_config))
				{
					i_classCollection.reReadYapClass(getYapClass(i_handlers.ICLASS_PBOOTRECORD, false
						));
					setInternal(i_systemTrans, i_bootRecord, false);
				}
			}
			else
			{
				initBootRecord();
			}
			showInternalClasses(false);
			writeHeader(false);
			com.db4o.Transaction trans = i_configBlock.getTransactionToCommit();
			if (trans != null)
			{
				if (!i_config.i_disableCommitRecovery)
				{
					trans.writeOld();
				}
			}
		}

		private sealed class _AnonymousInnerClass484 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass484(YapFile _enclosing, com.db4o.Tree[] addressTree)
			{
				this._enclosing = _enclosing;
				this.addressTree = addressTree;
			}

			public void visit(object a_object)
			{
				com.db4o.FreeSlotNode node = ((com.db4o.FreeSlotNode)a_object).i_peer;
				addressTree[0] = com.db4o.Tree.add(addressTree[0], node);
			}

			private readonly YapFile _enclosing;

			private readonly com.db4o.Tree[] addressTree;
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
					i_semaphores.forEachKey(new _AnonymousInnerClass543(this));
					j4o.lang.JavaSystem.notifyAll(i_semaphores);
				}
			}
		}

		private sealed class _AnonymousInnerClass543 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass543(YapFile _enclosing)
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
						i_semaphores = new com.db4o.Hashtable4(10);
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

		internal abstract void copy(int oldAddress, int oldAddressOffset, int newAddress, 
			int newAddressOffset, int length);

		internal abstract void syncFiles();

		public override string ToString()
		{
			return fileName();
		}

		internal sealed override com.db4o.YapWriter updateObject(com.db4o.Transaction a_trans
			, com.db4o.YapMeta a_object)
		{
			int length = a_object.ownLength();
			int id = a_object.getID();
			int address = getSlot(length);
			int[] oldAddressLength = new int[2];
			a_trans.getSlotInformation(id, oldAddressLength);
			a_trans.freeOnCommit(id, oldAddressLength[0], oldAddressLength[1]);
			a_trans.freeOnRollback(id, address, length);
			a_trans.setPointer(id, address, length);
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

		internal abstract void writeBytes(com.db4o.YapWriter a_Bytes);

		internal sealed override void writeDirty()
		{
			com.db4o.YapMeta dirty;
			com.db4o.Iterator4 i = i_dirty.iterator();
			while (i.hasNext())
			{
				dirty = (com.db4o.YapMeta)i.next();
				dirty.write(this, i_systemTrans);
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
			a_child.getTransaction().freeOnRollback(address, address, length);
			a_child.address(address);
			a_child.writeEncrypt();
			int offsetBackup = a_parent._offset;
			a_parent._offset = a_child.getID();
			a_parent.writeInt(address);
			a_parent._offset = offsetBackup;
		}

		internal virtual void writeHeader(bool shuttingDown)
		{
			int freeBySizeID = 0;
			if (shuttingDown)
			{
				int length = com.db4o.Tree.byteCount(i_freeBySize);
				int[] slot = newSlot(i_systemTrans, length);
				freeBySizeID = slot[0];
				com.db4o.YapWriter sdwriter = new com.db4o.YapWriter(i_systemTrans, length);
				sdwriter.useSlot(freeBySizeID, slot[1], length);
				com.db4o.Tree.write(sdwriter, i_freeBySize);
				sdwriter.writeEncrypt();
				i_systemTrans.writePointer(slot[0], slot[1], length);
			}
			com.db4o.YapWriter writer = getWriter(i_systemTrans, 0, HEADER_LENGTH);
			writer.append(com.db4o.YapConst.YAPFILEVERSION);
			writer.append(blockSize());
			writer.writeInt(i_configBlock._address);
			writer.writeInt(0);
			writer.writeInt(i_classCollection.getID());
			if (shuttingDown)
			{
				writer.writeInt(freeBySizeID);
			}
			else
			{
				writer.writeInt(0);
			}
			if (com.db4o.Deploy.debug && com.db4o.Deploy.overwrite)
			{
				writer.setID(com.db4o.YapConst.IGNORE_ID);
			}
			writer.write();
		}

		internal sealed override void writeNew(com.db4o.YapClass a_yapClass, com.db4o.YapWriter
			 aWriter)
		{
			writeObject(null, aWriter);
			if (maintainsIndices())
			{
				a_yapClass.addToIndex(this, aWriter.getTransaction(), aWriter.getID());
			}
		}

		internal void writeObject(com.db4o.YapMeta a_object, com.db4o.YapWriter a_writer)
		{
			i_handlers.encrypt(a_writer);
			writeBytes(a_writer);
		}

		internal virtual void writeBootRecord()
		{
			i_bootRecord.store(1);
		}

		internal abstract void writeXBytes(int a_address, int a_length);

		internal virtual com.db4o.YapWriter xBytes(int a_address, int a_length)
		{
			throw com.db4o.YapConst.virtualException();
		}

		internal sealed override void writeTransactionPointer(int a_address)
		{
			com.db4o.YapWriter bytes = new com.db4o.YapWriter(i_systemTrans, i_configBlock._address
				, com.db4o.YapConst.YAPINT_LENGTH * 2);
			bytes.moveForward(com.db4o.YapConfigBlock.TRANSACTION_OFFSET);
			bytes.writeInt(a_address);
			bytes.writeInt(a_address);
			if (com.db4o.Deploy.debug && com.db4o.Deploy.overwrite)
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
			trans.setPointer(id, address, length);
			trans.freeOnRollback(id, address, length);
			i_handlers.encrypt(a_bytes);
			a_bytes.write();
		}
	}
}
