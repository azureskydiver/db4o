namespace com.db4o.inside.freespace
{
	/// <exclude></exclude>
	public sealed class FreeSlotNode : com.db4o.TreeInt
	{
		public static int sizeLimit;

		internal com.db4o.inside.freespace.FreeSlotNode _peer;

		internal FreeSlotNode(int a_key) : base(a_key)
		{
		}

		public override object shallowClone()
		{
			com.db4o.inside.freespace.FreeSlotNode frslot = new com.db4o.inside.freespace.FreeSlotNode
				(_key);
			frslot._peer = _peer;
			return base.shallowCloneInternal(frslot);
		}

		internal void createPeer(int a_key)
		{
			_peer = new com.db4o.inside.freespace.FreeSlotNode(a_key);
			_peer._peer = this;
		}

		public override bool duplicates()
		{
			return true;
		}

		public sealed override int ownLength()
		{
			return com.db4o.YapConst.YAPINT_LENGTH * 2;
		}

		internal static com.db4o.Tree removeGreaterOrEqual(com.db4o.inside.freespace.FreeSlotNode
			 a_in, com.db4o.TreeIntObject a_finder)
		{
			if (a_in == null)
			{
				return null;
			}
			int cmp = a_in._key - a_finder._key;
			if (cmp == 0)
			{
				a_finder._object = a_in;
				return a_in.remove();
			}
			else
			{
				if (cmp > 0)
				{
					a_in._preceding = removeGreaterOrEqual((com.db4o.inside.freespace.FreeSlotNode)a_in
						._preceding, a_finder);
					if (a_finder._object != null)
					{
						a_in._size--;
						return a_in;
					}
					a_finder._object = a_in;
					return a_in.remove();
				}
				else
				{
					a_in._subsequent = removeGreaterOrEqual((com.db4o.inside.freespace.FreeSlotNode)a_in
						._subsequent, a_finder);
					if (a_finder._object != null)
					{
						a_in._size--;
					}
					return a_in;
				}
			}
		}

		public override object read(com.db4o.YapReader a_reader)
		{
			int size = a_reader.readInt();
			int address = a_reader.readInt();
			if (size > sizeLimit)
			{
				com.db4o.inside.freespace.FreeSlotNode node = new com.db4o.inside.freespace.FreeSlotNode
					(size);
				node.createPeer(address);
				return node;
			}
			return null;
		}

		public sealed override void write(com.db4o.YapReader a_writer)
		{
			a_writer.writeInt(_key);
			a_writer.writeInt(_peer._key);
		}

		public override string ToString()
		{
			return base.ToString();
			string str = "FreeSlotNode " + _key;
			if (_peer != null)
			{
				str += " peer: " + _peer._key;
			}
			return str;
		}
	}
}
