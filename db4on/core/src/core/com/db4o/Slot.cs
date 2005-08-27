
namespace com.db4o
{
	internal sealed class Slot : com.db4o.ReadWriteable
	{
		internal int i_address;

		internal int i_length;

		internal int i_references;

		internal Slot(int address, int length)
		{
			i_address = address;
			i_length = length;
		}

		public int byteCount()
		{
			return com.db4o.YapConst.YAPINT_LENGTH * 2;
		}

		public void write(com.db4o.YapWriter a_bytes)
		{
			a_bytes.writeInt(i_address);
			a_bytes.writeInt(i_length);
		}

		public object read(com.db4o.YapReader a_bytes)
		{
			int address = a_bytes.readInt();
			int length = a_bytes.readInt();
			return new com.db4o.Slot(address, length);
		}
	}
}
