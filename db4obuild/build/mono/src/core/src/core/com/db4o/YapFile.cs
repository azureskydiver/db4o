/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
MA  02111-1307, USA. */

using System;
using j4o.lang;
using j4o.io;
using com.db4o.ext;
namespace com.db4o {

   abstract internal class YapFile : YapStream {
      internal YapConfigBlock i_configBlock;
      internal PBootRecord i_bootRecord;
      private Collection4 i_dirty;
      private Tree i_freeByAddress;
      private Tree i_freeBySize;
      private bool i_isServer = false;
      private Tree i_prefetchedIDs;
      private Hashtable4 i_semaphores;
      internal int i_writeAt;
      private TreeIntObject i_finder = new TreeIntObject(0);
      
      internal YapFile(YapStream yapstream) : base(yapstream) {
      }
      
      internal virtual byte blockSize() {
         return (byte)1;
      }
      
      internal virtual void blockSize(int i) {
      }
      
      internal override bool close2() {
         bool xbool1 = base.close2();
         i_freeBySize = null;
         i_freeByAddress = null;
         i_dirty = null;
         return xbool1;
      }
      
      internal override void commit1() {
         this.checkClosed();
         try {
            {
               i_entryCounter++;
               write(false);
            }
         }  catch (Exception throwable) {
            {
               this.fatalException(throwable);
            }
         }
         i_entryCounter--;
      }
      
      internal virtual void configureNewFile() {
         blockSize(i_config.i_blockSize);
         i_writeAt = blocksFor(18L);
         i_configBlock = new YapConfigBlock(this, i_config.i_encoding);
         i_configBlock.write();
         i_configBlock.go();
         initNewClassCollection();
         this.initializeEssentialClasses();
         initBootRecord();
      }
      
      internal override long currentVersion() {
         return i_bootRecord.i_versionGenerator;
      }
      
      internal virtual void initNewClassCollection() {
         i_classCollection.initTables(1);
      }
      
      internal override ClassIndex createClassIndex(YapClass yapclass) {
         return new ClassIndex();
      }
      
      internal override QResult createQResult(Transaction transaction) {
         return new QResult(transaction);
      }
      
      internal override bool delete5(Transaction transaction, YapObject yapobject, int i) {
         int i_0_1 = yapobject.getID();
         YapWriter yapwriter1 = readWriterByID(transaction, i_0_1);
         if (yapwriter1 != null) {
            Object obj1 = yapobject.getObject();
            if (obj1 != null && !this.showInternalClasses() && YapConst.CLASS_INTERNAL.isAssignableFrom(j4o.lang.Class.getClassForObject(obj1))) return false;
            yapwriter1.setCascadeDeletes(i);
            transaction.setPointer(i_0_1, 0, 0);
            YapClass yapclass1 = yapobject.getYapClass();
            yapclass1.delete(yapwriter1, obj1);
            transaction.freeOnCommit(i_0_1, yapwriter1.getAddress(), yapwriter1.getLength());
            return true;
         }
         return false;
      }
      
      abstract internal long fileLength();
      
      abstract internal String fileName();
      
      internal void addFreeSlotNodes(int i, int i_1_) {
         FreeSlotNode freeslotnode1 = new FreeSlotNode(i);
         freeslotnode1.createPeer(i_1_);
         i_freeByAddress = Tree.add(i_freeByAddress, freeslotnode1);
         i_freeBySize = Tree.add(i_freeBySize, freeslotnode1.i_peer);
      }
      
      internal virtual void free(int i, int i_2_) {
         if (i_2_ > i_config.i_discardFreeSpace) {
            i_2_ = blocksFor((long)i_2_);
            i_finder.i_key = i;
            FreeSlotNode freeslotnode1 = (FreeSlotNode)Tree.findSmaller(i_freeByAddress, i_finder);
            if (freeslotnode1 != null && freeslotnode1.i_key + freeslotnode1.i_peer.i_key == i) {
               FreeSlotNode freeslotnode_3_1 = freeslotnode1.i_peer;
               i_freeBySize = i_freeBySize.removeNode(freeslotnode_3_1);
               freeslotnode_3_1.i_key += i_2_;
               FreeSlotNode freeslotnode_4_1 = (FreeSlotNode)Tree.findGreaterOrEqual(i_freeByAddress, i_finder);
               if (freeslotnode_4_1 != null && i + i_2_ == freeslotnode_4_1.i_key) {
                  freeslotnode_3_1.i_key += freeslotnode_4_1.i_peer.i_key;
                  i_freeBySize = i_freeBySize.removeNode(freeslotnode_4_1.i_peer);
                  i_freeByAddress = i_freeByAddress.removeNode(freeslotnode_4_1);
               }
               freeslotnode_3_1.removeChildren();
               i_freeBySize = Tree.add(i_freeBySize, freeslotnode_3_1);
            } else {
               freeslotnode1 = (FreeSlotNode)Tree.findGreaterOrEqual(i_freeByAddress, i_finder);
               if (freeslotnode1 != null && i + i_2_ == freeslotnode1.i_key) {
                  FreeSlotNode freeslotnode_5_1 = freeslotnode1.i_peer;
                  i_freeByAddress = i_freeByAddress.removeNode(freeslotnode1);
                  i_freeBySize = i_freeBySize.removeNode(freeslotnode_5_1);
                  freeslotnode_5_1.i_key += i_2_;
                  freeslotnode1.i_key = i;
                  freeslotnode1.removeChildren();
                  freeslotnode_5_1.removeChildren();
                  i_freeByAddress = Tree.add(i_freeByAddress, freeslotnode1);
                  i_freeBySize = Tree.add(i_freeBySize, freeslotnode_5_1);
               } else addFreeSlotNodes(i, i_2_);
            }
         }
      }
      
      internal void freePrefetchedPointers() {
         if (i_prefetchedIDs != null) i_prefetchedIDs.traverse(new YapFile__1(this));
         i_prefetchedIDs = null;
      }
      
      internal override void getAll(Transaction transaction, QResult qresult) {
         Tree[] trees1 = new Tree[1];
         YapClassCollectionIterator yapclasscollectioniterator1 = i_classCollection.iterator();
         while (yapclasscollectioniterator1.hasNext()) {
            YapClass yapclass1 = yapclasscollectioniterator1.nextClass();
            if (yapclass1.getName() != null) {
               Class var_class1 = yapclass1.getJavaClass();
               if (var_class1 == null || !YapConst.CLASS_INTERNAL.isAssignableFrom(var_class1)) {
                  Tree tree1 = yapclass1.getIndex(transaction);
                  if (tree1 != null) tree1.traverse(new YapFile__2(this, trees1, qresult));
               }
            }
         }
      }
      
      internal int getPointerSlot() {
         int i1 = getSlot(8);
         i_systemTrans.writePointer(i1, 0, 0);
         if (i1 <= YapHandlers.maxTypeID()) return getPointerSlot();
         return i1;
      }
      
      private int blocksFor(long l) {
         byte i1 = blockSize();
         int i_6_1 = (int)(l / (long)i1);
         if (l % (long)i1 != 0L) i_6_1++;
         return i_6_1;
      }
      
      internal virtual int getSlot(int i) {
         int i_7_1 = getSlot1(i);
         return i_7_1;
      }
      
      private int getSlot1(int i) {
         int i_8_1 = blocksFor((long)i);
         i_finder.i_key = i_8_1;
         i_finder.i_object = null;
         i_freeBySize = FreeSlotNode.removeGreaterOrEqual((FreeSlotNode)i_freeBySize, i_finder);
         if (i_finder.i_object == null) {
            int i_9_1 = i_writeAt;
            i_writeAt += i_8_1;
            return i_9_1;
         }
         FreeSlotNode freeslotnode1 = (FreeSlotNode)i_finder.i_object;
         int i_10_1 = freeslotnode1.i_key;
         int i_11_1 = freeslotnode1.i_peer.i_key;
         i_freeByAddress = i_freeByAddress.removeNode(freeslotnode1.i_peer);
         if (i_10_1 > i_8_1) addFreeSlotNodes(i_11_1 + i_8_1, i_10_1 - i_8_1);
         return i_11_1;
      }
      
      public override Db4oDatabase identity() {
         return i_bootRecord.i_db;
      }
      
      internal override void initialize2() {
         i_dirty = new Collection4();
         base.initialize2();
      }
      
      private void initBootRecord() {
         this.showInternalClasses(true);
         i_bootRecord = new PBootRecord();
         i_bootRecord.i_stream = this;
         i_bootRecord.init(i_config);
         this.setInternal(i_systemTrans, i_bootRecord, false);
         i_configBlock._bootRecordID = this.getID1(i_systemTrans, i_bootRecord);
         i_configBlock.write();
         this.showInternalClasses(false);
      }
      
      internal override bool isServer() {
         return i_isServer;
      }
      
      internal override YapWriter newObject(Transaction transaction, YapMeta yapmeta) {
         int i1 = yapmeta.ownLength();
         int[] xis1 = newSlot(transaction, i1);
         yapmeta.setID(this, xis1[0]);
         YapWriter yapwriter1 = new YapWriter(transaction, i1);
         yapwriter1.useSlot(xis1[0], xis1[1], i1);
         return yapwriter1;
      }
      
      internal int[] newSlot(Transaction transaction, int i) {
         int i_12_1 = getPointerSlot();
         int i_13_1 = getSlot(i);
         transaction.setPointer(i_12_1, i_13_1, i);
         return new int[]{
            i_12_1,
i_13_1         };
      }
      
      internal override int newUserObject() {
         return getPointerSlot();
      }
      
      internal void prefetchedIDConsumed(int i) {
         i_finder.i_key = i;
         i_prefetchedIDs = i_prefetchedIDs.removeLike(i_finder);
      }
      
      internal int prefetchID() {
         int i1 = getPointerSlot();
         i_prefetchedIDs = Tree.add(i_prefetchedIDs, new TreeInt(i1));
         return i1;
      }
      
      internal override void raiseVersion(long l) {
         if (i_bootRecord.i_versionGenerator < l) {
            i_bootRecord.i_versionGenerator = l;
            i_bootRecord.setDirty();
            i_bootRecord.store(1);
         }
      }
      
      internal override YapWriter readWriterByID(Transaction transaction, int i) {
         if (i == 0) return null;
         int[] xis1 = new int[2];
         try {
            {
               transaction.getSlotInformation(i, xis1);
            }
         }  catch (Exception exception) {
            {
               return null;
            }
         }
         if (xis1[0] == 0) return null;
         YapWriter yapwriter1 = this.getWriter(transaction, xis1[0], xis1[1]);
         yapwriter1.setID(i);
         yapwriter1.readEncrypt(this, xis1[0]);
         return yapwriter1;
      }
      
      internal override YapReader readReaderByID(Transaction transaction, int i) {
         if (i == 0) return null;
         int[] xis1 = new int[2];
         try {
            {
               transaction.getSlotInformation(i, xis1);
            }
         }  catch (Exception exception) {
            {
               return null;
            }
         }
         if (xis1[0] == 0) return null;
         YapReader yapreader1 = new YapReader(xis1[1]);
         yapreader1.readEncrypt(this, xis1[0]);
         return yapreader1;
      }
      
      internal virtual void readThis() {
         YapWriter yapwriter1 = this.getWriter(i_systemTrans, 0, 18);
         yapwriter1.read();
         byte i1 = yapwriter1.readByte();
         byte i_14_1 = 1;
         if (i1 != 123) {
            if (i1 != 4) Db4o.throwRuntimeException(17);
            i_14_1 = yapwriter1.readByte();
         } else if (yapwriter1.readByte() != 89) Db4o.throwRuntimeException(17);
         blockSize(i_14_1);
         i_writeAt = blocksFor(fileLength());
         i_configBlock = new YapConfigBlock(this, i_14_1);
         i_configBlock.read(yapwriter1);
         yapwriter1.incrementOffset(4);
         i_classCollection.setID(this, yapwriter1.readInt());
         i_classCollection.read(i_systemTrans);
         int i_15_1 = yapwriter1.getAddress() + yapwriter1._offset;
         int i_16_1 = yapwriter1.readInt();
         i_freeBySize = null;
         i_freeByAddress = null;
         if (i_16_1 > 0 && i_config.i_discardFreeSpace != 2147483647) {
            YapWriter yapwriter_17_1 = readWriterByID(i_systemTrans, i_16_1);
            if (yapwriter_17_1 != null) {
               FreeSlotNode.sizeLimit = i_config.i_discardFreeSpace;
               i_freeBySize = new TreeReader(yapwriter_17_1, new FreeSlotNode(0), true).read();
               Tree[] trees1 = new Tree[1];
               if (i_freeBySize != null) i_freeBySize.traverse(new YapFile__3(this, trees1));
               i_freeByAddress = trees1[0];
               free(i_16_1, 8);
               free(yapwriter_17_1.getAddress(), yapwriter_17_1.getLength());
            }
         }
         this.showInternalClasses(true);
         Object obj1 = null;
         if (i_configBlock._bootRecordID > 0) obj1 = this.getByID1(i_systemTrans, (long)i_configBlock._bootRecordID);
         if (obj1 is PBootRecord) {
            i_bootRecord = (PBootRecord)obj1;
            i_bootRecord.checkActive();
            i_bootRecord.i_stream = this;
            if (i_bootRecord.initConfig(i_config)) {
               i_classCollection.reReadYapClass(this.getYapClass(YapConst.CLASS_PBOOTRECORD, false));
               this.setInternal(i_systemTrans, i_bootRecord, false);
            }
         } else initBootRecord();
         this.showInternalClasses(false);
         writeHeader(false);
         Transaction transaction1 = i_configBlock.getTransactionToCommit();
         if (transaction1 != null && !i_config.i_disableCommitRecovery) transaction1.writeOld();
      }
      
      public override void releaseSemaphore(String xstring) {
         releaseSemaphore(this.checkTransaction(null), xstring);
      }
      
      internal void releaseSemaphore(Transaction transaction, String xstring) {
         if (i_semaphores != null) {
            lock (i_semaphores) {
               if (i_semaphores != null && transaction == i_semaphores.get(xstring)) {
                  i_semaphores.remove(xstring);
                  j4o.lang.JavaSystem.notifyAll(i_semaphores);
               }
            }
         }
      }
      
      internal override void releaseSemaphores(Transaction transaction) {
         if (i_semaphores != null) {
            lock (i_semaphores) {
               i_semaphores.forEachKey(new YapFile__4(this));
               j4o.lang.JavaSystem.notifyAll(i_semaphores);
            }
         }
      }
      
      internal override void rollback1() {
         this.checkClosed();
         i_entryCounter++;
         this.getTransaction().rollback();
         i_entryCounter--;
      }
      
      internal override void setDirty(UseSystemTransaction usesystemtransaction) {
         ((YapMeta)usesystemtransaction).setStateDirty();
         ((YapMeta)usesystemtransaction).cacheDirty(i_dirty);
      }
      
      public override bool setSemaphore(String xstring, int i) {
         return setSemaphore(this.checkTransaction(null), xstring, i);
      }
      
      internal bool setSemaphore(Transaction transaction, String xstring, int i) {
         if (xstring == null) throw new ArgumentNullException();
         if (i_semaphores == null) {
            lock (i_lock) {
               if (i_semaphores == null) i_semaphores = new Hashtable4(10);
            }
         }
         lock (i_semaphores) {
            Object obj1 = i_semaphores.get(xstring);
            if (obj1 == null) {
               i_semaphores.put(xstring, transaction);
               return true;
            }
            if (transaction == obj1) return true;
            long l1 = j4o.lang.JavaSystem.currentTimeMillis() + (long)i;
            for (long l_18_1 = (long)i; l_18_1 > 0L; l_18_1 = l1 - j4o.lang.JavaSystem.currentTimeMillis()) {
               try {
                  {
                     j4o.lang.JavaSystem.wait(i_semaphores, l_18_1);
                  }
               }  catch (Exception exception) {
                  {
                  }
               }
               if (i_classCollection == null) return false;
               obj1 = i_semaphores.get(xstring);
               if (obj1 == null) {
                  i_semaphores.put(xstring, transaction);
                  return true;
               }
            }
            return false;
         }
      }
      
      internal void setServer(bool xbool) {
         i_isServer = xbool;
      }
      
      abstract internal void copy(int i, int i_19_, int i_20_, int i_21_, int i_22_);
      
      abstract internal void syncFiles();
      
      public override String ToString() {
         return fileName();
      }
      
      internal override YapWriter updateObject(Transaction transaction, YapMeta yapmeta) {
         int i1 = yapmeta.ownLength();
         int i_23_1 = yapmeta.getID();
         int i_24_1 = getSlot(i1);
         int[] xis1 = new int[2];
         transaction.getSlotInformation(i_23_1, xis1);
         transaction.freeOnCommit(i_23_1, xis1[0], xis1[1]);
         transaction.freeOnRollback(i_23_1, i_24_1, i1);
         transaction.setPointer(i_23_1, i_24_1, i1);
         YapWriter yapwriter1 = transaction.i_stream.getWriter(transaction, i1);
         yapwriter1.useSlot(i_23_1, i_24_1, i1);
         return yapwriter1;
      }
      
      internal override void write(bool xbool) {
         i_trans.commit();
         if (xbool) writeHeader(xbool);
      }
      
      abstract internal bool writeAccessTime();
      
      abstract internal void writeBytes(YapWriter yapwriter);
      
      internal override void writeDirty() {
         Iterator4 iterator41 = i_dirty.iterator();
         while (iterator41.hasNext()) {
            YapMeta yapmeta1 = (YapMeta)iterator41.next();
            yapmeta1.write(this, i_systemTrans);
            yapmeta1.notCachedDirty();
         }
         i_dirty.clear();
         writeBootRecord();
      }
      
      internal override void writeEmbedded(YapWriter yapwriter, YapWriter yapwriter_25_) {
         int i1 = yapwriter_25_.getLength();
         int i_26_1 = getSlot(i1);
         yapwriter_25_.getTransaction().freeOnRollback(i_26_1, i_26_1, i1);
         yapwriter_25_.address(i_26_1);
         yapwriter_25_.writeEncrypt();
         int i_27_1 = yapwriter._offset;
         yapwriter._offset = yapwriter_25_.getID();
         yapwriter.writeInt(i_26_1);
         yapwriter._offset = i_27_1;
      }
      
      internal virtual void writeHeader(bool xbool) {
         int i1 = 0;
         if (xbool) {
            int i_28_1 = Tree.byteCount(i_freeBySize);
            int[] xis1 = newSlot(i_systemTrans, i_28_1);
            i1 = xis1[0];
            YapWriter yapwriter2 = new YapWriter(i_systemTrans, i_28_1);
            yapwriter2.useSlot(i1, xis1[1], i_28_1);
            Tree.write(yapwriter2, i_freeBySize);
            yapwriter2.writeEncrypt();
            i_systemTrans.writePointer(xis1[0], xis1[1], i_28_1);
         }
         YapWriter yapwriter1 = this.getWriter(i_systemTrans, 0, 18);
         yapwriter1.append((byte)4);
         yapwriter1.append(blockSize());
         yapwriter1.writeInt(i_configBlock._address);
         yapwriter1.writeInt(0);
         yapwriter1.writeInt(i_classCollection.getID());
         if (xbool) yapwriter1.writeInt(i1); else yapwriter1.writeInt(0);
         yapwriter1.write();
      }
      
      internal override void writeNew(YapClass yapclass, YapWriter yapwriter) {
         writeObject(null, yapwriter);
         if (this.maintainsIndices()) yapclass.addToIndex(this, yapwriter.getTransaction(), yapwriter.getID());
      }
      
      internal void writeObject(YapMeta yapmeta, YapWriter yapwriter) {
         i_handlers.encrypt(yapwriter);
         writeBytes(yapwriter);
      }
      
      internal virtual void writeBootRecord() {
         i_bootRecord.store(1);
      }
      
      abstract internal void writeXBytes(int i, int i_29_);
      
      internal YapWriter xBytes(int i, int i_30_) {
         throw YapConst.virtualException();
      }
      
      internal override void writeTransactionPointer(int i) {
         YapWriter yapwriter1 = new YapWriter(i_systemTrans, i_configBlock._address, 8);
         yapwriter1.moveForward(21);
         yapwriter1.writeInt(i);
         yapwriter1.writeInt(i);
         yapwriter1.write();
      }
      
      internal override void writeUpdate(YapClass yapclass, YapWriter yapwriter) {
         Transaction transaction1 = yapwriter.getTransaction();
         int i1 = yapwriter.getID();
         int i_31_1 = yapwriter.getLength();
         int i_32_1 = getSlot(i_31_1);
         yapwriter.address(i_32_1);
         transaction1.setPointer(i1, i_32_1, i_31_1);
         transaction1.freeOnRollback(i1, i_32_1, i_31_1);
         i_handlers.encrypt(yapwriter);
         yapwriter.write();
      }
      
      static internal Hashtable4 access__000(YapFile yapfile) {
         return yapfile.i_semaphores;
      }
   }
}