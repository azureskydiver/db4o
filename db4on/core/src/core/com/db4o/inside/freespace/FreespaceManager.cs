namespace com.db4o.inside.freespace
{
	public abstract class FreespaceManager
	{
		internal readonly com.db4o.YapFile _file;

		public const byte FM_DEFAULT = 0;

		public const byte FM_LEGACY_RAM = 1;

		public const byte FM_RAM = 2;

		public const byte FM_IX = 3;

		public const byte FM_DEBUG = 4;

		private const int INTS_IN_SLOT = 12;

		public FreespaceManager(com.db4o.YapFile file)
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

		public static com.db4o.inside.freespace.FreespaceManager CreateNew(com.db4o.YapFile
			 file, byte systemType)
		{
			systemType = CheckType(systemType);
			switch (systemType)
			{
				case FM_LEGACY_RAM:				case FM_RAM:
				{
					return new com.db4o.inside.freespace.FreespaceManagerRam(file);
				}

				default:
				{
					return new com.db4o.inside.freespace.FreespaceManagerIx(file);
					break;
				}
			}
		}

		public static int InitSlot(com.db4o.YapFile file)
		{
			int address = file.GetSlot(SlotLength());
			SlotEntryToZeroes(file, address);
			return address;
		}

		internal static void SlotEntryToZeroes(com.db4o.YapFile file, int address)
		{
			com.db4o.YapWriter writer = new com.db4o.YapWriter(file.GetSystemTransaction(), address
				, SlotLength());
			for (int i = 0; i < INTS_IN_SLOT; i++)
			{
				writer.WriteInt(0);
			}
			writer.WriteEncrypt();
		}

		internal static int SlotLength()
		{
			return com.db4o.YapConst.INT_LENGTH * INTS_IN_SLOT;
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

		public abstract void Free(int a_address, int a_length);

		public abstract void FreeSelf();

		public abstract int GetSlot(int length);

		public abstract void Migrate(com.db4o.inside.freespace.FreespaceManager newFM);

		public abstract void Read(int freeSlotsID);

		public abstract void Start(int slotAddress);

		public abstract byte SystemType();

		public abstract int Write(bool shuttingDown);
	}
}
