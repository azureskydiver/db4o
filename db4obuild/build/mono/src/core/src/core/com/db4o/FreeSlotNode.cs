/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
namespace com.db4o
{
	internal sealed class FreeSlotNode : com.db4o.TreeInt
	{
		internal static int sizeLimit;

		internal com.db4o.FreeSlotNode i_peer;

		internal FreeSlotNode(int a_key) : base(a_key)
		{
		}

		internal void createPeer(int a_key)
		{
			i_peer = new com.db4o.FreeSlotNode(a_key);
			i_peer.i_peer = this;
		}

		internal override bool duplicates()
		{
			return true;
		}

		internal sealed override int ownLength()
		{
			return com.db4o.YapConst.YAPINT_LENGTH * 2;
		}

		internal static com.db4o.Tree removeGreaterOrEqual(com.db4o.FreeSlotNode a_in, com.db4o.TreeIntObject
			 a_finder)
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
					a_in.i_preceding = removeGreaterOrEqual((com.db4o.FreeSlotNode)a_in.i_preceding, 
						a_finder);
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
					a_in.i_subsequent = removeGreaterOrEqual((com.db4o.FreeSlotNode)a_in.i_subsequent
						, a_finder);
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
				com.db4o.FreeSlotNode node = new com.db4o.FreeSlotNode(size);
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
	}
}
