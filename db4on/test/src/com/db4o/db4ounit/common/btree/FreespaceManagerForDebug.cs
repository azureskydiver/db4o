namespace com.db4o.db4ounit.common.btree
{
	public class FreespaceManagerForDebug : com.db4o.@internal.freespace.FreespaceManager
	{
		private readonly com.db4o.db4ounit.common.btree.SlotListener _listener;

		public FreespaceManagerForDebug(com.db4o.@internal.LocalObjectContainer file, com.db4o.db4ounit.common.btree.SlotListener
			 listener) : base(file)
		{
			_listener = listener;
		}

		public override void BeginCommit()
		{
		}

		public override void Debug()
		{
		}

		public override void EndCommit()
		{
		}

		public override int EntryCount()
		{
			return 0;
		}

		public override void Free(int address, int length)
		{
			_listener.OnFree(new com.db4o.@internal.slots.Slot(address, length));
		}

		public override void FreeSelf()
		{
		}

		public override int FreeSize()
		{
			return 0;
		}

		public override int GetSlot(int length)
		{
			return 0;
		}

		public override void Migrate(com.db4o.@internal.freespace.FreespaceManager newFM)
		{
		}

		public override void OnNew(com.db4o.@internal.LocalObjectContainer file)
		{
		}

		public override void Read(int freeSlotsID)
		{
		}

		public override void Start(int slotAddress)
		{
		}

		public override byte SystemType()
		{
			return FM_DEBUG;
		}

		public override int Write(bool shuttingDown)
		{
			return 0;
		}
	}
}
