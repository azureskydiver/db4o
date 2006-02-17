namespace com.db4o.inside.slots
{
	/// <exclude></exclude>
	public class SlotChange : com.db4o.TreeInt
	{
		private int _action;

		private com.db4o.inside.slots.Slot _newSlot;

		private com.db4o.inside.slots.ReferencedSlot _shared;

		private const int FREE_ON_COMMIT_BIT = 1;

		private const int FREE_ON_ROLLBACK_BIT = 2;

		private const int SET_POINTER_BIT = 3;

		public SlotChange(int id) : base(id)
		{
		}

		private void doFreeOnCommit()
		{
			setBit(FREE_ON_COMMIT_BIT);
		}

		private void doFreeOnRollback()
		{
			setBit(FREE_ON_ROLLBACK_BIT);
		}

		private void doSetPointer()
		{
			setBit(SET_POINTER_BIT);
		}

		public virtual void freeDuringCommit(com.db4o.YapFile file)
		{
			if (isFreeOnCommit())
			{
				file.freeDuringCommit(_shared, _newSlot);
			}
		}

		public virtual void freeOnCommit(com.db4o.YapFile file, com.db4o.inside.slots.Slot
			 slot)
		{
			if (_shared != null)
			{
				file.free(slot);
				return;
			}
			doFreeOnCommit();
			com.db4o.inside.slots.ReferencedSlot refSlot = file.produceFreeOnCommitEntry(i_key
				);
			if (refSlot.addReferenceIsFirst())
			{
				refSlot.pointTo(slot);
			}
			_shared = refSlot;
		}

		public virtual void freeOnRollback(int address, int length)
		{
			doFreeOnRollback();
			_newSlot = new com.db4o.inside.slots.Slot(address, length);
		}

		public virtual void freeOnRollbackSetPointer(int address, int length)
		{
			doSetPointer();
			freeOnRollback(address, length);
		}

		private bool isBitSet(int bitPos)
		{
			return (_action | (1 << bitPos)) == _action;
		}

		public virtual bool isDeleted()
		{
			return isSetPointer() && (_newSlot._address == 0);
		}

		private bool isFreeOnCommit()
		{
			return isBitSet(FREE_ON_COMMIT_BIT);
		}

		private bool isFreeOnRollback()
		{
			return isBitSet(FREE_ON_ROLLBACK_BIT);
		}

		public bool isSetPointer()
		{
			return isBitSet(SET_POINTER_BIT);
		}

		public virtual com.db4o.inside.slots.Slot newSlot()
		{
			return _newSlot;
		}

		public override object read(com.db4o.YapReader reader)
		{
			com.db4o.inside.slots.SlotChange change = new com.db4o.inside.slots.SlotChange(reader
				.readInt());
			change._newSlot = new com.db4o.inside.slots.Slot(reader.readInt(), reader.readInt
				());
			change.doSetPointer();
			return change;
		}

		public virtual void rollback(com.db4o.YapFile yapFile)
		{
			if (_shared != null)
			{
				yapFile.reduceFreeOnCommitReferences(_shared);
			}
			if (isFreeOnRollback())
			{
				yapFile.free(_newSlot);
			}
		}

		private void setBit(int bitPos)
		{
			_action |= (1 << bitPos);
		}

		public virtual void setPointer(int address, int length)
		{
			doSetPointer();
			_newSlot = new com.db4o.inside.slots.Slot(address, length);
		}

		public override void write(com.db4o.YapWriter writer)
		{
			if (isSetPointer())
			{
				writer.writeInt(i_key);
				writer.writeInt(_newSlot._address);
				writer.writeInt(_newSlot._length);
			}
		}

		public virtual void writePointer(com.db4o.Transaction trans)
		{
			if (isSetPointer())
			{
				trans.writePointer(i_key, _newSlot._address, _newSlot._length);
			}
		}
	}
}
