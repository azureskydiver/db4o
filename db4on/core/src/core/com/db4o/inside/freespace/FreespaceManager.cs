namespace com.db4o.inside.freespace
{
	public abstract class FreespaceManager
	{
		internal readonly com.db4o.YapFile _file;

		public const byte FM_LEGACY_RAM = 1;

		public const byte FM_RAM = 2;

		public const byte FM_IX = 3;

		private const int INTS_IN_SLOT = 12;

		internal FreespaceManager(com.db4o.YapFile file)
		{
			_file = file;
		}

		public static byte checkType(byte systemType)
		{
			if (systemType == 0)
			{
				return FM_RAM;
			}
			return systemType;
		}

		public static com.db4o.inside.freespace.FreespaceManager createNew(com.db4o.YapFile
			 file, byte systemType)
		{
			systemType = checkType(systemType);
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

		public static int initSlot(com.db4o.YapFile file)
		{
			int address = file.getSlot(slotLength());
			slotEntryToZeroes(file, address);
			return address;
		}

		internal static void slotEntryToZeroes(com.db4o.YapFile file, int address)
		{
			com.db4o.YapWriter writer = new com.db4o.YapWriter(file.i_systemTrans, address, slotLength
				());
			for (int i = 0; i < INTS_IN_SLOT; i++)
			{
				writer.writeInt(0);
			}
			writer.writeEncrypt();
		}

		internal static int slotLength()
		{
			return com.db4o.YapConst.YAPINT_LENGTH * INTS_IN_SLOT;
		}

		public abstract void beginCommit();

		internal int blockSize()
		{
			return _file.blockSize();
		}

		public abstract void debug();

		internal int discardLimit()
		{
			return _file.i_config.i_discardFreeSpace;
		}

		public abstract void endCommit();

		public abstract void free(int a_address, int a_length);

		public abstract void freeSelf();

		public abstract int getSlot(int length);

		public abstract void migrate(com.db4o.inside.freespace.FreespaceManager newFM);

		public abstract void read(int freeSlotsID);

		public abstract void start(int slotAddress);

		public abstract byte systemType();

		public abstract int write(bool shuttingDown);
	}
}
