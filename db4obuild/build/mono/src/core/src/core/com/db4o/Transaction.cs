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
	/// <exclude></exclude>
	public class Transaction
	{
		public readonly com.db4o.YapStream i_stream;

		internal readonly com.db4o.YapFile i_file;

		internal readonly com.db4o.Transaction i_parentTransaction;

		private readonly com.db4o.YapWriter i_pointerIo;

		private com.db4o.Tree i_slots;

		private com.db4o.Tree i_addToClassIndex;

		private com.db4o.Tree i_removeFromClassIndex;

		private com.db4o.List4 i_freeOnCommit;

		private com.db4o.Tree i_freeOnRollback;

		private com.db4o.List4 i_freeOnBoth;

		private com.db4o.List4 i_dirtyFieldIndexes;

		private com.db4o.List4 i_transactionListeners;

		private int i_address;

		private byte[] i_bytes = new byte[com.db4o.YapConst.POINTER_LENGTH];

		public com.db4o.Tree i_delete;

		protected com.db4o.Tree i_writtenUpdateDeletedMembers;

		internal Transaction(com.db4o.YapStream a_stream, com.db4o.Transaction a_parent)
		{
			i_stream = a_stream;
			i_file = (a_stream is com.db4o.YapFile) ? (com.db4o.YapFile)a_stream : null;
			i_parentTransaction = a_parent;
			i_pointerIo = new com.db4o.YapWriter(this, com.db4o.YapConst.POINTER_LENGTH);
		}

		public virtual void addTransactionListener(com.db4o.TransactionListener a_listener
			)
		{
			i_transactionListeners = new com.db4o.List4(i_transactionListeners, a_listener);
		}

		internal virtual void addDirtyFieldIndex(com.db4o.IxFieldTransaction a_xft)
		{
			i_dirtyFieldIndexes = new com.db4o.List4(i_dirtyFieldIndexes, a_xft);
		}

		internal virtual void addToClassIndex(int a_yapClassID, int a_id)
		{
			removeFromClassIndexTree(i_removeFromClassIndex, a_yapClassID, a_id);
			i_addToClassIndex = addToClassIndexTree(i_addToClassIndex, a_yapClassID, a_id);
		}

		private com.db4o.Tree addToClassIndexTree(com.db4o.Tree a_tree, int a_yapClassID, 
			int a_id)
		{
			com.db4o.TreeIntObject[] node = new com.db4o.TreeIntObject[] { new com.db4o.TreeIntObject
				(a_yapClassID) };
			a_tree = createClassIndexNode(a_tree, node);
			node[0].i_object = com.db4o.Tree.add((com.db4o.Tree)node[0].i_object, new com.db4o.TreeInt
				(a_id));
			return a_tree;
		}

		internal virtual void beginEndSet()
		{
			if (i_delete != null)
			{
				bool[] foundOne = { false };
				com.db4o.Transaction finalThis = this;
				do
				{
					foundOne[0] = false;
					com.db4o.Tree delete = i_delete;
					i_delete = null;
					delete.traverse(new _AnonymousInnerClass100(this, foundOne, finalThis));
				}
				while (foundOne[0]);
			}
			i_delete = null;
			i_writtenUpdateDeletedMembers = null;
		}

		private sealed class _AnonymousInnerClass100 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass100(Transaction _enclosing, bool[] foundOne, com.db4o.Transaction
				 finalThis)
			{
				this._enclosing = _enclosing;
				this.foundOne = foundOne;
				this.finalThis = finalThis;
			}

			public void visit(object a_object)
			{
				com.db4o.TreeIntObject tio = (com.db4o.TreeIntObject)a_object;
				if (tio.i_object != null)
				{
					object[] arr = (object[])tio.i_object;
					foundOne[0] = true;
					com.db4o.YapObject yo = (com.db4o.YapObject)arr[0];
					int cascade = ((int)arr[1]);
					object obj = yo.getObject();
					if (obj == null)
					{
						arr = finalThis.i_stream.getObjectAndYapObjectByID(finalThis, yo.getID());
						obj = arr[0];
						yo = (com.db4o.YapObject)arr[1];
					}
					this._enclosing.i_stream.delete4(finalThis, yo, obj, cascade, false);
				}
				this._enclosing.i_delete = com.db4o.Tree.add(this._enclosing.i_delete, new com.db4o.TreeIntObject
					(tio.i_key, null));
			}

			private readonly Transaction _enclosing;

			private readonly bool[] foundOne;

			private readonly com.db4o.Transaction finalThis;
		}

		private int calculateLength()
		{
			return ((2 + (com.db4o.Tree.size(i_slots) * 3)) * com.db4o.YapConst.YAPINT_LENGTH
				) + com.db4o.Tree.byteCount(i_addToClassIndex) + com.db4o.Tree.byteCount(i_removeFromClassIndex
				);
		}

		private void clearAll()
		{
			i_slots = null;
			i_addToClassIndex = null;
			i_removeFromClassIndex = null;
			i_freeOnCommit = null;
			i_freeOnRollback = null;
			i_dirtyFieldIndexes = null;
			i_transactionListeners = null;
		}

		private com.db4o.Tree createClassIndexNode(com.db4o.Tree a_tree, com.db4o.Tree[] 
			a_node)
		{
			if (a_tree != null)
			{
				com.db4o.Tree existing = a_tree.find(a_node[0]);
				if (existing != null)
				{
					a_node[0] = existing;
				}
				else
				{
					a_tree = a_tree.add(a_node[0]);
				}
			}
			else
			{
				a_tree = a_node[0];
			}
			return a_tree;
		}

		internal virtual void close(bool a_rollbackOnClose)
		{
			try
			{
				if (i_stream != null)
				{
					i_stream.releaseSemaphores(this);
				}
			}
			catch (System.Exception e)
			{
			}
			if (a_rollbackOnClose)
			{
				try
				{
					rollback();
				}
				catch (System.Exception e)
				{
				}
			}
		}

		internal virtual void commitTransactionListeners()
		{
			if (i_transactionListeners != null)
			{
				com.db4o.Iterator4 i = new com.db4o.Iterator4(i_transactionListeners);
				while (i.hasNext())
				{
					((com.db4o.TransactionListener)i.next()).preCommit();
				}
				i_transactionListeners = null;
			}
		}

		internal virtual void commit()
		{
			lock (i_stream.i_lock)
			{
				beginEndSet();
				commitTransactionListeners();
				i_stream.checkNeededUpdates();
				i_stream.writeDirty();
				i_stream.i_classCollection.write(i_stream, i_stream.getSystemTransaction());
				if (i_dirtyFieldIndexes != null)
				{
					com.db4o.Iterator4 i = new com.db4o.Iterator4(i_dirtyFieldIndexes);
					while (i.hasNext())
					{
						((com.db4o.IxFieldTransaction)i.next()).commit();
					}
				}
				if (i_parentTransaction != null)
				{
					i_parentTransaction.commit();
				}
				else
				{
					i_stream.writeDirty();
				}
				write();
				freeOnCommit();
				clearAll();
			}
		}

		internal virtual void delete(com.db4o.YapObject a_yo, object a_object, int a_cascade
			, bool a_deleteMembers)
		{
			int id = a_yo.getID();
			com.db4o.TreeIntObject tio = (com.db4o.TreeIntObject)com.db4o.TreeInt.find(i_delete
				, id);
			if (tio == null)
			{
				tio = new com.db4o.TreeIntObject(id, new object[] { a_yo, a_cascade });
				i_delete = com.db4o.Tree.add(i_delete, tio);
			}
			else
			{
				if (a_deleteMembers)
				{
					deleteCollectionMembers(a_yo, a_object, a_cascade);
				}
			}
		}

		private void deleteCollectionMembers(com.db4o.YapObject a_yo, object a_object, int
			 a_cascade)
		{
			if (a_object != null)
			{
				if (reflector().isCollection(reflector().forObject(a_object)))
				{
					writeUpdateDeleteMembers(a_yo.getID(), a_yo.getYapClass(), i_stream.i_handlers.arrayType
						(a_object), a_cascade);
				}
			}
		}

		internal virtual void dontDelete(int a_id, bool a_deleteMembers)
		{
			com.db4o.TreeIntObject tio = (com.db4o.TreeIntObject)com.db4o.TreeInt.find(i_delete
				, a_id);
			if (tio != null)
			{
				if (a_deleteMembers && tio.i_object != null)
				{
					object[] arr = (object[])tio.i_object;
					com.db4o.YapObject yo = (com.db4o.YapObject)arr[0];
					int cascade = ((int)arr[1]);
					deleteCollectionMembers(yo, yo.getObject(), cascade);
				}
				tio.i_object = null;
			}
			else
			{
				tio = new com.db4o.TreeIntObject(a_id, null);
				i_delete = com.db4o.Tree.add(i_delete, tio);
			}
		}

		internal virtual void dontRemoveFromClassIndex(int a_yapClassID, int a_id)
		{
			removeFromClassIndexTree(i_removeFromClassIndex, a_yapClassID, a_id);
			com.db4o.YapClass yapClass = i_stream.getYapClass(a_yapClassID);
			if (com.db4o.TreeInt.find(yapClass.getIndexRoot(), a_id) == null)
			{
				addToClassIndex(a_yapClassID, a_id);
			}
		}

		public virtual int ensureDb4oDatabase(com.db4o.ext.Db4oDatabase a_db)
		{
			com.db4o.ext.Db4oDatabase stored = (com.db4o.ext.Db4oDatabase)i_stream.db4oTypeStored
				(this, a_db);
			if (stored == null)
			{
				i_stream.showInternalClasses(true);
				i_stream.set3(this, a_db, 2, false);
				int newID = i_stream.getID1(this, a_db);
				i_stream.showInternalClasses(false);
				return newID;
			}
			if (stored == a_db)
			{
				return i_stream.getID1(this, a_db);
			}
			i_stream.showInternalClasses(true);
			int id = i_stream.getID1(this, stored);
			i_stream.bind(a_db, id);
			i_stream.showInternalClasses(false);
			return id;
		}

		internal virtual bool isDeleted(int a_id)
		{
			com.db4o.Slot slot = findSlotInHierarchy(a_id);
			if (slot != null)
			{
				return slot.i_address == 0;
			}
			return false;
		}

		private com.db4o.Slot findSlot(int a_id)
		{
			com.db4o.Tree tree = com.db4o.TreeInt.find(i_slots, a_id);
			if (tree != null)
			{
				return (com.db4o.Slot)((com.db4o.TreeIntObject)tree).i_object;
			}
			return null;
		}

		private com.db4o.Slot findSlotInHierarchy(int a_id)
		{
			com.db4o.Slot slot = findSlot(a_id);
			if (slot != null)
			{
				return slot;
			}
			if (i_parentTransaction != null)
			{
				return i_parentTransaction.findSlotInHierarchy(a_id);
			}
			return null;
		}

		private void freeOnBoth()
		{
			com.db4o.Iterator4 i = new com.db4o.Iterator4(i_freeOnBoth);
			while (i.hasNext())
			{
				com.db4o.Slot slot = (com.db4o.Slot)i.next();
				i_file.free(slot.i_address, slot.i_length);
			}
			i_freeOnBoth = null;
		}

		private void freeOnCommit()
		{
			freeOnBoth();
			if (i_freeOnCommit != null)
			{
				com.db4o.Iterator4 i = new com.db4o.Iterator4(i_freeOnCommit);
				while (i.hasNext())
				{
					int id = ((int)i.next());
					com.db4o.Tree node = com.db4o.TreeInt.find(i_stream.i_freeOnCommit, id);
					if (node != null)
					{
						com.db4o.Slot slot = (com.db4o.Slot)((com.db4o.TreeIntObject)node).i_object;
						i_file.free(slot.i_address, slot.i_length);
						slot.i_references--;
						bool removeNode = true;
						if (slot.i_references > 0)
						{
							com.db4o.Tree tio = com.db4o.TreeInt.find(i_freeOnRollback, id);
							if (tio != null)
							{
								com.db4o.Slot newSlot = (com.db4o.Slot)((com.db4o.TreeIntObject)tio).i_object;
								if (slot.i_address != newSlot.i_address)
								{
									slot.i_address = newSlot.i_address;
									slot.i_length = newSlot.i_length;
									removeNode = false;
								}
							}
						}
						if (removeNode)
						{
							i_stream.i_freeOnCommit = i_stream.i_freeOnCommit.removeNode(node);
						}
					}
				}
			}
			i_freeOnCommit = null;
		}

		internal virtual void freeOnCommit(int a_id, int a_address, int a_length)
		{
			if (a_id == 0)
			{
				return;
			}
			com.db4o.Tree isSecondWrite = com.db4o.TreeInt.find(i_freeOnRollback, a_id);
			if (isSecondWrite != null)
			{
				i_freeOnBoth = new com.db4o.List4(i_freeOnBoth, ((com.db4o.TreeIntObject)isSecondWrite
					).i_object);
				i_freeOnRollback = i_freeOnRollback.removeNode(isSecondWrite);
			}
			else
			{
				com.db4o.Tree node = com.db4o.TreeInt.find(i_stream.i_freeOnCommit, a_id);
				if (node != null)
				{
					com.db4o.Slot slot = (com.db4o.Slot)((com.db4o.TreeIntObject)node).i_object;
					slot.i_references++;
				}
				else
				{
					com.db4o.Slot slot = new com.db4o.Slot(a_address, a_length);
					slot.i_references = 1;
					i_stream.i_freeOnCommit = com.db4o.Tree.add(i_stream.i_freeOnCommit, new com.db4o.TreeIntObject
						(a_id, slot));
				}
				i_freeOnCommit = new com.db4o.List4(i_freeOnCommit, a_id);
			}
		}

		internal virtual void freeOnRollback(int a_id, int a_address, int a_length)
		{
			i_freeOnRollback = com.db4o.Tree.add(i_freeOnRollback, new com.db4o.TreeIntObject
				(a_id, new com.db4o.Slot(a_address, a_length)));
		}

		internal virtual void freePointer(int a_id)
		{
			freeOnCommit(a_id, a_id, com.db4o.YapConst.POINTER_LENGTH);
		}

		internal virtual void getSlotInformation(int a_id, int[] a_addressLength)
		{
			if (a_id != 0)
			{
				com.db4o.Slot slot = findSlot(a_id);
				if (slot != null)
				{
					a_addressLength[0] = slot.i_address;
					a_addressLength[1] = slot.i_length;
				}
				else
				{
					if (i_parentTransaction != null)
					{
						i_parentTransaction.getSlotInformation(a_id, a_addressLength);
						if (a_addressLength[0] != 0)
						{
							return;
						}
					}
					i_file.readBytes(i_bytes, a_id, com.db4o.YapConst.POINTER_LENGTH);
					a_addressLength[0] = (i_bytes[3] & 255) | (i_bytes[2] & 255) << 8 | (i_bytes[1] &
						 255) << 16 | i_bytes[0] << 24;
					a_addressLength[1] = (i_bytes[7] & 255) | (i_bytes[6] & 255) << 8 | (i_bytes[5] &
						 255) << 16 | i_bytes[4] << 24;
				}
			}
		}

		internal virtual object[] objectAndYapObjectBySignature(long a_uuid, byte[] a_signature
			)
		{
			object[] ret = new object[2];
			com.db4o.IxTree ixTree = (com.db4o.IxTree)i_stream.i_handlers.i_indexes.i_fieldUUID
				.getIndexRoot(this);
			com.db4o.IxTraverser ixTraverser = new com.db4o.IxTraverser();
			int count = ixTraverser.findBoundsExactMatch(a_uuid, ixTree);
			if (count > 0)
			{
				com.db4o.QCandidates candidates = new com.db4o.QCandidates(this, null, null);
				com.db4o.Tree tree = ixTraverser.getMatches(candidates);
				if (tree != null)
				{
					com.db4o.Transaction finalThis = this;
					tree.traverse(new _AnonymousInnerClass543(this, finalThis, a_signature, ret));
				}
			}
			return ret;
		}

		private sealed class _AnonymousInnerClass543 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass543(Transaction _enclosing, com.db4o.Transaction finalThis
				, byte[] a_signature, object[] ret)
			{
				this._enclosing = _enclosing;
				this.finalThis = finalThis;
				this.a_signature = a_signature;
				this.ret = ret;
			}

			public void visit(object a_object)
			{
				com.db4o.QCandidate candidate = (com.db4o.QCandidate)a_object;
				object[] arr = finalThis.i_stream.getObjectAndYapObjectByID(finalThis, candidate.
					i_key);
				if (arr[1] != null)
				{
					com.db4o.YapObject yod = (com.db4o.YapObject)arr[1];
					com.db4o.VirtualAttributes vad = yod.virtualAttributes(finalThis);
					byte[] cmp = vad.i_database.i_signature;
					bool same = true;
					if (a_signature.Length == cmp.Length)
					{
						for (int i = 0; i < a_signature.Length; i++)
						{
							if (a_signature[i] != cmp[i])
							{
								same = false;
								break;
							}
						}
					}
					else
					{
						same = false;
					}
					if (same)
					{
						ret[0] = arr[0];
						ret[1] = arr[1];
					}
				}
			}

			private readonly Transaction _enclosing;

			private readonly com.db4o.Transaction finalThis;

			private readonly byte[] a_signature;

			private readonly object[] ret;
		}

		internal virtual com.db4o.reflect.Reflector reflector()
		{
			return i_stream.reflector();
		}

		internal virtual void removeFromClassIndex(int a_yapClassID, int a_id)
		{
			removeFromClassIndexTree(i_addToClassIndex, a_yapClassID, a_id);
			i_removeFromClassIndex = addToClassIndexTree(i_removeFromClassIndex, a_yapClassID
				, a_id);
		}

		private void removeFromClassIndexTree(com.db4o.Tree a_tree, int a_yapClassID, int
			 a_id)
		{
			if (a_tree != null)
			{
				com.db4o.TreeIntObject node = (com.db4o.TreeIntObject)((com.db4o.TreeInt)a_tree).
					find(a_yapClassID);
				if (node != null)
				{
					node.i_object = com.db4o.Tree.removeLike((com.db4o.Tree)node.i_object, new com.db4o.TreeInt
						(a_id));
				}
			}
		}

		public virtual void rollback()
		{
			lock (i_stream.i_lock)
			{
				beginEndSet();
				if (i_dirtyFieldIndexes != null)
				{
					com.db4o.Iterator4 i = new com.db4o.Iterator4(i_dirtyFieldIndexes);
					while (i.hasNext())
					{
						((com.db4o.IxFieldTransaction)i.next()).rollback();
					}
				}
				if (i_freeOnCommit != null)
				{
					com.db4o.Iterator4 i = new com.db4o.Iterator4(i_freeOnCommit);
					while (i.hasNext())
					{
						com.db4o.Tree node = com.db4o.TreeInt.find(i_stream.i_freeOnCommit, ((int)i.next(
							)));
						if (node != null)
						{
							com.db4o.Slot slot = (com.db4o.Slot)((com.db4o.TreeIntObject)node).i_object;
							slot.i_references--;
							if (slot.i_references < 1)
							{
								i_stream.i_freeOnCommit = i_stream.i_freeOnCommit.removeNode(node);
							}
						}
					}
				}
				if (i_freeOnRollback != null)
				{
					i_freeOnRollback.traverse(new _AnonymousInnerClass639(this));
				}
				freeOnBoth();
				rollBackTransactionListeners();
				clearAll();
			}
		}

		private sealed class _AnonymousInnerClass639 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass639(Transaction _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void visit(object obj)
			{
				com.db4o.TreeIntObject node = (com.db4o.TreeIntObject)obj;
				com.db4o.Slot slot = (com.db4o.Slot)node.i_object;
				((com.db4o.YapFile)this._enclosing.i_stream).free(slot.i_address, slot.i_length);
			}

			private readonly Transaction _enclosing;
		}

		internal virtual void rollBackTransactionListeners()
		{
			if (i_transactionListeners != null)
			{
				com.db4o.Iterator4 i = new com.db4o.Iterator4(i_transactionListeners);
				while (i.hasNext())
				{
					((com.db4o.TransactionListener)i.next()).postRollback();
				}
				i_transactionListeners = null;
			}
		}

		internal virtual void setAddress(int a_address)
		{
			i_address = a_address;
		}

		internal virtual void setPointer(int a_id, int a_address, int a_length)
		{
			com.db4o.Slot slot = findSlot(a_id);
			if (slot != null)
			{
				slot.i_address = a_address;
				slot.i_length = a_length;
			}
			else
			{
				i_slots = com.db4o.Tree.add(i_slots, new com.db4o.TreeIntObject(a_id, new com.db4o.Slot
					(a_address, a_length)));
			}
		}

		internal virtual void traverseAddedClassIDs(int a_yapClassID, com.db4o.Visitor4 visitor
			)
		{
			traverseDeep(i_addToClassIndex, a_yapClassID, visitor);
		}

		internal virtual void traverseRemovedClassIDs(int a_yapClassID, com.db4o.Visitor4
			 visitor)
		{
			traverseDeep(i_removeFromClassIndex, a_yapClassID, visitor);
		}

		internal virtual void traverseDeep(com.db4o.Tree a_tree, int a_yapClassID, com.db4o.Visitor4
			 visitor)
		{
			if (a_tree != null)
			{
				com.db4o.TreeIntObject node = (com.db4o.TreeIntObject)((com.db4o.TreeInt)a_tree).
					find(a_yapClassID);
				if (node != null && node.i_object != null)
				{
					((com.db4o.Tree)node.i_object).traverse(visitor);
				}
			}
		}

		private void write()
		{
			if (!(i_slots == null && i_addToClassIndex == null && i_removeFromClassIndex == null
				))
			{
				int length = calculateLength();
				int address = ((com.db4o.YapFile)i_stream).getSlot(length);
				freeOnCommit(address, address, length);
				com.db4o.YapWriter bytes = new com.db4o.YapWriter(this, address, length);
				bytes.writeInt(length);
				com.db4o.Tree.write(bytes, i_slots);
				com.db4o.Tree.write(bytes, i_addToClassIndex);
				com.db4o.Tree.write(bytes, i_removeFromClassIndex);
				bytes.write();
				i_stream.writeTransactionPointer(address);
				writeSlots();
				i_stream.writeTransactionPointer(0);
			}
		}

		private void traverseYapClassEntries(com.db4o.Tree a_tree, bool a_add, com.db4o.Collection4
			 a_indices)
		{
			if (a_tree != null)
			{
				a_tree.traverse(new _AnonymousInnerClass739(this, a_add, a_indices));
			}
		}

		private sealed class _AnonymousInnerClass739 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass739(Transaction _enclosing, bool a_add, com.db4o.Collection4
				 a_indices)
			{
				this._enclosing = _enclosing;
				this.a_add = a_add;
				this.a_indices = a_indices;
			}

			public void visit(object obj)
			{
				com.db4o.TreeIntObject node = (com.db4o.TreeIntObject)obj;
				com.db4o.YapClass yapClass = this._enclosing.i_stream.getYapClass(node.i_key);
				com.db4o.ClassIndex classIndex = yapClass.getIndex();
				if (node.i_object != null)
				{
					com.db4o.Visitor4 visitor = null;
					if (a_add)
					{
						visitor = new _AnonymousInnerClass748(this, classIndex);
					}
					else
					{
						visitor = new _AnonymousInnerClass755(this, classIndex);
					}
					((com.db4o.Tree)node.i_object).traverse(visitor);
					if (!a_indices.containsByIdentity(classIndex))
					{
						a_indices.add(classIndex);
					}
				}
			}

			private sealed class _AnonymousInnerClass748 : com.db4o.Visitor4
			{
				public _AnonymousInnerClass748(_AnonymousInnerClass739 _enclosing, com.db4o.ClassIndex
					 classIndex)
				{
					this._enclosing = _enclosing;
					this.classIndex = classIndex;
				}

				public void visit(object a_object)
				{
					classIndex.add(((com.db4o.TreeInt)a_object).i_key);
				}

				private readonly _AnonymousInnerClass739 _enclosing;

				private readonly com.db4o.ClassIndex classIndex;
			}

			private sealed class _AnonymousInnerClass755 : com.db4o.Visitor4
			{
				public _AnonymousInnerClass755(_AnonymousInnerClass739 _enclosing, com.db4o.ClassIndex
					 classIndex)
				{
					this._enclosing = _enclosing;
					this.classIndex = classIndex;
				}

				public void visit(object a_object)
				{
					int id = ((com.db4o.TreeInt)a_object).i_key;
					com.db4o.YapObject yo = this._enclosing._enclosing.i_stream.getYapObject(id);
					if (yo != null)
					{
						this._enclosing._enclosing.i_stream.yapObjectGCd(yo);
					}
					classIndex.remove(id);
				}

				private readonly _AnonymousInnerClass739 _enclosing;

				private readonly com.db4o.ClassIndex classIndex;
			}

			private readonly Transaction _enclosing;

			private readonly bool a_add;

			private readonly com.db4o.Collection4 a_indices;
		}

		private void writeSlots()
		{
			com.db4o.Collection4 indicesToBeWritten = new com.db4o.Collection4();
			traverseYapClassEntries(i_addToClassIndex, true, indicesToBeWritten);
			traverseYapClassEntries(i_removeFromClassIndex, false, indicesToBeWritten);
			com.db4o.Iterator4 i = indicesToBeWritten.iterator();
			while (i.hasNext())
			{
				com.db4o.ClassIndex classIndex = (com.db4o.ClassIndex)i.next();
				classIndex.setDirty(i_stream);
				classIndex.write(i_stream, this);
			}
			if (i_slots != null)
			{
				i_slots.traverse(new _AnonymousInnerClass794(this));
			}
		}

		private sealed class _AnonymousInnerClass794 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass794(Transaction _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void visit(object obj)
			{
				com.db4o.TreeIntObject node = (com.db4o.TreeIntObject)obj;
				com.db4o.Slot slot = (com.db4o.Slot)node.i_object;
				this._enclosing.writePointer(node.i_key, slot.i_address, slot.i_length);
			}

			private readonly Transaction _enclosing;
		}

		internal virtual void writeOld()
		{
			lock (i_stream.i_lock)
			{
				i_pointerIo.useSlot(i_address);
				i_pointerIo.read();
				int length = i_pointerIo.readInt();
				if (length > 0)
				{
					com.db4o.YapWriter bytes = new com.db4o.YapWriter(this, i_address, length);
					bytes.read();
					bytes.incrementOffset(com.db4o.YapConst.YAPINT_LENGTH);
					i_slots = new com.db4o.TreeReader(bytes, new com.db4o.TreeIntObject(0, new com.db4o.Slot
						(0, 0))).read();
					i_addToClassIndex = new com.db4o.TreeReader(bytes, new com.db4o.TreeIntObject(0, 
						new com.db4o.TreeInt(0))).read();
					i_removeFromClassIndex = new com.db4o.TreeReader(bytes, new com.db4o.TreeIntObject
						(0, new com.db4o.TreeInt(0))).read();
					writeSlots();
					i_stream.writeTransactionPointer(0);
					freeOnCommit();
				}
				else
				{
					i_stream.writeTransactionPointer(0);
				}
			}
		}

		internal virtual void writePointer(int a_id, int a_address, int a_length)
		{
			i_pointerIo.useSlot(a_id);
			i_pointerIo.writeInt(a_address);
			i_pointerIo.writeInt(a_length);
			if (com.db4o.Deploy.debug && com.db4o.Deploy.overwrite)
			{
				i_pointerIo.setID(com.db4o.YapConst.IGNORE_ID);
			}
			i_pointerIo.write();
		}

		internal virtual void writeUpdateDeleteMembers(int a_id, com.db4o.YapClass a_yc, 
			int a_type, int a_cascade)
		{
			if (com.db4o.Tree.find(i_writtenUpdateDeletedMembers, new com.db4o.TreeInt(a_id))
				 == null)
			{
				i_writtenUpdateDeletedMembers = com.db4o.Tree.add(i_writtenUpdateDeletedMembers, 
					new com.db4o.TreeInt(a_id));
				com.db4o.YapWriter objectBytes = i_stream.readWriterByID(this, a_id);
				if (objectBytes != null)
				{
					a_yc.readObjectHeader(objectBytes, a_id);
				}
				else
				{
					if (a_yc.hasIndex())
					{
						dontRemoveFromClassIndex(a_yc.getID(), a_id);
					}
				}
				if (objectBytes != null)
				{
					objectBytes.setCascadeDeletes(a_cascade);
					a_yc.deleteMembers(objectBytes, a_type);
					freeOnCommit(a_id, objectBytes.getAddress(), objectBytes.getLength());
				}
			}
		}

		public override string ToString()
		{
			return i_stream.ToString();
		}
	}
}
