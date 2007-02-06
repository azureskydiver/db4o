namespace com.db4o.@internal.slots
{
	/// <exclude></exclude>
	public class SlotChange : com.db4o.@internal.TreeInt
	{
		private int _action;

		private com.db4o.@internal.slots.Slot _newSlot;

		private com.db4o.@internal.slots.ReferencedSlot _shared;

		private const int FREE_ON_COMMIT_BIT = 1;

		private const int FREE_ON_ROLLBACK_BIT = 2;

		private const int SET_POINTER_BIT = 3;

		public SlotChange(int id) : base(id)
		{
		}

		public override object ShallowClone()
		{
			com.db4o.@internal.slots.SlotChange sc = new com.db4o.@internal.slots.SlotChange(
				0);
			sc._action = _action;
			sc._newSlot = _newSlot;
			sc._shared = _shared;
			return base.ShallowCloneInternal(sc);
		}

		private void DoFreeOnCommit()
		{
			SetBit(FREE_ON_COMMIT_BIT);
		}

		private void DoFreeOnRollback()
		{
			SetBit(FREE_ON_ROLLBACK_BIT);
		}

		private void DoSetPointer()
		{
			SetBit(SET_POINTER_BIT);
		}

		public virtual void FreeDuringCommit(com.db4o.@internal.LocalObjectContainer file
			)
		{
			if (IsFreeOnCommit())
			{
				file.FreeDuringCommit(_shared, _newSlot);
			}
		}

		public virtual void FreeOnCommit(com.db4o.@internal.LocalObjectContainer file, com.db4o.@internal.slots.Slot
			 slot)
		{
			if (_shared != null)
			{
				file.Free(slot);
				return;
			}
			DoFreeOnCommit();
			com.db4o.@internal.slots.ReferencedSlot refSlot = file.ProduceFreeOnCommitEntry(_key
				);
			if (refSlot.AddReferenceIsFirst())
			{
				refSlot.PointTo(slot);
			}
			_shared = refSlot;
		}

		public virtual void FreeOnRollback(int address, int length)
		{
			DoFreeOnRollback();
			_newSlot = new com.db4o.@internal.slots.Slot(address, length);
		}

		public virtual void FreeOnRollbackSetPointer(int address, int length)
		{
			DoSetPointer();
			FreeOnRollback(address, length);
		}

		private bool IsBitSet(int bitPos)
		{
			return (_action | (1 << bitPos)) == _action;
		}

		public virtual bool IsDeleted()
		{
			return IsSetPointer() && (_newSlot._address == 0);
		}

		private bool IsFreeOnCommit()
		{
			return IsBitSet(FREE_ON_COMMIT_BIT);
		}

		private bool IsFreeOnRollback()
		{
			return IsBitSet(FREE_ON_ROLLBACK_BIT);
		}

		public bool IsSetPointer()
		{
			return IsBitSet(SET_POINTER_BIT);
		}

		public virtual com.db4o.@internal.slots.Slot NewSlot()
		{
			return _newSlot;
		}

		public virtual com.db4o.@internal.slots.Slot OldSlot()
		{
			if (_shared == null)
			{
				return null;
			}
			return _shared.Slot();
		}

		public override object Read(com.db4o.@internal.Buffer reader)
		{
			com.db4o.@internal.slots.SlotChange change = new com.db4o.@internal.slots.SlotChange
				(reader.ReadInt());
			change._newSlot = new com.db4o.@internal.slots.Slot(reader.ReadInt(), reader.ReadInt
				());
			change.DoSetPointer();
			return change;
		}

		public virtual void Rollback(com.db4o.@internal.LocalObjectContainer yapFile)
		{
			if (_shared != null)
			{
				yapFile.ReduceFreeOnCommitReferences(_shared);
			}
			if (IsFreeOnRollback())
			{
				yapFile.Free(_newSlot);
			}
		}

		private void SetBit(int bitPos)
		{
			_action |= (1 << bitPos);
		}

		public virtual void SetPointer(int address, int length)
		{
			DoSetPointer();
			_newSlot = new com.db4o.@internal.slots.Slot(address, length);
		}

		public override void Write(com.db4o.@internal.Buffer writer)
		{
			if (IsSetPointer())
			{
				writer.WriteInt(_key);
				writer.WriteInt(_newSlot._address);
				writer.WriteInt(_newSlot._length);
			}
		}

		public virtual void WritePointer(com.db4o.@internal.Transaction trans)
		{
			if (IsSetPointer())
			{
				trans.WritePointer(_key, _newSlot._address, _newSlot._length);
			}
		}
	}
}
