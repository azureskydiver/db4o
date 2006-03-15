namespace com.db4o
{
	/// <exclude></exclude>
	public class Transaction
	{
		private com.db4o.Tree _slotChanges;

		private int i_address;

		private com.db4o.Tree i_addToClassIndex;

		private byte[] i_bytes = new byte[com.db4o.YapConst.POINTER_LENGTH];

		public com.db4o.Tree i_delete;

		private com.db4o.foundation.List4 i_dirtyFieldIndexes;

		public readonly com.db4o.YapFile i_file;

		internal readonly com.db4o.Transaction i_parentTransaction;

		private readonly com.db4o.YapWriter i_pointerIo;

		private com.db4o.Tree i_removeFromClassIndex;

		public readonly com.db4o.YapStream i_stream;

		private com.db4o.foundation.List4 i_transactionListeners;

		protected com.db4o.Tree i_writtenUpdateDeletedMembers;

		internal Transaction(com.db4o.YapStream a_stream, com.db4o.Transaction a_parent)
		{
			i_stream = a_stream;
			i_file = (a_stream is com.db4o.YapFile) ? (com.db4o.YapFile)a_stream : null;
			i_parentTransaction = a_parent;
			i_pointerIo = new com.db4o.YapWriter(this, com.db4o.YapConst.POINTER_LENGTH);
		}

		public virtual void addDirtyFieldIndex(com.db4o.inside.ix.IndexTransaction a_xft)
		{
			i_dirtyFieldIndexes = new com.db4o.foundation.List4(i_dirtyFieldIndexes, a_xft);
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

		public virtual void addTransactionListener(com.db4o.TransactionListener a_listener
			)
		{
			i_transactionListeners = new com.db4o.foundation.List4(i_transactionListeners, a_listener
				);
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
					delete.traverse(new _AnonymousInnerClass99(this, foundOne, finalThis));
				}
				while (foundOne[0]);
			}
			i_delete = null;
			i_writtenUpdateDeletedMembers = null;
		}

		private sealed class _AnonymousInnerClass99 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass99(Transaction _enclosing, bool[] foundOne, com.db4o.Transaction
				 finalThis)
			{
				this._enclosing = _enclosing;
				this.foundOne = foundOne;
				this.finalThis = finalThis;
			}

			public void visit(object a_object)
			{
				com.db4o.DeleteInfo info = (com.db4o.DeleteInfo)a_object;
				if (info._delete)
				{
					foundOne[0] = true;
					object obj = null;
					if (info._reference != null)
					{
						obj = info._reference.getObject();
					}
					if (obj == null)
					{
						object[] arr = finalThis.i_stream.getObjectAndYapObjectByID(finalThis, info.i_key
							);
						obj = arr[0];
						info._reference = (com.db4o.YapObject)arr[1];
					}
					this._enclosing.i_stream.delete4(finalThis, info._reference, obj, info._cascade, 
						false);
				}
				this._enclosing.i_delete = com.db4o.Tree.add(this._enclosing.i_delete, new com.db4o.DeleteInfo
					(info.i_key, null, false, info._cascade));
			}

			private readonly Transaction _enclosing;

			private readonly bool[] foundOne;

			private readonly com.db4o.Transaction finalThis;
		}

		private void clearAll()
		{
			_slotChanges = null;
			i_addToClassIndex = null;
			i_removeFromClassIndex = null;
			i_dirtyFieldIndexes = null;
			i_transactionListeners = null;
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

		internal virtual void commit()
		{
			lock (i_stream.i_lock)
			{
				i_file.freeSpaceBeginCommit();
				commitExceptForFreespace();
				i_file.freeSpaceEndCommit();
			}
		}

		private void commitExceptForFreespace()
		{
			commit1BeginEndSet();
			commit2Listeners();
			commit3Stream();
			commit4FieldIndexes();
			commit5writeClassIndexChanges();
			i_stream.writeDirty();
			commit6WriteChanges();
			freeOnCommit();
			commit7ClearAll();
		}

		private void commit1BeginEndSet()
		{
			if (i_parentTransaction != null)
			{
				i_parentTransaction.commit1BeginEndSet();
			}
			beginEndSet();
		}

		private void commit2Listeners()
		{
			if (i_parentTransaction != null)
			{
				i_parentTransaction.commit2Listeners();
			}
			commitTransactionListeners();
		}

		private void commit3Stream()
		{
			i_stream.checkNeededUpdates();
			i_stream.writeDirty();
			i_stream.i_classCollection.write(i_stream.getSystemTransaction());
		}

		private void commit4FieldIndexes()
		{
			if (i_parentTransaction != null)
			{
				i_parentTransaction.commit4FieldIndexes();
			}
			if (i_dirtyFieldIndexes != null)
			{
				com.db4o.foundation.Iterator4 i = new com.db4o.foundation.Iterator4Impl(i_dirtyFieldIndexes
					);
				while (i.hasNext())
				{
					((com.db4o.inside.ix.IndexTransaction)i.next()).commit();
				}
			}
		}

		private void commit5writeClassIndexChanges()
		{
			if (i_parentTransaction != null)
			{
				i_parentTransaction.commit5writeClassIndexChanges();
			}
			com.db4o.foundation.Collection4 indicesToBeWritten = new com.db4o.foundation.Collection4
				();
			traverseYapClassEntries(i_addToClassIndex, true, indicesToBeWritten);
			traverseYapClassEntries(i_removeFromClassIndex, false, indicesToBeWritten);
			if (indicesToBeWritten.size() > 0)
			{
				com.db4o.foundation.Iterator4 i = indicesToBeWritten.iterator();
				while (i.hasNext())
				{
					com.db4o.ClassIndex classIndex = (com.db4o.ClassIndex)i.next();
					classIndex.setStateDirty();
					classIndex.write(this);
				}
			}
		}

		private void commit6WriteChanges()
		{
			if (i_parentTransaction != null)
			{
				i_parentTransaction.commit6WriteChanges();
			}
			int[] slotSetPointerCount = { 0 };
			if (_slotChanges != null)
			{
				_slotChanges.traverse(new _AnonymousInnerClass264(this, slotSetPointerCount));
			}
			if (slotSetPointerCount[0] > 0)
			{
				int length = (((slotSetPointerCount[0] * 3) + 2) * com.db4o.YapConst.YAPINT_LENGTH
					);
				int address = i_file.getSlot(length);
				com.db4o.YapWriter bytes = new com.db4o.YapWriter(this, address, length);
				bytes.writeInt(length);
				com.db4o.Tree.write(bytes, _slotChanges, slotSetPointerCount[0]);
				bytes.write();
				flushFile();
				i_stream.writeTransactionPointer(address);
				flushFile();
				writeSlots();
				i_stream.writeTransactionPointer(0);
				flushFile();
				i_file.free(address, length);
			}
		}

		private sealed class _AnonymousInnerClass264 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass264(Transaction _enclosing, int[] slotSetPointerCount)
			{
				this._enclosing = _enclosing;
				this.slotSetPointerCount = slotSetPointerCount;
			}

			public void visit(object obj)
			{
				com.db4o.inside.slots.SlotChange slot = (com.db4o.inside.slots.SlotChange)obj;
				if (slot.isSetPointer())
				{
					slotSetPointerCount[0]++;
				}
			}

			private readonly Transaction _enclosing;

			private readonly int[] slotSetPointerCount;
		}

		private void commit7ClearAll()
		{
			if (i_parentTransaction != null)
			{
				i_parentTransaction.commit7ClearAll();
			}
			clearAll();
		}

		internal virtual void commitTransactionListeners()
		{
			if (i_transactionListeners != null)
			{
				com.db4o.foundation.Iterator4 i = new com.db4o.foundation.Iterator4Impl(i_transactionListeners
					);
				while (i.hasNext())
				{
					((com.db4o.TransactionListener)i.next()).preCommit();
				}
				i_transactionListeners = null;
			}
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

		internal virtual void delete(com.db4o.YapObject a_yo, int a_cascade)
		{
			int id = a_yo.getID();
			com.db4o.DeleteInfo info = (com.db4o.DeleteInfo)com.db4o.TreeInt.find(i_delete, id
				);
			if (info == null)
			{
				info = new com.db4o.DeleteInfo(id, a_yo, true, a_cascade);
				i_delete = com.db4o.Tree.add(i_delete, info);
				return;
			}
			info._reference = a_yo;
			if (a_cascade > info._cascade)
			{
				info._cascade = a_cascade;
			}
		}

		internal virtual void dontDelete(int classID, int a_id)
		{
			com.db4o.DeleteInfo info = (com.db4o.DeleteInfo)com.db4o.TreeInt.find(i_delete, a_id
				);
			if (info == null)
			{
				i_delete = com.db4o.Tree.add(i_delete, new com.db4o.DeleteInfo(a_id, null, false, 
					0));
			}
			else
			{
				info._delete = false;
			}
			com.db4o.YapClass yc = i_stream.getYapClass(classID);
			dontDeleteAllAncestors(yc, a_id);
		}

		internal virtual void dontDeleteAllAncestors(com.db4o.YapClass yapClass, int objectID
			)
		{
			if (yapClass == null)
			{
				return;
			}
			removeFromClassIndexTree(i_removeFromClassIndex, yapClass.getID(), objectID);
			dontDeleteAllAncestors(yapClass.i_ancestor, objectID);
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

		private com.db4o.inside.slots.SlotChange findSlotChange(int a_id)
		{
			return (com.db4o.inside.slots.SlotChange)com.db4o.TreeInt.find(_slotChanges, a_id
				);
		}

		private void flushFile()
		{
			if (i_file.i_config._flushFileBuffers)
			{
				i_file.syncFiles();
			}
		}

		private void freeOnCommit()
		{
			if (i_parentTransaction != null)
			{
				i_parentTransaction.freeOnCommit();
			}
			if (_slotChanges != null)
			{
				_slotChanges.traverse(new _AnonymousInnerClass420(this));
			}
		}

		private sealed class _AnonymousInnerClass420 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass420(Transaction _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void visit(object obj)
			{
				((com.db4o.inside.slots.SlotChange)obj).freeDuringCommit(this._enclosing.i_file);
			}

			private readonly Transaction _enclosing;
		}

		internal virtual com.db4o.inside.slots.Slot getSlotInformation(int a_id)
		{
			if (a_id == 0)
			{
				return null;
			}
			com.db4o.inside.slots.SlotChange change = findSlotChange(a_id);
			if (change != null)
			{
				if (change.isSetPointer())
				{
					return change.newSlot();
				}
			}
			if (i_parentTransaction != null)
			{
				com.db4o.inside.slots.Slot parentSlot = i_parentTransaction.getSlotInformation(a_id
					);
				if (parentSlot != null)
				{
					return parentSlot;
				}
			}
			i_file.readBytes(i_bytes, a_id, com.db4o.YapConst.POINTER_LENGTH);
			int address = (i_bytes[3] & 255) | (i_bytes[2] & 255) << 8 | (i_bytes[1] & 255) <<
				 16 | i_bytes[0] << 24;
			int length = (i_bytes[7] & 255) | (i_bytes[6] & 255) << 8 | (i_bytes[5] & 255) <<
				 16 | i_bytes[4] << 24;
			return new com.db4o.inside.slots.Slot(address, length);
		}

		internal virtual bool isDeleted(int a_id)
		{
			com.db4o.inside.slots.SlotChange slot = findSlotChange(a_id);
			if (slot != null)
			{
				return slot.isDeleted();
			}
			if (i_parentTransaction != null)
			{
				return i_parentTransaction.isDeleted(a_id);
			}
			return false;
		}

		internal virtual object[] objectAndYapObjectBySignature(long a_uuid, byte[] a_signature
			)
		{
			object[] ret = new object[2];
			com.db4o.inside.ix.IxTree ixTree = (com.db4o.inside.ix.IxTree)i_stream.i_handlers
				.i_indexes.i_fieldUUID.getIndexRoot(this);
			com.db4o.inside.ix.IxTraverser ixTraverser = new com.db4o.inside.ix.IxTraverser();
			int count = ixTraverser.findBoundsExactMatch(a_uuid, ixTree);
			if (count > 0)
			{
				com.db4o.Transaction finalThis = this;
				ixTraverser.visitAll(new _AnonymousInnerClass491(this, finalThis, a_signature, ret
					));
			}
			return ret;
		}

		private sealed class _AnonymousInnerClass491 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass491(Transaction _enclosing, com.db4o.Transaction finalThis
				, byte[] a_signature, object[] ret)
			{
				this._enclosing = _enclosing;
				this.finalThis = finalThis;
				this.a_signature = a_signature;
				this.ret = ret;
			}

			public void visit(object a_object)
			{
				object[] arr = finalThis.i_stream.getObjectAndYapObjectByID(finalThis, ((int)a_object
					));
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

		private com.db4o.inside.slots.SlotChange produceSlotChange(int id)
		{
			com.db4o.inside.slots.SlotChange slot = new com.db4o.inside.slots.SlotChange(id);
			_slotChanges = com.db4o.Tree.add(_slotChanges, slot);
			return (com.db4o.inside.slots.SlotChange)slot.duplicateOrThis();
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
					com.db4o.foundation.Iterator4 i = new com.db4o.foundation.Iterator4Impl(i_dirtyFieldIndexes
						);
					while (i.hasNext())
					{
						((com.db4o.inside.ix.IndexTransaction)i.next()).rollback();
					}
				}
				if (_slotChanges != null)
				{
					_slotChanges.traverse(new _AnonymousInnerClass576(this));
				}
				rollBackTransactionListeners();
				clearAll();
			}
		}

		private sealed class _AnonymousInnerClass576 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass576(Transaction _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void visit(object a_object)
			{
				((com.db4o.inside.slots.SlotChange)a_object).rollback(this._enclosing.i_file);
			}

			private readonly Transaction _enclosing;
		}

		internal virtual void rollBackTransactionListeners()
		{
			if (i_transactionListeners != null)
			{
				com.db4o.foundation.Iterator4 i = new com.db4o.foundation.Iterator4Impl(i_transactionListeners
					);
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
			produceSlotChange(a_id).setPointer(a_address, a_length);
		}

		internal virtual void slotDelete(int a_id, int a_address, int a_length)
		{
			if (a_id == 0)
			{
				return;
			}
			com.db4o.inside.slots.SlotChange slot = produceSlotChange(a_id);
			slot.freeOnCommit(i_file, new com.db4o.inside.slots.Slot(a_address, a_length));
			slot.setPointer(0, 0);
		}

		internal virtual void slotFreeOnCommit(int a_id, int a_address, int a_length)
		{
			if (a_id == 0)
			{
				return;
			}
			produceSlotChange(a_id).freeOnCommit(i_file, new com.db4o.inside.slots.Slot(a_address
				, a_length));
		}

		internal virtual void slotFreeOnRollback(int a_id, int a_address, int a_length)
		{
			produceSlotChange(a_id).freeOnRollback(a_address, a_length);
		}

		internal virtual void slotFreeOnRollbackCommitSetPointer(int a_id, int newAddress
			, int newLength)
		{
			com.db4o.inside.slots.Slot slot = getSlotInformation(a_id);
			com.db4o.inside.slots.SlotChange change = produceSlotChange(a_id);
			change.freeOnRollbackSetPointer(newAddress, newLength);
			change.freeOnCommit(i_file, slot);
		}

		internal virtual void slotFreeOnRollbackSetPointer(int a_id, int a_address, int a_length
			)
		{
			produceSlotChange(a_id).freeOnRollbackSetPointer(a_address, a_length);
		}

		internal virtual void slotFreePointerOnCommit(int a_id, int a_address, int a_length
			)
		{
			slotFreeOnCommit(a_address, a_address, a_length);
			slotFreeOnCommit(a_id, a_id, com.db4o.YapConst.POINTER_LENGTH);
		}

		internal virtual bool supportsVirtualFields()
		{
			return true;
		}

		public override string ToString()
		{
			return i_stream.ToString();
		}

		internal virtual void traverseAddedClassIDs(int a_yapClassID, com.db4o.foundation.Visitor4
			 visitor)
		{
			traverseDeep(i_addToClassIndex, a_yapClassID, visitor);
		}

		internal virtual void traverseDeep(com.db4o.Tree a_tree, int a_yapClassID, com.db4o.foundation.Visitor4
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

		internal virtual void traverseRemovedClassIDs(int a_yapClassID, com.db4o.foundation.Visitor4
			 visitor)
		{
			traverseDeep(i_removeFromClassIndex, a_yapClassID, visitor);
		}

		private void traverseYapClassEntries(com.db4o.Tree a_tree, bool a_add, com.db4o.foundation.Collection4
			 a_indices)
		{
			if (a_tree != null)
			{
				a_tree.traverse(new _AnonymousInnerClass732(this, a_add, a_indices));
			}
		}

		private sealed class _AnonymousInnerClass732 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass732(Transaction _enclosing, bool a_add, com.db4o.foundation.Collection4
				 a_indices)
			{
				this._enclosing = _enclosing;
				this.a_add = a_add;
				this.a_indices = a_indices;
			}

			public void visit(object obj)
			{
				com.db4o.TreeIntObject node = (com.db4o.TreeIntObject)obj;
				com.db4o.YapClass yapClass = this._enclosing.i_stream.i_classCollection.getYapClass
					(node.i_key);
				com.db4o.ClassIndex classIndex = yapClass.getIndex();
				if (node.i_object != null)
				{
					com.db4o.foundation.Visitor4 visitor = null;
					if (a_add)
					{
						visitor = new _AnonymousInnerClass741(this, classIndex);
					}
					else
					{
						visitor = new _AnonymousInnerClass747(this, classIndex);
					}
					((com.db4o.Tree)node.i_object).traverse(visitor);
					if (!a_indices.containsByIdentity(classIndex))
					{
						a_indices.add(classIndex);
					}
				}
			}

			private sealed class _AnonymousInnerClass741 : com.db4o.foundation.Visitor4
			{
				public _AnonymousInnerClass741(_AnonymousInnerClass732 _enclosing, com.db4o.ClassIndex
					 classIndex)
				{
					this._enclosing = _enclosing;
					this.classIndex = classIndex;
				}

				public void visit(object a_object)
				{
					classIndex.add(((com.db4o.TreeInt)a_object).i_key);
				}

				private readonly _AnonymousInnerClass732 _enclosing;

				private readonly com.db4o.ClassIndex classIndex;
			}

			private sealed class _AnonymousInnerClass747 : com.db4o.foundation.Visitor4
			{
				public _AnonymousInnerClass747(_AnonymousInnerClass732 _enclosing, com.db4o.ClassIndex
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

				private readonly _AnonymousInnerClass732 _enclosing;

				private readonly com.db4o.ClassIndex classIndex;
			}

			private readonly Transaction _enclosing;

			private readonly bool a_add;

			private readonly com.db4o.foundation.Collection4 a_indices;
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
					_slotChanges = new com.db4o.TreeReader(bytes, new com.db4o.inside.slots.SlotChange
						(0)).read();
					writeSlots();
					i_stream.writeTransactionPointer(0);
					flushFile();
					freeOnCommit();
				}
				else
				{
					i_stream.writeTransactionPointer(0);
					flushFile();
				}
			}
		}

		public virtual void writePointer(int a_id, int a_address, int a_length)
		{
			i_pointerIo.useSlot(a_id);
			i_pointerIo.writeInt(a_address);
			i_pointerIo.writeInt(a_length);
			if (com.db4o.Debug.xbytes && com.db4o.Deploy.overwrite)
			{
				i_pointerIo.setID(com.db4o.YapConst.IGNORE_ID);
			}
			i_pointerIo.write();
		}

		private void writeSlots()
		{
			if (_slotChanges != null)
			{
				_slotChanges.traverse(new _AnonymousInnerClass823(this));
				flushFile();
			}
		}

		private sealed class _AnonymousInnerClass823 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass823(Transaction _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void visit(object a_object)
			{
				((com.db4o.inside.slots.SlotChange)a_object).writePointer(this._enclosing);
			}

			private readonly Transaction _enclosing;
		}

		internal virtual void writeUpdateDeleteMembers(int a_id, com.db4o.YapClass a_yc, 
			int a_type, int a_cascade)
		{
			if (com.db4o.Tree.find(i_writtenUpdateDeletedMembers, new com.db4o.TreeInt(a_id))
				 != null)
			{
				return;
			}
			i_writtenUpdateDeletedMembers = com.db4o.Tree.add(i_writtenUpdateDeletedMembers, 
				new com.db4o.TreeInt(a_id));
			com.db4o.YapWriter objectBytes = i_stream.readWriterByID(this, a_id);
			if (objectBytes == null)
			{
				if (a_yc.hasIndex())
				{
					dontRemoveFromClassIndex(a_yc.getID(), a_id);
				}
				return;
			}
			a_yc.readObjectHeader(objectBytes, a_id);
			com.db4o.DeleteInfo info = (com.db4o.DeleteInfo)com.db4o.TreeInt.find(i_delete, a_id
				);
			if (info != null)
			{
				if (info._cascade > a_cascade)
				{
					a_cascade = info._cascade;
				}
			}
			objectBytes.setCascadeDeletes(a_cascade);
			a_yc.deleteMembers(objectBytes, a_type, true);
			slotFreeOnCommit(a_id, objectBytes.getAddress(), objectBytes.getLength());
		}
	}
}
