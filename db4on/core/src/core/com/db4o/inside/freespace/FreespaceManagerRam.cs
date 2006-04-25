namespace com.db4o.inside.freespace
{
	public class FreespaceManagerRam : com.db4o.inside.freespace.FreespaceManager
	{
		private readonly com.db4o.TreeIntObject _finder = new com.db4o.TreeIntObject(0);

		private com.db4o.Tree _freeByAddress;

		private com.db4o.Tree _freeBySize;

		public FreespaceManagerRam(com.db4o.YapFile file) : base(file)
		{
		}

		private void addFreeSlotNodes(int a_address, int a_length)
		{
			com.db4o.inside.freespace.FreeSlotNode addressNode = new com.db4o.inside.freespace.FreeSlotNode
				(a_address);
			addressNode.createPeer(a_length);
			_freeByAddress = com.db4o.Tree.add(_freeByAddress, addressNode);
			_freeBySize = com.db4o.Tree.add(_freeBySize, addressNode._peer);
		}

		public override void beginCommit()
		{
		}

		public override void debug()
		{
		}

		public override void endCommit()
		{
		}

		public override void free(int a_address, int a_length)
		{
			if (a_address <= 0)
			{
				return;
			}
			if (a_length <= discardLimit())
			{
				return;
			}
			a_length = _file.blocksFor(a_length);
			_finder._key = a_address;
			com.db4o.inside.freespace.FreeSlotNode sizeNode;
			com.db4o.inside.freespace.FreeSlotNode addressnode = (com.db4o.inside.freespace.FreeSlotNode
				)com.db4o.Tree.findSmaller(_freeByAddress, _finder);
			if ((addressnode != null) && ((addressnode._key + addressnode._peer._key) == a_address
				))
			{
				sizeNode = addressnode._peer;
				_freeBySize = _freeBySize.removeNode(sizeNode);
				sizeNode._key += a_length;
				com.db4o.inside.freespace.FreeSlotNode secondAddressNode = (com.db4o.inside.freespace.FreeSlotNode
					)com.db4o.Tree.findGreaterOrEqual(_freeByAddress, _finder);
				if ((secondAddressNode != null) && (a_address + a_length == secondAddressNode._key
					))
				{
					sizeNode._key += secondAddressNode._peer._key;
					_freeBySize = _freeBySize.removeNode(secondAddressNode._peer);
					_freeByAddress = _freeByAddress.removeNode(secondAddressNode);
				}
				sizeNode.removeChildren();
				_freeBySize = com.db4o.Tree.add(_freeBySize, sizeNode);
			}
			else
			{
				addressnode = (com.db4o.inside.freespace.FreeSlotNode)com.db4o.Tree.findGreaterOrEqual
					(_freeByAddress, _finder);
				if ((addressnode != null) && (a_address + a_length == addressnode._key))
				{
					sizeNode = addressnode._peer;
					_freeByAddress = _freeByAddress.removeNode(addressnode);
					_freeBySize = _freeBySize.removeNode(sizeNode);
					sizeNode._key += a_length;
					addressnode._key = a_address;
					addressnode.removeChildren();
					sizeNode.removeChildren();
					_freeByAddress = com.db4o.Tree.add(_freeByAddress, addressnode);
					_freeBySize = com.db4o.Tree.add(_freeBySize, sizeNode);
				}
				else
				{
					addFreeSlotNodes(a_address, a_length);
				}
			}
		}

		public override void freeSelf()
		{
		}

		public override int getSlot(int length)
		{
			int address = getSlot1(length);
			if (address != 0)
			{
			}
			return address;
		}

		public virtual int getSlot1(int length)
		{
			length = _file.blocksFor(length);
			_finder._key = length;
			_finder._object = null;
			_freeBySize = com.db4o.inside.freespace.FreeSlotNode.removeGreaterOrEqual((com.db4o.inside.freespace.FreeSlotNode
				)_freeBySize, _finder);
			if (_finder._object == null)
			{
				return 0;
			}
			com.db4o.inside.freespace.FreeSlotNode node = (com.db4o.inside.freespace.FreeSlotNode
				)_finder._object;
			int blocksFound = node._key;
			int address = node._peer._key;
			_freeByAddress = _freeByAddress.removeNode(node._peer);
			if (blocksFound > length)
			{
				addFreeSlotNodes(address + length, blocksFound - length);
			}
			return address;
		}

		public override void migrate(com.db4o.inside.freespace.FreespaceManager newFM)
		{
			if (_freeByAddress != null)
			{
				_freeByAddress.traverse(new _AnonymousInnerClass160(this, newFM));
			}
		}

		private sealed class _AnonymousInnerClass160 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass160(FreespaceManagerRam _enclosing, com.db4o.inside.freespace.FreespaceManager
				 newFM)
			{
				this._enclosing = _enclosing;
				this.newFM = newFM;
			}

			public void visit(object a_object)
			{
				com.db4o.inside.freespace.FreeSlotNode fsn = (com.db4o.inside.freespace.FreeSlotNode
					)a_object;
				int address = fsn._key;
				int length = fsn._peer._key;
				newFM.free(address, length);
			}

			private readonly FreespaceManagerRam _enclosing;

			private readonly com.db4o.inside.freespace.FreespaceManager newFM;
		}

		public override void read(int freeSlotsID)
		{
			if (freeSlotsID <= 0)
			{
				return;
			}
			if (discardLimit() == int.MaxValue)
			{
				return;
			}
			com.db4o.YapWriter reader = _file.readWriterByID(trans(), freeSlotsID);
			if (reader == null)
			{
				return;
			}
			com.db4o.inside.freespace.FreeSlotNode.sizeLimit = discardLimit();
			_freeBySize = new com.db4o.TreeReader(reader, new com.db4o.inside.freespace.FreeSlotNode
				(0), true).read();
			com.db4o.Tree[] addressTree = new com.db4o.Tree[1];
			if (_freeBySize != null)
			{
				_freeBySize.traverse(new _AnonymousInnerClass189(this, addressTree));
			}
			_freeByAddress = addressTree[0];
			_file.free(freeSlotsID, com.db4o.YapConst.POINTER_LENGTH);
			_file.free(reader.getAddress(), reader.getLength());
		}

		private sealed class _AnonymousInnerClass189 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass189(FreespaceManagerRam _enclosing, com.db4o.Tree[] addressTree
				)
			{
				this._enclosing = _enclosing;
				this.addressTree = addressTree;
			}

			public void visit(object a_object)
			{
				com.db4o.inside.freespace.FreeSlotNode node = ((com.db4o.inside.freespace.FreeSlotNode
					)a_object)._peer;
				addressTree[0] = com.db4o.Tree.add(addressTree[0], node);
			}

			private readonly FreespaceManagerRam _enclosing;

			private readonly com.db4o.Tree[] addressTree;
		}

		public override void start(int slotAddress)
		{
		}

		public override byte systemType()
		{
			return FM_RAM;
		}

		private com.db4o.Transaction trans()
		{
			return _file.i_systemTrans;
		}

		public override int write(bool shuttingDown)
		{
			if (!shuttingDown)
			{
				return 0;
			}
			int freeBySizeID = 0;
			int length = com.db4o.Tree.byteCount(_freeBySize);
			com.db4o.inside.slots.Pointer4 ptr = _file.newSlot(trans(), length);
			freeBySizeID = ptr._id;
			com.db4o.YapWriter sdwriter = new com.db4o.YapWriter(trans(), length);
			sdwriter.useSlot(freeBySizeID, ptr._address, length);
			com.db4o.Tree.write(sdwriter, _freeBySize);
			sdwriter.writeEncrypt();
			trans().writePointer(ptr._id, ptr._address, length);
			return freeBySizeID;
		}
	}
}
