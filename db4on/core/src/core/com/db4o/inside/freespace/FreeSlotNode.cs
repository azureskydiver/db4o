namespace com.db4o.inside.freespace
{
	/// <exclude></exclude>
	public sealed class FreeSlotNode : com.db4o.TreeInt
	{
		public static int sizeLimit;

		internal com.db4o.inside.freespace.FreeSlotNode i_peer;

		internal FreeSlotNode(int a_key) : base(a_key)
		{
		}

		internal void createPeer(int a_key)
		{
			i_peer = new com.db4o.inside.freespace.FreeSlotNode(a_key);
			i_peer.i_peer = this;
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
			int cmp = a_in.i_key - a_finder.i_key;
			if (cmp == 0)
			{
				a_finder.i_object = a_in;
				return a_in.remove();
			}
			else
			{
				if (cmp > 0)
				{
					a_in.i_preceding = removeGreaterOrEqual((com.db4o.inside.freespace.FreeSlotNode)a_in
						.i_preceding, a_finder);
					if (a_finder.i_object != null)
					{
						a_in.i_size--;
						return a_in;
					}
					a_finder.i_object = a_in;
					return a_in.remove();
				}
				else
				{
					a_in.i_subsequent = removeGreaterOrEqual((com.db4o.inside.freespace.FreeSlotNode)
						a_in.i_subsequent, a_finder);
					if (a_finder.i_object != null)
					{
						a_in.i_size--;
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

		public sealed override void write(com.db4o.YapWriter a_writer)
		{
			a_writer.writeInt(i_key);
			a_writer.writeInt(i_peer.i_key);
		}

		public override string ToString()
		{
			return base.ToString();
		}
	}
}
