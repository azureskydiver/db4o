namespace com.db4o.inside.freespace
{
	public class FreespaceManagerIx : com.db4o.inside.freespace.FreespaceManager
	{
		private int _slotAddress;

		private com.db4o.inside.freespace.FreespaceIxAddress _addressIx;

		private com.db4o.inside.freespace.FreespaceIxLength _lengthIx;

		private bool _started;

		private com.db4o.foundation.Collection4 _xBytes;

		internal FreespaceManagerIx(com.db4o.YapFile file) : base(file)
		{
		}

		private void add(int address, int length)
		{
			_addressIx.add(address, length);
			_lengthIx.add(address, length);
		}

		public override void beginCommit()
		{
			if (!started())
			{
				return;
			}
			slotEntryToZeroes(_file, _slotAddress);
		}

		public override void debug()
		{
		}

		public override void endCommit()
		{
			if (!started())
			{
				return;
			}
			_addressIx._index.commitFreeSpace(_lengthIx._index);
			com.db4o.YapWriter writer = new com.db4o.YapWriter(_file.i_systemTrans, _slotAddress
				, slotLength());
			_addressIx._index._metaIndex.write(writer);
			_lengthIx._index._metaIndex.write(writer);
			writer.writeEncrypt();
		}

		public override void free(int address, int length)
		{
			if (!started())
			{
				return;
			}
			if (address <= 0)
			{
				return;
			}
			if (length <= discardLimit())
			{
				return;
			}
			length = _file.blocksFor(length);
			int freedAddress = address;
			int freedLength = length;
			_addressIx.find(address);
			if (_addressIx.preceding())
			{
				if (_addressIx.address() + _addressIx.length() == address)
				{
					remove(_addressIx.address(), _addressIx.length());
					address = _addressIx.address();
					length += _addressIx.length();
					_addressIx.find(freedAddress);
				}
			}
			if (_addressIx.subsequent())
			{
				if (freedAddress + freedLength == _addressIx.address())
				{
					remove(_addressIx.address(), _addressIx.length());
					length += _addressIx.length();
				}
			}
			add(address, length);
		}

		public override void freeSelf()
		{
			if (!started())
			{
				return;
			}
			_addressIx._index._metaIndex.free(_file);
			_lengthIx._index._metaIndex.free(_file);
		}

		public override int getSlot(int length)
		{
			if (!started())
			{
				return 0;
			}
			int address = getSlot1(length);
			if (address != 0)
			{
			}
			return address;
		}

		private int getSlot1(int length)
		{
			if (!started())
			{
				return 0;
			}
			length = _file.blocksFor(length);
			_lengthIx.find(length);
			if (_lengthIx.match())
			{
				remove(_lengthIx.address(), _lengthIx.length());
				return _lengthIx.address();
			}
			if (_lengthIx.subsequent())
			{
				int lengthRemainder = _lengthIx.length() - length;
				int addressRemainder = _lengthIx.address() + length;
				remove(_lengthIx.address(), _lengthIx.length());
				add(addressRemainder, lengthRemainder);
				return _lengthIx.address();
			}
			return 0;
		}

		public override void migrate(com.db4o.inside.freespace.FreespaceManager newFM)
		{
			if (!started())
			{
				return;
			}
			com.db4o.foundation.IntObjectVisitor addToNewFM = new _AnonymousInnerClass178(this
				, newFM);
			com.db4o.Tree.traverse(_addressIx._indexTrans.getRoot(), new _AnonymousInnerClass183
				(this, addToNewFM));
		}

		private sealed class _AnonymousInnerClass178 : com.db4o.foundation.IntObjectVisitor
		{
			public _AnonymousInnerClass178(FreespaceManagerIx _enclosing, com.db4o.inside.freespace.FreespaceManager
				 newFM)
			{
				this._enclosing = _enclosing;
				this.newFM = newFM;
			}

			public void visit(int length, object address)
			{
				newFM.free(((int)address), length);
			}

			private readonly FreespaceManagerIx _enclosing;

			private readonly com.db4o.inside.freespace.FreespaceManager newFM;
		}

		private sealed class _AnonymousInnerClass183 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass183(FreespaceManagerIx _enclosing, com.db4o.foundation.IntObjectVisitor
				 addToNewFM)
			{
				this._enclosing = _enclosing;
				this.addToNewFM = addToNewFM;
			}

			public void visit(object a_object)
			{
				com.db4o.inside.ix.IxTree ixTree = (com.db4o.inside.ix.IxTree)a_object;
				ixTree.visitAll(addToNewFM);
			}

			private readonly FreespaceManagerIx _enclosing;

			private readonly com.db4o.foundation.IntObjectVisitor addToNewFM;
		}

		public override void read(int freespaceID)
		{
		}

		private void remove(int address, int length)
		{
			_addressIx.remove(address, length);
			_lengthIx.remove(address, length);
		}

		public override void start(int slotAddress)
		{
			if (started())
			{
				return;
			}
			_slotAddress = slotAddress;
			com.db4o.MetaIndex miAddress = new com.db4o.MetaIndex();
			com.db4o.MetaIndex miLength = new com.db4o.MetaIndex();
			com.db4o.YapReader reader = new com.db4o.YapReader(slotLength());
			reader.read(_file, slotAddress, 0);
			miAddress.read(reader);
			miLength.read(reader);
			_addressIx = new com.db4o.inside.freespace.FreespaceIxAddress(_file, miAddress);
			_lengthIx = new com.db4o.inside.freespace.FreespaceIxLength(_file, miLength);
			_started = true;
		}

		private bool started()
		{
			return _started;
		}

		public override byte systemType()
		{
			return FM_IX;
		}

		public override int write(bool shuttingDown)
		{
			return 0;
		}

		private void writeXBytes(int address, int length)
		{
		}
	}
}
