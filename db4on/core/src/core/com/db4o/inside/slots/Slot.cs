namespace com.db4o.inside.slots
{
	public class Slot : com.db4o.ReadWriteable
	{
		public int _address;

		public int _length;

		public Slot(int address, int length)
		{
			_address = address;
			_length = length;
		}

		public virtual int byteCount()
		{
			return com.db4o.YapConst.YAPINT_LENGTH * 2;
		}

		public virtual void write(com.db4o.YapWriter a_bytes)
		{
			a_bytes.writeInt(_address);
			a_bytes.writeInt(_length);
		}

		public virtual object read(com.db4o.YapReader a_bytes)
		{
			int address = a_bytes.readInt();
			int length = a_bytes.readInt();
			return new com.db4o.inside.slots.Slot(address, length);
		}
	}
}
