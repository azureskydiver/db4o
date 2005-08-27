
namespace com.db4o
{
	/// <summary>A range of index entries in the database file.</summary>
	/// <remarks>A range of index entries in the database file.</remarks>
	internal class IxFileRange : com.db4o.IxTree
	{
		internal readonly int _address;

		internal int _addressOffset;

		internal int _entries;

		private int[] _lowerAndUpperMatches;

		public IxFileRange(com.db4o.IxFieldTransaction a_ft, int a_address, int addressOffset
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

		internal override int compare(com.db4o.Tree a_to)
		{
			_lowerAndUpperMatches = new int[2];
			return reader().compare(this, _lowerAndUpperMatches);
		}

		internal override int[] lowerAndUpperMatch()
		{
			return _lowerAndUpperMatches;
		}

		private com.db4o.IxFileRangeReader reader()
		{
			return i_fieldTransaction.i_index.fileRangeReader();
		}

		public virtual void incrementAddress(int length)
		{
			_addressOffset += length;
		}

		internal override int ownSize()
		{
			return _entries;
		}

		public override string ToString()
		{
			com.db4o.YapFile yf = stream();
			com.db4o.Transaction transaction = trans();
			com.db4o.YapReader fileReader = new com.db4o.YapReader(slotLength());
			j4o.lang.StringBuffer sb = new j4o.lang.StringBuffer();
			sb.append("IxFileRange");
			for (int i = 0; i < _entries; i++)
			{
				int address = _address + (i * slotLength());
				fileReader.read(yf, address, _addressOffset);
				fileReader._offset = 0;
				sb.append("\n  ");
				object obj = handler().comparableObject(transaction, handler().readIndexEntry(fileReader
					));
				int parentID = fileReader.readInt();
				sb.append("Parent: " + parentID);
				sb.append("\n ");
				sb.append(obj);
			}
			return sb.ToString();
		}

		public override void visit(com.db4o.foundation.Visitor4 visitor, int[] lowerUpper
			)
		{
			com.db4o.IxFileRangeReader frr = reader();
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

		internal override void write(com.db4o.YapDataType a_handler, com.db4o.YapWriter a_writer
			)
		{
			com.db4o.YapFile yf = (com.db4o.YapFile)a_writer.getStream();
			int length = _entries * slotLength();
			yf.copy(_address, _addressOffset, a_writer.getAddress(), a_writer.addressOffset()
				, length);
			a_writer.moveForward(length);
		}
	}
}
