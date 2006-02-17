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
			i_size = a_entries;
		}

		public override com.db4o.Tree add(com.db4o.Tree a_new)
		{
			return reader().add(this, a_new);
		}

		public override int compare(com.db4o.Tree a_to)
		{
			_lowerAndUpperMatches = new int[2];
			return reader().compare(this, _lowerAndUpperMatches);
		}

		internal override int[] lowerAndUpperMatch()
		{
			return _lowerAndUpperMatches;
		}

		private com.db4o.inside.ix.IxFileRangeReader reader()
		{
			return i_fieldTransaction.i_index.fileRangeReader();
		}

		public virtual void incrementAddress(int length)
		{
			_addressOffset += length;
		}

		public override int ownSize()
		{
			return _entries;
		}

		public override string ToString()
		{
			return base.ToString();
			com.db4o.YapFile yf = stream();
			com.db4o.Transaction transaction = trans();
			com.db4o.YapReader fileReader = new com.db4o.YapReader(slotLength());
			j4o.lang.StringBuffer sb = new j4o.lang.StringBuffer();
			sb.append("IxFileRange");
			visitAll(new _AnonymousInnerClass61(this, sb));
			return sb.ToString();
		}

		private sealed class _AnonymousInnerClass61 : com.db4o.foundation.IntObjectVisitor
		{
			public _AnonymousInnerClass61(IxFileRange _enclosing, j4o.lang.StringBuffer sb)
			{
				this._enclosing = _enclosing;
				this.sb = sb;
			}

			public void visit(int anInt, object anObject)
			{
				sb.append("\n  ");
				sb.append("Parent: " + anInt);
				sb.append("\n ");
				sb.append(anObject);
			}

			private readonly IxFileRange _enclosing;

			private readonly j4o.lang.StringBuffer sb;
		}

		public override void visit(object obj)
		{
			visit((com.db4o.foundation.Visitor4)obj, null);
		}

		public override void visit(com.db4o.foundation.Visitor4 visitor, int[] lowerUpper
			)
		{
			com.db4o.inside.ix.IxFileRangeReader frr = reader();
			if (lowerUpper == null)
			{
				lowerUpper = new int[] { 0, _entries - 1 };
			}
			int count = lowerUpper[1] - lowerUpper[0] + 1;
			if (count > 0)
			{
				com.db4o.YapReader fileReader = new com.db4o.YapReader(count * frr._slotLength);
				fileReader.read(stream(), _address, _addressOffset + (lowerUpper[0] * frr._slotLength
					));
				for (int i = lowerUpper[0]; i <= lowerUpper[1]; i++)
				{
					fileReader.incrementOffset(frr._linkLegth);
					visitor.visit(fileReader.readInt());
				}
			}
		}

		public override int write(com.db4o.inside.ix.Indexable4 a_handler, com.db4o.YapWriter
			 a_writer)
		{
			com.db4o.YapFile yf = (com.db4o.YapFile)a_writer.getStream();
			int length = _entries * slotLength();
			yf.copy(_address, _addressOffset, a_writer.getAddress(), a_writer.addressOffset()
				, length);
			a_writer.moveForward(length);
			return _entries;
		}

		public override void visitAll(com.db4o.foundation.IntObjectVisitor visitor)
		{
			com.db4o.YapFile yf = stream();
			com.db4o.Transaction transaction = trans();
			com.db4o.YapReader fileReader = new com.db4o.YapReader(slotLength());
			for (int i = 0; i < _entries; i++)
			{
				int address = _address + (i * slotLength());
				fileReader.read(yf, address, _addressOffset);
				fileReader._offset = 0;
				object obj = handler().comparableObject(transaction, handler().readIndexEntry(fileReader
					));
				visitor.visit(fileReader.readInt(), obj);
			}
		}

		public override void visitFirst(com.db4o.inside.freespace.FreespaceVisitor visitor
			)
		{
			if (i_preceding != null)
			{
				((com.db4o.inside.ix.IxTree)i_preceding).visitFirst(visitor);
				if (visitor.visited())
				{
					return;
				}
			}
			freespaceVisit(visitor, 0);
		}

		public override void visitLast(com.db4o.inside.freespace.FreespaceVisitor visitor
			)
		{
			if (i_subsequent != null)
			{
				((com.db4o.inside.ix.IxTree)i_subsequent).visitLast(visitor);
				if (visitor.visited())
				{
					return;
				}
			}
			int lastIndex = _entries - 1;
			freespaceVisit(visitor, _entries - 1);
		}

		public override void freespaceVisit(com.db4o.inside.freespace.FreespaceVisitor visitor
			, int index)
		{
			com.db4o.inside.ix.IxFileRangeReader frr = reader();
			com.db4o.YapReader fileReader = new com.db4o.YapReader(frr._slotLength);
			fileReader.read(stream(), _address, _addressOffset + (index * frr._slotLength));
			int val = fileReader.readInt();
			int parentID = fileReader.readInt();
			visitor.visit(parentID, val);
		}
	}
}
