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
namespace com.db4o {

   internal class Transaction {
      internal YapStream i_stream;
      internal YapFile i_file;
      internal Transaction i_parentTransaction;
      private YapWriter i_pointerIo;
      private Tree i_slots;
      private Tree i_addToClassIndex;
      private Tree i_removeFromClassIndex;
      private List4 i_freeOnCommit;
      private Tree i_freeOnRollback;
      private List4 i_freeOnBoth;
      private List4 i_dirtyFieldIndexes;
      private List4 i_transactionListeners;
      private int i_address;
      private byte[] i_bytes = new byte[8];
      public Tree i_delete;
      protected Tree i_writtenUpdateDeletedMembers;
      
      internal Transaction(YapStream yapstream, Transaction transaction_0_) : base() {
         i_stream = yapstream;
         i_file = yapstream is YapFile ? (YapFile)yapstream : null;
         i_parentTransaction = transaction_0_;
         i_pointerIo = new YapWriter(this, 8);
      }
      
      public void addTransactionListener(TransactionListener transactionlistener) {
         i_transactionListeners = new List4(i_transactionListeners, transactionlistener);
      }
      
      internal void addDirtyFieldIndex(IxFieldTransaction ixfieldtransaction) {
         i_dirtyFieldIndexes = new List4(i_dirtyFieldIndexes, ixfieldtransaction);
      }
      
      internal void addToClassIndex(int i, int i_1_) {
         removeFromClassIndexTree(i_removeFromClassIndex, i, i_1_);
         i_addToClassIndex = addToClassIndexTree(i_addToClassIndex, i, i_1_);
      }
      
      private Tree addToClassIndexTree(Tree tree, int i, int i_2_) {
         TreeIntObject[] treeintobjects1 = {
            new TreeIntObject(i)         };
         tree = createClassIndexNode(tree, treeintobjects1);
         treeintobjects1[0].i_object = Tree.add((Tree)treeintobjects1[0].i_object, new TreeInt(i_2_));
         return tree;
      }
      
      internal virtual void beginEndSet() {
         if (i_delete != null) {
            bool[] bools1 = {
               false            };
            Transaction transaction_3_1 = this;
            do {
               bools1[0] = false;
               Tree tree1 = i_delete;
               i_delete = null;
               tree1.traverse(new Transaction__1(this, bools1, transaction_3_1));
            }             while (bools1[0]);
         }
         i_delete = null;
         i_writtenUpdateDeletedMembers = null;
      }
      
      private int calculateLength() {
         return (2 + Tree.size(i_slots) * 3) * 4 + Tree.byteCount(i_addToClassIndex) + Tree.byteCount(i_removeFromClassIndex);
      }
      
      private void clearAll() {
         i_slots = null;
         i_addToClassIndex = null;
         i_removeFromClassIndex = null;
         i_freeOnCommit = null;
         i_freeOnRollback = null;
         i_dirtyFieldIndexes = null;
         i_transactionListeners = null;
      }
      
      private Tree createClassIndexNode(Tree tree, Tree[] trees) {
         if (tree != null) {
            Tree tree_4_1 = tree.find(trees[0]);
            if (tree_4_1 != null) trees[0] = tree_4_1; else tree = tree.add(trees[0]);
         } else tree = trees[0];
         return tree;
      }
      
      internal void close(bool xbool) {
         try {
            {
               if (i_stream != null) i_stream.releaseSemaphores(this);
            }
         }  catch (Exception exception) {
            {
            }
         }
         if (xbool) {
            try {
               {
                  rollback();
               }
            }  catch (Exception exception) {
               {
               }
            }
         }
      }
      
      internal void commitTransactionListeners() {
         if (i_transactionListeners != null) {
            Iterator4 iterator41 = new Iterator4(i_transactionListeners);
            while (iterator41.hasNext()) ((TransactionListener)iterator41.next()).preCommit();
            i_transactionListeners = null;
         }
      }
      
      internal virtual void commit() {
         lock (i_stream.i_lock) {
            commitTransactionListeners();
            i_stream.checkNeededUpdates();
            i_stream.writeDirty();
            i_stream.i_classCollection.write(i_stream, i_stream.getSystemTransaction());
            if (i_dirtyFieldIndexes != null) {
               Iterator4 iterator41 = new Iterator4(i_dirtyFieldIndexes);
               while (iterator41.hasNext()) ((IxFieldTransaction)iterator41.next()).commit();
            }
            if (i_parentTransaction != null) i_parentTransaction.commit(); else i_stream.writeDirty();
            write();
            freeOnCommit();
            clearAll();
         }
      }
      
      internal virtual void delete(YapObject yapobject, Object obj, int i, bool xbool) {
         int i_5_1 = yapobject.getID();
         TreeIntObject treeintobject1 = (TreeIntObject)TreeInt.find(i_delete, i_5_1);
         if (treeintobject1 == null) {
            treeintobject1 = new TreeIntObject(i_5_1, new Object[]{
               yapobject,
System.Convert.ToInt32(i)            });
            i_delete = Tree.add(i_delete, treeintobject1);
         } else if (xbool) deleteCollectionMembers(yapobject, obj, i);
      }
      
      private void deleteCollectionMembers(YapObject yapobject, Object obj, int i) {
         if (obj != null && Platform.isCollection(j4o.lang.Class.getClassForObject(obj))) writeUpdateDeleteMembers(yapobject.getID(), yapobject.getYapClass(), YapHandlers.arrayType(obj), i);
      }
      
      internal virtual void dontDelete(int i, bool xbool) {
         TreeIntObject treeintobject1 = (TreeIntObject)TreeInt.find(i_delete, i);
         if (treeintobject1 != null) {
            if (xbool && treeintobject1.i_object != null) {
               Object[] objs1 = (Object[])treeintobject1.i_object;
               YapObject yapobject1 = (YapObject)objs1[0];
               int i_6_1 = System.Convert.ToInt32((Int32)objs1[1]);
               deleteCollectionMembers(yapobject1, yapobject1.getObject(), i_6_1);
            }
            treeintobject1.i_object = null;
         } else {
            treeintobject1 = new TreeIntObject(i, null);
            i_delete = Tree.add(i_delete, treeintobject1);
         }
      }
      
      internal void dontRemoveFromClassIndex(int i, int i_7_) {
         removeFromClassIndexTree(i_removeFromClassIndex, i, i_7_);
         YapClass yapclass1 = i_stream.getYapClass(i);
         if (TreeInt.find(yapclass1.getIndexRoot(), i_7_) == null) addToClassIndex(i, i_7_);
      }
      
      internal virtual bool isDeleted(int i) {
         Slot slot1 = findSlotInHierarchy(i);
         if (slot1 != null) return slot1.i_address == 0;
         return false;
      }
      
      private Slot findSlot(int i) {
         TreeInt treeint1 = TreeInt.find(i_slots, i);
         if (treeint1 != null) return (Slot)((TreeIntObject)treeint1).i_object;
         return null;
      }
      
      private Slot findSlotInHierarchy(int i) {
         Slot slot1 = findSlot(i);
         if (slot1 != null) return slot1;
         if (i_parentTransaction != null) return i_parentTransaction.findSlotInHierarchy(i);
         return null;
      }
      
      private void freeOnBoth() {
         Iterator4 iterator41 = new Iterator4(i_freeOnBoth);
         while (iterator41.hasNext()) {
            Slot slot1 = (Slot)iterator41.next();
            i_file.free(slot1.i_address, slot1.i_length);
         }
         i_freeOnBoth = null;
      }
      
      private void freeOnCommit() {
         freeOnBoth();
         if (i_freeOnCommit != null) {
            Iterator4 iterator41 = new Iterator4(i_freeOnCommit);
            while (iterator41.hasNext()) {
               int i1 = System.Convert.ToInt32((Int32)iterator41.next());
               TreeInt treeint1 = TreeInt.find(i_stream.i_freeOnCommit, i1);
               if (treeint1 != null) {
                  Slot slot1 = (Slot)((TreeIntObject)treeint1).i_object;
                  i_file.free(slot1.i_address, slot1.i_length);
                  slot1.i_references--;
                  bool xbool1 = true;
                  if (slot1.i_references > 0) {
                     TreeInt treeint_8_1 = TreeInt.find(i_freeOnRollback, i1);
                     if (treeint_8_1 != null) {
                        Slot slot_9_1 = (Slot)((TreeIntObject)treeint_8_1).i_object;
                        if (slot1.i_address != slot_9_1.i_address) {
                           slot1.i_address = slot_9_1.i_address;
                           slot1.i_length = slot_9_1.i_length;
                           xbool1 = false;
                        }
                     }
                  }
                  if (xbool1) i_stream.i_freeOnCommit = i_stream.i_freeOnCommit.removeNode(treeint1);
               }
            }
         }
         i_freeOnCommit = null;
      }
      
      internal void freeOnCommit(int i, int i_10_, int i_11_) {
         if (i != 0) {
            TreeInt treeint1 = TreeInt.find(i_freeOnRollback, i);
            if (treeint1 != null) {
               i_freeOnBoth = new List4(i_freeOnBoth, ((TreeIntObject)treeint1).i_object);
               i_freeOnRollback = i_freeOnRollback.removeNode(treeint1);
            } else {
               TreeInt treeint_12_1 = TreeInt.find(i_stream.i_freeOnCommit, i);
               if (treeint_12_1 != null) {
                  Slot slot1 = (Slot)((TreeIntObject)treeint_12_1).i_object;
                  slot1.i_references++;
               } else {
                  Slot slot1 = new Slot(i_10_, i_11_);
                  slot1.i_references = 1;
                  i_stream.i_freeOnCommit = Tree.add(i_stream.i_freeOnCommit, new TreeIntObject(i, slot1));
               }
               i_freeOnCommit = new List4(i_freeOnCommit, System.Convert.ToInt32(i));
            }
         }
      }
      
      internal virtual void freeOnRollback(int i, int i_13_, int i_14_) {
         i_freeOnRollback = Tree.add(i_freeOnRollback, new TreeIntObject(i, new Slot(i_13_, i_14_)));
      }
      
      internal void freePointer(int i) {
         freeOnCommit(i, i, 8);
      }
      
      internal void getSlotInformation(int i, int[] xis) {
         if (i != 0) {
            Slot slot1 = findSlot(i);
            if (slot1 != null) {
               xis[0] = slot1.i_address;
               xis[1] = slot1.i_length;
            } else {
               if (i_parentTransaction != null) {
                  i_parentTransaction.getSlotInformation(i, xis);
                  if (xis[0] != 0) return;
               }
               i_file.readBytes(i_bytes, i, 8);
               xis[0] = i_bytes[3] & 255 | (i_bytes[2] & 255) << 8 | (i_bytes[1] & 255) << 16 | i_bytes[0] << 24;
               xis[1] = i_bytes[7] & 255 | (i_bytes[6] & 255) << 8 | (i_bytes[5] & 255) << 16 | i_bytes[4] << 24;
            }
         }
      }
      
      internal virtual Object[] objectAndYapObjectBySignature(long l, byte[] xis) {
         Object[] objs1 = new Object[2];
         IxTree ixtree1 = (IxTree)i_stream.i_handlers.i_indexes.i_fieldUUID.getIndexRoot(this);
         IxTraverser ixtraverser1 = new IxTraverser();
         int i1 = ixtraverser1.findBoundsExactMatch(System.Convert.ToInt64(l), ixtree1);
         if (i1 > 0) {
            QCandidates qcandidates1 = new QCandidates(this, null, null);
            Tree tree1 = ixtraverser1.getMatches(qcandidates1);
            if (tree1 != null) {
               Transaction transaction_15_1 = this;
               tree1.traverse(new Transaction__2(this, transaction_15_1, xis, objs1));
            }
         }
         return objs1;
      }
      
      internal void removeFromClassIndex(int i, int i_16_) {
         removeFromClassIndexTree(i_addToClassIndex, i, i_16_);
         i_removeFromClassIndex = addToClassIndexTree(i_removeFromClassIndex, i, i_16_);
      }
      
      private void removeFromClassIndexTree(Tree tree, int i, int i_17_) {
         if (tree != null) {
            TreeIntObject treeintobject1 = (TreeIntObject)((TreeInt)tree).find(i);
            if (treeintobject1 != null) treeintobject1.i_object = Tree.removeLike((Tree)treeintobject1.i_object, new TreeInt(i_17_));
         }
      }
      
      public virtual void rollback() {
         lock (i_stream.i_lock) {
            if (i_dirtyFieldIndexes != null) {
               Iterator4 iterator41 = new Iterator4(i_dirtyFieldIndexes);
               while (iterator41.hasNext()) ((IxFieldTransaction)iterator41.next()).rollback();
            }
            if (i_freeOnCommit != null) {
               Iterator4 iterator41 = new Iterator4(i_freeOnCommit);
               while (iterator41.hasNext()) {
                  TreeInt treeint1 = TreeInt.find(i_stream.i_freeOnCommit, System.Convert.ToInt32((Int32)iterator41.next()));
                  if (treeint1 != null) {
                     Slot slot1 = (Slot)((TreeIntObject)treeint1).i_object;
                     slot1.i_references--;
                     if (slot1.i_references < 1) i_stream.i_freeOnCommit = i_stream.i_freeOnCommit.removeNode(treeint1);
                  }
               }
            }
            if (i_freeOnRollback != null) i_freeOnRollback.traverse(new Transaction__3(this));
            freeOnBoth();
            rollBackTransactionListeners();
            clearAll();
         }
      }
      
      internal void rollBackTransactionListeners() {
         if (i_transactionListeners != null) {
            Iterator4 iterator41 = new Iterator4(i_transactionListeners);
            while (iterator41.hasNext()) ((TransactionListener)iterator41.next()).postRollback();
            i_transactionListeners = null;
         }
      }
      
      internal void setAddress(int i) {
         i_address = i;
      }
      
      internal virtual void setPointer(int i, int i_18_, int i_19_) {
         Slot slot1 = findSlot(i);
         if (slot1 != null) {
            slot1.i_address = i_18_;
            slot1.i_length = i_19_;
         } else i_slots = Tree.add(i_slots, new TreeIntObject(i, new Slot(i_18_, i_19_)));
      }
      
      internal void traverseAddedClassIDs(int i, Visitor4 visitor4) {
         traverseDeep(i_addToClassIndex, i, visitor4);
      }
      
      internal void traverseRemovedClassIDs(int i, Visitor4 visitor4) {
         traverseDeep(i_removeFromClassIndex, i, visitor4);
      }
      
      internal void traverseDeep(Tree tree, int i, Visitor4 visitor4) {
         if (tree != null) {
            TreeIntObject treeintobject1 = (TreeIntObject)((TreeInt)tree).find(i);
            if (treeintobject1 != null && treeintobject1.i_object != null) ((Tree)treeintobject1.i_object).traverse(visitor4);
         }
      }
      
      private void write() {
         if (i_slots != null || i_addToClassIndex != null || i_removeFromClassIndex != null) {
            int i1 = calculateLength();
            int i_20_1 = ((YapFile)i_stream).getSlot(i1);
            freeOnCommit(i_20_1, i_20_1, i1);
            YapWriter yapwriter1 = new YapWriter(this, i_20_1, i1);
            yapwriter1.writeInt(i1);
            Tree.write(yapwriter1, i_slots);
            Tree.write(yapwriter1, i_addToClassIndex);
            Tree.write(yapwriter1, i_removeFromClassIndex);
            yapwriter1.write();
            i_stream.writeTransactionPointer(i_20_1);
            writeSlots();
            i_stream.writeTransactionPointer(0);
         }
      }
      
      private void traverseYapClassEntries(Tree tree, bool xbool, Collection4 collection4) {
         if (tree != null) tree.traverse(new Transaction__4(this, xbool, collection4));
      }
      
      private void writeSlots() {
         Collection4 collection41 = new Collection4();
         traverseYapClassEntries(i_addToClassIndex, true, collection41);
         traverseYapClassEntries(i_removeFromClassIndex, false, collection41);
         Iterator4 iterator41 = collection41.iterator();
         while (iterator41.hasNext()) {
            ClassIndex classindex1 = (ClassIndex)iterator41.next();
            classindex1.setDirty(i_stream);
            classindex1.write(i_stream, this);
         }
         if (i_slots != null) i_slots.traverse(new Transaction__7(this));
      }
      
      internal void writeOld() {
         lock (i_stream.i_lock) {
            i_pointerIo.useSlot(i_address);
            i_pointerIo.read();
            int i1 = i_pointerIo.readInt();
            if (i1 > 0) {
               YapWriter yapwriter1 = new YapWriter(this, i_address, i1);
               yapwriter1.read();
               yapwriter1.incrementOffset(4);
               i_slots = new TreeReader(yapwriter1, new TreeIntObject(0, new Slot(0, 0))).read();
               i_addToClassIndex = new TreeReader(yapwriter1, new TreeIntObject(0, new TreeInt(0))).read();
               i_removeFromClassIndex = new TreeReader(yapwriter1, new TreeIntObject(0, new TreeInt(0))).read();
               writeSlots();
               i_stream.writeTransactionPointer(0);
               freeOnCommit();
            } else i_stream.writeTransactionPointer(0);
         }
      }
      
      internal void writePointer(int i, int i_21_, int i_22_) {
         i_pointerIo.useSlot(i);
         i_pointerIo.writeInt(i_21_);
         i_pointerIo.writeInt(i_22_);
         i_pointerIo.write();
      }
      
      internal virtual void writeUpdateDeleteMembers(int i, YapClass yapclass, int i_23_, int i_24_) {
         if (Tree.find(i_writtenUpdateDeletedMembers, new TreeInt(i)) == null) {
            i_writtenUpdateDeletedMembers = Tree.add(i_writtenUpdateDeletedMembers, new TreeInt(i));
            YapWriter yapwriter1 = i_stream.readWriterByID(this, i);
            if (yapwriter1 != null) yapclass.readObjectHeader(yapwriter1, i); else if (yapclass.hasIndex()) dontRemoveFromClassIndex(yapclass.getID(), i);
            if (yapwriter1 != null) {
               yapwriter1.setCascadeDeletes(i_24_);
               yapclass.deleteMembers(yapwriter1, i_23_);
               freeOnCommit(i, yapwriter1.getAddress(), yapwriter1.getLength());
            }
         }
      }
      
      public override String ToString() {
         return i_stream.ToString();
      }
   }
}