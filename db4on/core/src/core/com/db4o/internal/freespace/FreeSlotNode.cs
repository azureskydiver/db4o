namespace com.db4o.@internal.freespace
{
	/// <exclude></exclude>
	public sealed class FreeSlotNode : com.db4o.@internal.TreeInt
	{
		internal static int sizeLimit;

		internal com.db4o.@internal.freespace.FreeSlotNode _peer;

		internal FreeSlotNode(int a_key) : base(a_key)
		{
		}

		public override object ShallowClone()
		{
			com.db4o.@internal.freespace.FreeSlotNode frslot = new com.db4o.@internal.freespace.FreeSlotNode
				(_key);
			frslot._peer = _peer;
			return base.ShallowCloneInternal(frslot);
		}

		internal void CreatePeer(int a_key)
		{
			_peer = new com.db4o.@internal.freespace.FreeSlotNode(a_key);
			_peer._peer = this;
		}

		public override bool Duplicates()
		{
			return true;
		}

		public sealed override int OwnLength()
		{
			return com.db4o.@internal.Const4.INT_LENGTH * 2;
		}

		internal static com.db4o.foundation.Tree RemoveGreaterOrEqual(com.db4o.@internal.freespace.FreeSlotNode
			 a_in, com.db4o.@internal.TreeIntObject a_finder)
		{
			if (a_in == null)
			{
				return null;
			}
			int cmp = a_in._key - a_finder._key;
			if (cmp == 0)
			{
				a_finder._object = a_in;
				return a_in.Remove();
			}
			if (cmp > 0)
			{
				a_in._preceding = RemoveGreaterOrEqual((com.db4o.@internal.freespace.FreeSlotNode
					)a_in._preceding, a_finder);
				if (a_finder._object != null)
				{
					a_in._size--;
					return a_in;
				}
				a_finder._object = a_in;
				return a_in.Remove();
			}
			a_in._subsequent = RemoveGreaterOrEqual((com.db4o.@internal.freespace.FreeSlotNode
				)a_in._subsequent, a_finder);
			if (a_finder._object != null)
			{
				a_in._size--;
			}
			return a_in;
		}

		public override object Read(com.db4o.@internal.Buffer a_reader)
		{
			int size = a_reader.ReadInt();
			int address = a_reader.ReadInt();
			if (size > sizeLimit)
			{
				com.db4o.@internal.freespace.FreeSlotNode node = new com.db4o.@internal.freespace.FreeSlotNode
					(size);
				node.CreatePeer(address);
				return node;
			}
			return null;
		}

		public sealed override void Write(com.db4o.@internal.Buffer a_writer)
		{
			a_writer.WriteInt(_key);
			a_writer.WriteInt(_peer._key);
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
