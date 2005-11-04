namespace com.db4o.inside.slots
{
	public sealed class ReferencedSlot : com.db4o.inside.slots.Slot
	{
		public int _references;

		public ReferencedSlot(int address, int length) : base(address, length)
		{
		}

		public override object read(com.db4o.YapReader a_bytes)
		{
			int address = a_bytes.readInt();
			int length = a_bytes.readInt();
			return new com.db4o.inside.slots.ReferencedSlot(address, length);
		}
	}
}
