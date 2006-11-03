namespace com.db4o.inside.ix
{
	/// <summary>A range of index entries in the database file.</summary>
	/// <remarks>A range of index entries in the database file.</remarks>
	internal class IxFileRange : com.db4o.inside.ix.IxTree
	{
		internal readonly int _address;

		internal int _addressOffset;

		internal int _entries;

		private int[] _lowerAndUpperMatches;

		public IxFileRange(com.db4o.inside.ix.IndexTransaction a_ft, int a_address, int addressOffset
			, int a_entries) : base(a_ft)
		{
			_address = a_address;
			_addressOffset = addressOffset;
			_entries = a_entries;
			_size = a_entries;
		}

		public override com.db4o.foundation.Tree Add(com.db4o.foundation.Tree a_new)
		{
			return Reader().Add(this, a_new);
		}

		public override int Compare(com.db4o.foundation.Tree a_to)
		{
			_lowerAndUpperMatches = new int[2];
			return Reader().Compare(this, _lowerAndUpperMatches);
		}

		internal override int[] LowerAndUpperMatch()
		{
			return _lowerAndUpperMatches;
		}

		private com.db4o.inside.ix.IxFileRangeReader Reader()
		{
			return _fieldTransaction.i_index.FileRangeReader();
		}

		public virtual void IncrementAddress(int length)
		{
			_addressOffset += length;
		}

		public override int OwnSize()
		{
			return _entries;
		}

		public override string ToString()
		{
			return base.ToString();
			com.db4o.YapReader fileReader = new com.db4o.YapReader(SlotLength());
			System.Text.StringBuilder sb = new System.Text.StringBuilder();
			sb.Append("IxFileRange");
			VisitAll(new _AnonymousInnerClass59(this, sb));
			return sb.ToString();
		}

		private sealed class _AnonymousInnerClass59 : com.db4o.foundation.IntObjectVisitor
		{
			public _AnonymousInnerClass59(IxFileRange _enclosing, System.Text.StringBuilder sb
				)
			{
				this._enclosing = _enclosing;
				this.sb = sb;
			}

			public void Visit(int anInt, object anObject)
			{
				sb.Append("\n  ");
				sb.Append("Parent: " + anInt);
				sb.Append("\n ");
				sb.Append(anObject);
			}

			private readonly IxFileRange _enclosing;

			private readonly System.Text.StringBuilder sb;
		}

		public override void Visit(object obj)
		{
			Visit((com.db4o.foundation.Visitor4)obj, null);
		}

		public override void Visit(com.db4o.foundation.Visitor4 visitor, int[] lowerUpper
			)
		{
			com.db4o.inside.ix.IxFileRangeReader frr = Reader();
			if (lowerUpper == null)
			{
				lowerUpper = new int[] { 0, _entries - 1 };
			}
			int count = lowerUpper[1] - lowerUpper[0] + 1;
			if (count > 0)
			{
				com.db4o.YapReader fileReader = new com.db4o.YapReader(count * frr._slotLength);
				fileReader.Read(Stream(), _address, _addressOffset + (lowerUpper[0] * frr._slotLength
					));
				for (int i = lowerUpper[0]; i <= lowerUpper[1]; i++)
				{
					fileReader.IncrementOffset(frr._linkLegth);
					visitor.Visit(fileReader.ReadInt());
				}
			}
		}

		public override int Write(com.db4o.inside.ix.Indexable4 a_handler, com.db4o.YapWriter
			 a_writer)
		{
			com.db4o.YapFile yf = (com.db4o.YapFile)a_writer.GetStream();
			int length = _entries * SlotLength();
			yf.Copy(_address, _addressOffset, a_writer.GetAddress(), a_writer.AddressOffset()
				, length);
			a_writer.MoveForward(length);
			return _entries;
		}

		public override void VisitAll(com.db4o.foundation.IntObjectVisitor visitor)
		{
			com.db4o.YapFile yf = Stream();
			com.db4o.Transaction transaction = Trans();
			com.db4o.YapReader fileReader = new com.db4o.YapReader(SlotLength());
			for (int i = 0; i < _entries; i++)
			{
				int address = _address + (i * SlotLength());
				fileReader.Read(yf, address, _addressOffset);
				fileReader._offset = 0;
				object obj = Handler().ComparableObject(transaction, Handler().ReadIndexEntry(fileReader
					));
				visitor.Visit(fileReader.ReadInt(), obj);
			}
		}

		public override void VisitFirst(com.db4o.inside.freespace.FreespaceVisitor visitor
			)
		{
			if (_preceding != null)
			{
				((com.db4o.inside.ix.IxTree)_preceding).VisitFirst(visitor);
				if (visitor.Visited())
				{
					return;
				}
			}
			FreespaceVisit(visitor, 0);
		}

		public override void VisitLast(com.db4o.inside.freespace.FreespaceVisitor visitor
			)
		{
			if (_subsequent != null)
			{
				((com.db4o.inside.ix.IxTree)_subsequent).VisitLast(visitor);
				if (visitor.Visited())
				{
					return;
				}
			}
			FreespaceVisit(visitor, _entries - 1);
		}

		public override void FreespaceVisit(com.db4o.inside.freespace.FreespaceVisitor visitor
			, int index)
		{
			com.db4o.inside.ix.IxFileRangeReader frr = Reader();
			com.db4o.YapReader fileReader = new com.db4o.YapReader(frr._slotLength);
			fileReader.Read(Stream(), _address, _addressOffset + (index * frr._slotLength));
			int val = fileReader.ReadInt();
			int parentID = fileReader.ReadInt();
			visitor.Visit(parentID, val);
		}

		public override object ShallowClone()
		{
			com.db4o.inside.ix.IxFileRange range = new com.db4o.inside.ix.IxFileRange(_fieldTransaction
				, _address, _addressOffset, _entries);
			base.ShallowCloneInternal(range);
			if (_lowerAndUpperMatches != null)
			{
				range._lowerAndUpperMatches = new int[] { _lowerAndUpperMatches[0], _lowerAndUpperMatches
					[1] };
			}
			return range;
		}
	}
}
