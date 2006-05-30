namespace com.db4o
{
	/// <exclude></exclude>
	public class Transaction
	{
		private com.db4o.Tree _slotChanges;

		private int i_address;

		private com.db4o.Tree i_addToClassIndex;

		private readonly byte[] _pointerBuffer = new byte[com.db4o.YapConst.POINTER_LENGTH
			];

		public com.db4o.Tree i_delete;

		private com.db4o.foundation.List4 i_dirtyFieldIndexes;

		private com.db4o.Tree _dirtyBTrees;

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

		public virtual void AddDirtyFieldIndex(com.db4o.inside.ix.IndexTransaction a_xft)
		{
			i_dirtyFieldIndexes = new com.db4o.foundation.List4(i_dirtyFieldIndexes, a_xft);
		}

		internal virtual void AddToClassIndex(int a_yapClassID, int a_id)
		{
		}

		private com.db4o.Tree AddToClassIndexTree(com.db4o.Tree a_tree, int a_yapClassID, 
			int a_id)
		{
			com.db4o.TreeIntObject[] node = new com.db4o.TreeIntObject[] { new com.db4o.TreeIntObject
				(a_yapClassID) };
			a_tree = CreateClassIndexNode(a_tree, node);
			node[0]._object = com.db4o.Tree.Add((com.db4o.Tree)node[0]._object, new com.db4o.TreeInt
				(a_id));
			return a_tree;
		}

		public virtual void AddTransactionListener(com.db4o.TransactionListener a_listener
			)
		{
			i_transactionListeners = new com.db4o.foundation.List4(i_transactionListeners, a_listener
				);
		}

		private void AppendSlotChanges(com.db4o.YapWriter writer)
		{
			if (i_parentTransaction != null)
			{
				i_parentTransaction.AppendSlotChanges(writer);
			}
			com.db4o.Tree.Traverse(_slotChanges, new _AnonymousInnerClass100(this, writer));
		}

		private sealed class _AnonymousInnerClass100 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass100(Transaction _enclosing, com.db4o.YapWriter writer)
			{
				this._enclosing = _enclosing;
				this.writer = writer;
			}

			public void Visit(object obj)
			{
				((com.db4o.Tree)obj).Write(writer);
			}

			private readonly Transaction _enclosing;

			private readonly com.db4o.YapWriter writer;
		}

		internal virtual void BeginEndSet()
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
					delete.Traverse(new _AnonymousInnerClass119(this, foundOne, finalThis));
				}
				while (foundOne[0]);
			}
			i_delete = null;
			i_writtenUpdateDeletedMembers = null;
		}

		private sealed class _AnonymousInnerClass119 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass119(Transaction _enclosing, bool[] foundOne, com.db4o.Transaction
				 finalThis)
			{
				this._enclosing = _enclosing;
				this.foundOne = foundOne;
				this.finalThis = finalThis;
			}

			public void Visit(object a_object)
			{
				com.db4o.DeleteInfo info = (com.db4o.DeleteInfo)a_object;
				if (info._delete)
				{
					foundOne[0] = true;
					object obj = null;
					if (info._reference != null)
					{
						obj = info._reference.GetObject();
					}
					if (obj == null)
					{
						object[] arr = finalThis.i_stream.GetObjectAndYapObjectByID(finalThis, info._key);
						obj = arr[0];
						info._reference = (com.db4o.YapObject)arr[1];
					}
					this._enclosing.i_stream.Delete4(finalThis, info._reference, obj, info._cascade, 
						false);
				}
				this._enclosing.i_delete = com.db4o.Tree.Add(this._enclosing.i_delete, new com.db4o.DeleteInfo
					(info._key, null, false, info._cascade));
			}

			private readonly Transaction _enclosing;

			private readonly bool[] foundOne;

			private readonly com.db4o.Transaction finalThis;
		}

		private void ClearAll()
		{
			_slotChanges = null;
			_dirtyBTrees = null;
			i_addToClassIndex = null;
			i_removeFromClassIndex = null;
			i_dirtyFieldIndexes = null;
			i_transactionListeners = null;
		}

		internal virtual void Close(bool a_rollbackOnClose)
		{
			try
			{
				if (i_stream != null)
				{
					i_stream.ReleaseSemaphores(this);
				}
			}
			catch (System.Exception e)
			{
			}
			if (a_rollbackOnClose)
			{
				try
				{
					Rollback();
				}
				catch (System.Exception e)
				{
				}
			}
		}

		internal virtual void Commit()
		{
			lock (i_stream.i_lock)
			{
				i_file.FreeSpaceBeginCommit();
				CommitExceptForFreespace();
				i_file.FreeSpaceEndCommit();
			}
		}

		private void CommitExceptForFreespace()
		{
			Commit1BeginEndSet();
			Commit2Listeners();
			Commit3Stream();
			Commit4FieldIndexes();
			Commit5writeClassIndexChanges();
			i_stream.WriteDirty();
			Commit6WriteChanges();
			FreeOnCommit();
			Commit7ClearAll();
		}

		private void Commit1BeginEndSet()
		{
			if (i_parentTransaction != null)
			{
				i_parentTransaction.Commit1BeginEndSet();
			}
			BeginEndSet();
		}

		private void Commit2Listeners()
		{
			if (i_parentTransaction != null)
			{
				i_parentTransaction.Commit2Listeners();
			}
			CommitTransactionListeners();
		}

		private void Commit3Stream()
		{
			i_stream.CheckNeededUpdates();
			i_stream.WriteDirty();
			i_stream.i_classCollection.Write(i_stream.GetSystemTransaction());
		}

		private void Commit4FieldIndexes()
		{
			if (i_parentTransaction != null)
			{
				i_parentTransaction.Commit4FieldIndexes();
			}
			if (i_dirtyFieldIndexes != null)
			{
				com.db4o.foundation.Iterator4 i = new com.db4o.foundation.Iterator4Impl(i_dirtyFieldIndexes
					);
				while (i.HasNext())
				{
					((com.db4o.inside.ix.IndexTransaction)i.Next()).Commit();
				}
			}
		}

		private void Commit5writeClassIndexChanges()
		{
			if (i_parentTransaction != null)
			{
				i_parentTransaction.Commit5writeClassIndexChanges();
			}
			if (_dirtyBTrees != null)
			{
				_dirtyBTrees.Traverse(new _AnonymousInnerClass276(this));
			}
		}

		private sealed class _AnonymousInnerClass276 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass276(Transaction _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object obj)
			{
				com.db4o.inside.btree.BTree btree = (com.db4o.inside.btree.BTree)((com.db4o.TreeIntObject
					)obj)._object;
				btree.Commit(this._enclosing);
			}

			private readonly Transaction _enclosing;
		}

		private void Commit6WriteChanges()
		{
			int slotSetPointerCount = CountSlotChanges();
			if (slotSetPointerCount > 0)
			{
				int length = (((slotSetPointerCount * 3) + 2) * com.db4o.YapConst.YAPINT_LENGTH);
				int address = i_file.GetSlot(length);
				com.db4o.YapWriter bytes = new com.db4o.YapWriter(this, address, length);
				bytes.WriteInt(length);
				bytes.WriteInt(slotSetPointerCount);
				AppendSlotChanges(bytes);
				bytes.Write();
				FlushFile();
				i_stream.WriteTransactionPointer(address);
				FlushFile();
				if (WriteSlots())
				{
					FlushFile();
				}
				i_stream.WriteTransactionPointer(0);
				FlushFile();
				i_file.Free(address, length);
			}
		}

		private void Commit7ClearAll()
		{
			if (i_parentTransaction != null)
			{
				i_parentTransaction.Commit7ClearAll();
			}
			ClearAll();
		}

		internal virtual void CommitTransactionListeners()
		{
			if (i_transactionListeners != null)
			{
				com.db4o.foundation.Iterator4 i = new com.db4o.foundation.Iterator4Impl(i_transactionListeners
					);
				while (i.HasNext())
				{
					((com.db4o.TransactionListener)i.Next()).PreCommit();
				}
				i_transactionListeners = null;
			}
		}

		private int CountSlotChanges()
		{
			int count = 0;
			if (i_parentTransaction != null)
			{
				count += i_parentTransaction.CountSlotChanges();
			}
			int[] slotSetPointerCount = { count };
			if (_slotChanges != null)
			{
				_slotChanges.Traverse(new _AnonymousInnerClass350(this, slotSetPointerCount));
			}
			return slotSetPointerCount[0];
		}

		private sealed class _AnonymousInnerClass350 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass350(Transaction _enclosing, int[] slotSetPointerCount)
			{
				this._enclosing = _enclosing;
				this.slotSetPointerCount = slotSetPointerCount;
			}

			public void Visit(object obj)
			{
				com.db4o.inside.slots.SlotChange slot = (com.db4o.inside.slots.SlotChange)obj;
				if (slot.IsSetPointer())
				{
					slotSetPointerCount[0]++;
				}
			}

			private readonly Transaction _enclosing;

			private readonly int[] slotSetPointerCount;
		}

		private com.db4o.Tree CreateClassIndexNode(com.db4o.Tree a_tree, com.db4o.Tree[] 
			a_node)
		{
			if (a_tree != null)
			{
				com.db4o.Tree existing = a_tree.Find(a_node[0]);
				if (existing != null)
				{
					a_node[0] = existing;
				}
				else
				{
					a_tree = a_tree.Add(a_node[0]);
				}
			}
			else
			{
				a_tree = a_node[0];
			}
			return a_tree;
		}

		internal virtual void Delete(com.db4o.YapObject a_yo, int a_cascade)
		{
			int id = a_yo.GetID();
			com.db4o.DeleteInfo info = (com.db4o.DeleteInfo)com.db4o.TreeInt.Find(i_delete, id
				);
			if (info == null)
			{
				info = new com.db4o.DeleteInfo(id, a_yo, true, a_cascade);
				i_delete = com.db4o.Tree.Add(i_delete, info);
				return;
			}
			info._reference = a_yo;
			if (a_cascade > info._cascade)
			{
				info._cascade = a_cascade;
			}
		}

		public virtual void DirtyBTree(com.db4o.inside.btree.BTree btree)
		{
			_dirtyBTrees = com.db4o.Tree.Add(_dirtyBTrees, new com.db4o.TreeIntObject(btree.GetID
				(), btree));
		}

		internal virtual void DontDelete(int classID, int a_id)
		{
			com.db4o.DeleteInfo info = (com.db4o.DeleteInfo)com.db4o.TreeInt.Find(i_delete, a_id
				);
			if (info == null)
			{
				i_delete = com.db4o.Tree.Add(i_delete, new com.db4o.DeleteInfo(a_id, null, false, 
					0));
			}
			else
			{
				info._delete = false;
			}
			com.db4o.YapClass yc = i_stream.GetYapClass(classID);
			DontDeleteAllAncestors(yc, a_id);
		}

		internal virtual void DontDeleteAllAncestors(com.db4o.YapClass yapClass, int objectID
			)
		{
			if (yapClass == null)
			{
				return;
			}
			RemoveFromClassIndexTree(i_removeFromClassIndex, yapClass.GetID(), objectID);
			DontDeleteAllAncestors(yapClass.i_ancestor, objectID);
		}

		internal virtual void DontRemoveFromClassIndex(int a_yapClassID, int a_id)
		{
			RemoveFromClassIndexTree(i_removeFromClassIndex, a_yapClassID, a_id);
			com.db4o.YapClass yapClass = i_stream.GetYapClass(a_yapClassID);
			if (com.db4o.TreeInt.Find(yapClass.GetIndexRoot(), a_id) == null)
			{
				AddToClassIndex(a_yapClassID, a_id);
			}
		}

		private com.db4o.inside.slots.SlotChange FindSlotChange(int a_id)
		{
			return (com.db4o.inside.slots.SlotChange)com.db4o.TreeInt.Find(_slotChanges, a_id
				);
		}

		private void FlushFile()
		{
			if (i_file.i_config.FlushFileBuffers())
			{
				i_file.SyncFiles();
			}
		}

		private void FreeOnCommit()
		{
			if (i_parentTransaction != null)
			{
				i_parentTransaction.FreeOnCommit();
			}
			if (_slotChanges != null)
			{
				_slotChanges.Traverse(new _AnonymousInnerClass477(this));
			}
		}

		private sealed class _AnonymousInnerClass477 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass477(Transaction _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object obj)
			{
				((com.db4o.inside.slots.SlotChange)obj).FreeDuringCommit(this._enclosing.i_file);
			}

			private readonly Transaction _enclosing;
		}

		public virtual com.db4o.inside.slots.Slot GetSlotInformation(int a_id)
		{
			if (a_id == 0)
			{
				return null;
			}
			com.db4o.inside.slots.SlotChange change = FindSlotChange(a_id);
			if (change != null)
			{
				if (change.IsSetPointer())
				{
					return change.NewSlot();
				}
			}
			if (i_parentTransaction != null)
			{
				com.db4o.inside.slots.Slot parentSlot = i_parentTransaction.GetSlotInformation(a_id
					);
				if (parentSlot != null)
				{
					return parentSlot;
				}
			}
			i_file.ReadBytes(_pointerBuffer, a_id, com.db4o.YapConst.POINTER_LENGTH);
			int address = (_pointerBuffer[3] & 255) | (_pointerBuffer[2] & 255) << 8 | (_pointerBuffer
				[1] & 255) << 16 | _pointerBuffer[0] << 24;
			int length = (_pointerBuffer[7] & 255) | (_pointerBuffer[6] & 255) << 8 | (_pointerBuffer
				[5] & 255) << 16 | _pointerBuffer[4] << 24;
			return new com.db4o.inside.slots.Slot(address, length);
		}

		internal virtual bool IsDeleted(int a_id)
		{
			com.db4o.inside.slots.SlotChange slot = FindSlotChange(a_id);
			if (slot != null)
			{
				return slot.IsDeleted();
			}
			if (i_parentTransaction != null)
			{
				return i_parentTransaction.IsDeleted(a_id);
			}
			return false;
		}

		internal virtual object[] ObjectAndYapObjectBySignature(long a_uuid, byte[] a_signature
			)
		{
			object[] ret = new object[2];
			com.db4o.inside.ix.IxTree ixTree = (com.db4o.inside.ix.IxTree)i_stream.i_handlers
				.i_indexes.i_fieldUUID.GetIndexRoot(this);
			com.db4o.inside.ix.IxTraverser ixTraverser = new com.db4o.inside.ix.IxTraverser();
			int count = ixTraverser.FindBoundsExactMatch(a_uuid, ixTree);
			if (count > 0)
			{
				com.db4o.Transaction finalThis = this;
				ixTraverser.VisitAll(new _AnonymousInnerClass548(this, finalThis, a_signature, ret
					));
			}
			return ret;
		}

		private sealed class _AnonymousInnerClass548 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass548(Transaction _enclosing, com.db4o.Transaction finalThis
				, byte[] a_signature, object[] ret)
			{
				this._enclosing = _enclosing;
				this.finalThis = finalThis;
				this.a_signature = a_signature;
				this.ret = ret;
			}

			public void Visit(object a_object)
			{
				object[] arr = finalThis.i_stream.GetObjectAndYapObjectByID(finalThis, ((int)a_object
					));
				if (arr[1] != null)
				{
					com.db4o.YapObject yod = (com.db4o.YapObject)arr[1];
					com.db4o.VirtualAttributes vad = yod.VirtualAttributes(finalThis);
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

		private com.db4o.inside.slots.SlotChange ProduceSlotChange(int id)
		{
			com.db4o.inside.slots.SlotChange slot = new com.db4o.inside.slots.SlotChange(id);
			_slotChanges = com.db4o.Tree.Add(_slotChanges, slot);
			return (com.db4o.inside.slots.SlotChange)slot.DuplicateOrThis();
		}

		internal virtual com.db4o.reflect.Reflector Reflector()
		{
			return i_stream.Reflector();
		}

		internal virtual void RemoveFromClassIndex(int a_yapClassID, int a_id)
		{
			RemoveFromClassIndexTree(i_addToClassIndex, a_yapClassID, a_id);
			i_removeFromClassIndex = AddToClassIndexTree(i_removeFromClassIndex, a_yapClassID
				, a_id);
		}

		private void RemoveFromClassIndexTree(com.db4o.Tree a_tree, int a_yapClassID, int
			 a_id)
		{
			if (a_tree != null)
			{
				com.db4o.TreeIntObject node = (com.db4o.TreeIntObject)((com.db4o.TreeInt)a_tree).
					Find(a_yapClassID);
				if (node != null)
				{
					node._object = com.db4o.Tree.RemoveLike((com.db4o.Tree)node._object, new com.db4o.TreeInt
						(a_id));
				}
			}
		}

		public virtual void Rollback()
		{
			lock (i_stream.i_lock)
			{
				BeginEndSet();
				if (_dirtyBTrees != null)
				{
					_dirtyBTrees.Traverse(new _AnonymousInnerClass628(this));
				}
				if (i_dirtyFieldIndexes != null)
				{
					com.db4o.foundation.Iterator4 i = new com.db4o.foundation.Iterator4Impl(i_dirtyFieldIndexes
						);
					while (i.HasNext())
					{
						((com.db4o.inside.ix.IndexTransaction)i.Next()).Rollback();
					}
				}
				if (_slotChanges != null)
				{
					_slotChanges.Traverse(new _AnonymousInnerClass644(this));
				}
				RollBackTransactionListeners();
				ClearAll();
			}
		}

		private sealed class _AnonymousInnerClass628 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass628(Transaction _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object obj)
			{
				com.db4o.inside.btree.BTree btree = (com.db4o.inside.btree.BTree)((com.db4o.TreeIntObject
					)obj)._object;
				btree.Rollback(this._enclosing);
			}

			private readonly Transaction _enclosing;
		}

		private sealed class _AnonymousInnerClass644 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass644(Transaction _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object a_object)
			{
				((com.db4o.inside.slots.SlotChange)a_object).Rollback(this._enclosing.i_file);
			}

			private readonly Transaction _enclosing;
		}

		internal virtual void RollBackTransactionListeners()
		{
			if (i_transactionListeners != null)
			{
				com.db4o.foundation.Iterator4 i = new com.db4o.foundation.Iterator4Impl(i_transactionListeners
					);
				while (i.HasNext())
				{
					((com.db4o.TransactionListener)i.Next()).PostRollback();
				}
				i_transactionListeners = null;
			}
		}

		internal virtual void SetAddress(int a_address)
		{
			i_address = a_address;
		}

		public virtual void SetPointer(int a_id, int a_address, int a_length)
		{
			ProduceSlotChange(a_id).SetPointer(a_address, a_length);
		}

		internal virtual void SlotDelete(int a_id, int a_address, int a_length)
		{
			if (a_id == 0)
			{
				return;
			}
			com.db4o.inside.slots.SlotChange slot = ProduceSlotChange(a_id);
			slot.FreeOnCommit(i_file, new com.db4o.inside.slots.Slot(a_address, a_length));
			slot.SetPointer(0, 0);
		}

		public virtual void SlotFreeOnCommit(int a_id, int a_address, int a_length)
		{
			if (a_id == 0)
			{
				return;
			}
			ProduceSlotChange(a_id).FreeOnCommit(i_file, new com.db4o.inside.slots.Slot(a_address
				, a_length));
		}

		internal virtual void SlotFreeOnRollback(int a_id, int a_address, int a_length)
		{
			ProduceSlotChange(a_id).FreeOnRollback(a_address, a_length);
		}

		internal virtual void SlotFreeOnRollbackCommitSetPointer(int a_id, int newAddress
			, int newLength)
		{
			com.db4o.inside.slots.Slot slot = GetSlotInformation(a_id);
			com.db4o.inside.slots.SlotChange change = ProduceSlotChange(a_id);
			change.FreeOnRollbackSetPointer(newAddress, newLength);
			change.FreeOnCommit(i_file, slot);
		}

		internal virtual void SlotFreeOnRollbackSetPointer(int a_id, int a_address, int a_length
			)
		{
			ProduceSlotChange(a_id).FreeOnRollbackSetPointer(a_address, a_length);
		}

		public virtual void SlotFreePointerOnCommit(int a_id)
		{
			com.db4o.inside.slots.Slot slot = GetSlotInformation(a_id);
			if (slot == null)
			{
				return;
			}
			SlotFreeOnCommit(a_id, slot._address, slot._length);
		}

		internal virtual void SlotFreePointerOnCommit(int a_id, int a_address, int a_length
			)
		{
			SlotFreeOnCommit(a_address, a_address, a_length);
			SlotFreeOnCommit(a_id, a_id, com.db4o.YapConst.POINTER_LENGTH);
		}

		internal virtual bool SupportsVirtualFields()
		{
			return true;
		}

		public virtual com.db4o.Transaction SystemTransaction()
		{
			if (i_parentTransaction != null)
			{
				return i_parentTransaction;
			}
			return this;
		}

		public override string ToString()
		{
			return i_stream.ToString();
		}

		internal virtual void TraverseAddedClassIDs(int a_yapClassID, com.db4o.foundation.Visitor4
			 visitor)
		{
			TraverseDeep(i_addToClassIndex, a_yapClassID, visitor);
		}

		internal virtual void TraverseDeep(com.db4o.Tree a_tree, int a_yapClassID, com.db4o.foundation.Visitor4
			 visitor)
		{
			if (a_tree != null)
			{
				com.db4o.TreeIntObject node = (com.db4o.TreeIntObject)((com.db4o.TreeInt)a_tree).
					Find(a_yapClassID);
				if (node != null && node._object != null)
				{
					((com.db4o.Tree)node._object).Traverse(visitor);
				}
			}
		}

		internal virtual void TraverseRemovedClassIDs(int a_yapClassID, com.db4o.foundation.Visitor4
			 visitor)
		{
			TraverseDeep(i_removeFromClassIndex, a_yapClassID, visitor);
		}

		private void TraverseYapClassEntries(com.db4o.Tree a_tree, bool a_add, com.db4o.foundation.Collection4
			 a_indices)
		{
			return;
			if (a_tree != null)
			{
				a_tree.Traverse(new _AnonymousInnerClass822(this, a_add, a_indices));
			}
		}

		private sealed class _AnonymousInnerClass822 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass822(Transaction _enclosing, bool a_add, com.db4o.foundation.Collection4
				 a_indices)
			{
				this._enclosing = _enclosing;
				this.a_add = a_add;
				this.a_indices = a_indices;
			}

			public void Visit(object obj)
			{
				com.db4o.TreeIntObject node = (com.db4o.TreeIntObject)obj;
				com.db4o.YapClass yapClass = this._enclosing.i_stream.i_classCollection.GetYapClass
					(node._key);
				com.db4o.ClassIndex classIndex = yapClass.GetIndex();
				if (node._object != null)
				{
					com.db4o.foundation.Visitor4 visitor = null;
					if (a_add)
					{
						visitor = new _AnonymousInnerClass831(this, classIndex);
					}
					else
					{
						visitor = new _AnonymousInnerClass837(this, classIndex);
					}
					((com.db4o.Tree)node._object).Traverse(visitor);
					if (!a_indices.ContainsByIdentity(classIndex))
					{
						a_indices.Add(classIndex);
					}
				}
			}

			private sealed class _AnonymousInnerClass831 : com.db4o.foundation.Visitor4
			{
				public _AnonymousInnerClass831(_AnonymousInnerClass822 _enclosing, com.db4o.ClassIndex
					 classIndex)
				{
					this._enclosing = _enclosing;
					this.classIndex = classIndex;
				}

				public void Visit(object a_object)
				{
					classIndex.Add(((com.db4o.TreeInt)a_object)._key);
				}

				private readonly _AnonymousInnerClass822 _enclosing;

				private readonly com.db4o.ClassIndex classIndex;
			}

			private sealed class _AnonymousInnerClass837 : com.db4o.foundation.Visitor4
			{
				public _AnonymousInnerClass837(_AnonymousInnerClass822 _enclosing, com.db4o.ClassIndex
					 classIndex)
				{
					this._enclosing = _enclosing;
					this.classIndex = classIndex;
				}

				public void Visit(object a_object)
				{
					int id = ((com.db4o.TreeInt)a_object)._key;
					com.db4o.YapObject yo = this._enclosing._enclosing.i_stream.GetYapObject(id);
					if (yo != null)
					{
						this._enclosing._enclosing.i_stream.YapObjectGCd(yo);
					}
					classIndex.Remove(id);
				}

				private readonly _AnonymousInnerClass822 _enclosing;

				private readonly com.db4o.ClassIndex classIndex;
			}

			private readonly Transaction _enclosing;

			private readonly bool a_add;

			private readonly com.db4o.foundation.Collection4 a_indices;
		}

		internal virtual void WriteOld()
		{
			lock (i_stream.i_lock)
			{
				i_pointerIo.UseSlot(i_address);
				i_pointerIo.Read();
				int length = i_pointerIo.ReadInt();
				if (length > 0)
				{
					com.db4o.YapWriter bytes = new com.db4o.YapWriter(this, i_address, length);
					bytes.Read();
					bytes.IncrementOffset(com.db4o.YapConst.YAPINT_LENGTH);
					_slotChanges = new com.db4o.TreeReader(bytes, new com.db4o.inside.slots.SlotChange
						(0)).Read();
					if (WriteSlots())
					{
						FlushFile();
					}
					i_stream.WriteTransactionPointer(0);
					FlushFile();
					FreeOnCommit();
				}
				else
				{
					i_stream.WriteTransactionPointer(0);
					FlushFile();
				}
			}
		}

		public virtual void WritePointer(int a_id, int a_address, int a_length)
		{
			i_pointerIo.UseSlot(a_id);
			i_pointerIo.WriteInt(a_address);
			i_pointerIo.WriteInt(a_length);
			if (com.db4o.Debug.xbytes && com.db4o.Deploy.overwrite)
			{
				i_pointerIo.SetID(com.db4o.YapConst.IGNORE_ID);
			}
			i_pointerIo.Write();
		}

		private bool WriteSlots()
		{
			bool ret = false;
			if (i_parentTransaction != null)
			{
				if (i_parentTransaction.WriteSlots())
				{
					ret = true;
				}
			}
			if (_slotChanges != null)
			{
				_slotChanges.Traverse(new _AnonymousInnerClass925(this));
				ret = true;
			}
			return ret;
		}

		private sealed class _AnonymousInnerClass925 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass925(Transaction _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object a_object)
			{
				((com.db4o.inside.slots.SlotChange)a_object).WritePointer(this._enclosing);
			}

			private readonly Transaction _enclosing;
		}

		internal virtual void WriteUpdateDeleteMembers(int a_id, com.db4o.YapClass a_yc, 
			int a_type, int a_cascade)
		{
			if (com.db4o.Tree.Find(i_writtenUpdateDeletedMembers, new com.db4o.TreeInt(a_id))
				 != null)
			{
				return;
			}
			i_writtenUpdateDeletedMembers = com.db4o.Tree.Add(i_writtenUpdateDeletedMembers, 
				new com.db4o.TreeInt(a_id));
			com.db4o.YapWriter objectBytes = i_stream.ReadWriterByID(this, a_id);
			if (objectBytes == null)
			{
				if (a_yc.HasIndex())
				{
					DontRemoveFromClassIndex(a_yc.GetID(), a_id);
				}
				return;
			}
			com.db4o.inside.marshall.ObjectHeader oh = new com.db4o.inside.marshall.ObjectHeader
				(i_stream, a_yc, objectBytes);
			com.db4o.DeleteInfo info = (com.db4o.DeleteInfo)com.db4o.TreeInt.Find(i_delete, a_id
				);
			if (info != null)
			{
				if (info._cascade > a_cascade)
				{
					a_cascade = info._cascade;
				}
			}
			objectBytes.SetCascadeDeletes(a_cascade);
			a_yc.DeleteMembers(oh._marshallerFamily, oh._headerAttributes, objectBytes, a_type
				, true);
			SlotFreeOnCommit(a_id, objectBytes.GetAddress(), objectBytes.GetLength());
		}
	}
}
