namespace com.db4o.@internal.freespace
{
	public abstract class FreespaceManager
	{
		internal readonly com.db4o.@internal.LocalObjectContainer _file;

		public const byte FM_DEFAULT = 0;

		public const byte FM_LEGACY_RAM = 1;

		public const byte FM_RAM = 2;

		public const byte FM_IX = 3;

		public const byte FM_DEBUG = 4;

		private const int INTS_IN_SLOT = 12;

		public FreespaceManager(com.db4o.@internal.LocalObjectContainer file)
		{
			_file = file;
		}

		public static byte CheckType(byte systemType)
		{
			if (systemType == FM_DEFAULT)
			{
				return FM_RAM;
			}
			return systemType;
		}

		public static com.db4o.@internal.freespace.FreespaceManager CreateNew(com.db4o.@internal.LocalObjectContainer
			 file)
		{
			return CreateNew(file, file.SystemData().FreespaceSystem());
		}

		public abstract void OnNew(com.db4o.@internal.LocalObjectContainer file);

		public static com.db4o.@internal.freespace.FreespaceManager CreateNew(com.db4o.@internal.LocalObjectContainer
			 file, byte systemType)
		{
			systemType = CheckType(systemType);
			switch (systemType)
			{
				case FM_IX:
				{
					return new com.db4o.@internal.freespace.FreespaceManagerIx(file);
				}

				default:
				{
					return new com.db4o.@internal.freespace.FreespaceManagerRam(file);
					break;
				}
			}
		}

		public static int InitSlot(com.db4o.@internal.LocalObjectContainer file)
		{
			int address = file.GetSlot(SlotLength());
			SlotEntryToZeroes(file, address);
			return address;
		}

		internal static void SlotEntryToZeroes(com.db4o.@internal.LocalObjectContainer file
			, int address)
		{
			com.db4o.@internal.StatefulBuffer writer = new com.db4o.@internal.StatefulBuffer(
				file.GetSystemTransaction(), address, SlotLength());
			for (int i = 0; i < INTS_IN_SLOT; i++)
			{
				writer.WriteInt(0);
			}
			writer.WriteEncrypt();
		}

		internal static int SlotLength()
		{
			return com.db4o.@internal.Const4.INT_LENGTH * INTS_IN_SLOT;
		}

		public abstract void BeginCommit();

		internal int BlockSize()
		{
			return _file.BlockSize();
		}

		public abstract void Debug();

		internal int DiscardLimit()
		{
			return _file.ConfigImpl().DiscardFreeSpace();
		}

		public abstract void EndCommit();

		public abstract int EntryCount();

		public abstract void Free(int a_address, int a_length);

		public abstract int FreeSize();

		public abstract void FreeSelf();

		public abstract int GetSlot(int length);

		public abstract void Migrate(com.db4o.@internal.freespace.FreespaceManager newFM);

		public abstract void Read(int freeSlotsID);

		public abstract void Start(int slotAddress);

		public abstract byte SystemType();

		public abstract int Write(bool shuttingDown);

		public virtual bool RequiresMigration(byte configuredSystem, byte readSystem)
		{
			return (configuredSystem != 0 || readSystem == FM_LEGACY_RAM) && (SystemType() !=
				 configuredSystem);
		}

		public static void Migrate(com.db4o.@internal.freespace.FreespaceManager oldFM, com.db4o.@internal.freespace.FreespaceManager
			 newFM)
		{
			oldFM.Migrate(newFM);
			oldFM.FreeSelf();
			newFM.BeginCommit();
			newFM.EndCommit();
		}
	}
}
