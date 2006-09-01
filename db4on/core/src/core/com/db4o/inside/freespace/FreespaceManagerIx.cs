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

		private void Add(int address, int length)
		{
			_addressIx.Add(address, length);
			_lengthIx.Add(address, length);
		}

		public override void BeginCommit()
		{
			if (!Started())
			{
				return;
			}
			SlotEntryToZeroes(_file, _slotAddress);
		}

		public override void Debug()
		{
		}

		public override void EndCommit()
		{
			if (!Started())
			{
				return;
			}
			_addressIx._index.CommitFreeSpace(_lengthIx._index);
			com.db4o.YapWriter writer = new com.db4o.YapWriter(_file.GetSystemTransaction(), 
				_slotAddress, SlotLength());
			_addressIx._index._metaIndex.Write(writer);
			_lengthIx._index._metaIndex.Write(writer);
			if (_file.ConfigImpl().FlushFileBuffers())
			{
				_file.SyncFiles();
			}
			writer.WriteEncrypt();
		}

		public override void Free(int address, int length)
		{
			if (!Started())
			{
				return;
			}
			if (address <= 0)
			{
				return;
			}
			if (length <= DiscardLimit())
			{
				return;
			}
			length = _file.BlocksFor(length);
			int freedAddress = address;
			int freedLength = length;
			_addressIx.Find(address);
			if (_addressIx.Preceding())
			{
				if (_addressIx.Address() + _addressIx.Length() == address)
				{
					Remove(_addressIx.Address(), _addressIx.Length());
					address = _addressIx.Address();
					length += _addressIx.Length();
					_addressIx.Find(freedAddress);
				}
			}
			if (_addressIx.Subsequent())
			{
				if (freedAddress + freedLength == _addressIx.Address())
				{
					Remove(_addressIx.Address(), _addressIx.Length());
					length += _addressIx.Length();
				}
			}
			Add(address, length);
		}

		public override void FreeSelf()
		{
			if (!Started())
			{
				return;
			}
			_addressIx._index._metaIndex.Free(_file);
			_lengthIx._index._metaIndex.Free(_file);
		}

		public override int GetSlot(int length)
		{
			if (!Started())
			{
				return 0;
			}
			int address = GetSlot1(length);
			if (address != 0)
			{
			}
			return address;
		}

		private int GetSlot1(int length)
		{
			if (!Started())
			{
				return 0;
			}
			length = _file.BlocksFor(length);
			_lengthIx.Find(length);
			if (_lengthIx.Match())
			{
				Remove(_lengthIx.Address(), _lengthIx.Length());
				return _lengthIx.Address();
			}
			if (_lengthIx.Subsequent())
			{
				int lengthRemainder = _lengthIx.Length() - length;
				int addressRemainder = _lengthIx.Address() + length;
				Remove(_lengthIx.Address(), _lengthIx.Length());
				Add(addressRemainder, lengthRemainder);
				return _lengthIx.Address();
			}
			return 0;
		}

		public override void Migrate(com.db4o.inside.freespace.FreespaceManager newFM)
		{
			if (!Started())
			{
				return;
			}
			com.db4o.foundation.IntObjectVisitor addToNewFM = new _AnonymousInnerClass181(this
				, newFM);
			com.db4o.Tree.Traverse(_addressIx._indexTrans.GetRoot(), new _AnonymousInnerClass186
				(this, addToNewFM));
		}

		private sealed class _AnonymousInnerClass181 : com.db4o.foundation.IntObjectVisitor
		{
			public _AnonymousInnerClass181(FreespaceManagerIx _enclosing, com.db4o.inside.freespace.FreespaceManager
				 newFM)
			{
				this._enclosing = _enclosing;
				this.newFM = newFM;
			}

			public void Visit(int length, object address)
			{
				newFM.Free(((int)address), length);
			}

			private readonly FreespaceManagerIx _enclosing;

			private readonly com.db4o.inside.freespace.FreespaceManager newFM;
		}

		private sealed class _AnonymousInnerClass186 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass186(FreespaceManagerIx _enclosing, com.db4o.foundation.IntObjectVisitor
				 addToNewFM)
			{
				this._enclosing = _enclosing;
				this.addToNewFM = addToNewFM;
			}

			public void Visit(object a_object)
			{
				com.db4o.inside.ix.IxTree ixTree = (com.db4o.inside.ix.IxTree)a_object;
				ixTree.VisitAll(addToNewFM);
			}

			private readonly FreespaceManagerIx _enclosing;

			private readonly com.db4o.foundation.IntObjectVisitor addToNewFM;
		}

		public override void Read(int freespaceID)
		{
		}

		private void Remove(int address, int length)
		{
			_addressIx.Remove(address, length);
			_lengthIx.Remove(address, length);
		}

		public override void Start(int slotAddress)
		{
			if (Started())
			{
				return;
			}
			_slotAddress = slotAddress;
			com.db4o.MetaIndex miAddress = new com.db4o.MetaIndex();
			com.db4o.MetaIndex miLength = new com.db4o.MetaIndex();
			com.db4o.YapReader reader = new com.db4o.YapReader(SlotLength());
			reader.Read(_file, slotAddress, 0);
			miAddress.Read(reader);
			miLength.Read(reader);
			_addressIx = new com.db4o.inside.freespace.FreespaceIxAddress(_file, miAddress);
			_lengthIx = new com.db4o.inside.freespace.FreespaceIxLength(_file, miLength);
			_started = true;
		}

		private bool Started()
		{
			return _started;
		}

		public override byte SystemType()
		{
			return FM_IX;
		}

		public override int Write(bool shuttingDown)
		{
			return 0;
		}

		private void WriteXBytes(int address, int length)
		{
		}
	}
}
