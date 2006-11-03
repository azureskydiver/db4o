namespace com.db4o.inside.slots
{
	/// <exclude></exclude>
	public class Slot
	{
		public readonly int _address;

		public readonly int _length;

		public Slot(int address, int length)
		{
			_address = address;
			_length = length;
		}

		public virtual int GetAddress()
		{
			return _address;
		}

		public virtual int GetLength()
		{
			return _length;
		}

		public override bool Equals(object obj)
		{
			if (obj == this)
			{
				return true;
			}
			if (!(obj is com.db4o.inside.slots.Slot))
			{
				return false;
			}
			com.db4o.inside.slots.Slot other = (com.db4o.inside.slots.Slot)obj;
			return (_address == other._address) && (_length == other._length);
		}

		public override int GetHashCode()
		{
			return _address ^ _length;
		}

		public override string ToString()
		{
			return "[A:" + _address + ",L:" + _length + "]";
		}
	}
}
