namespace com.db4o.inside.freespace
{
	internal class FreespaceIxLength : com.db4o.inside.freespace.FreespaceIx
	{
		internal FreespaceIxLength(com.db4o.YapFile file, com.db4o.MetaIndex metaIndex) : 
			base(file, metaIndex)
		{
		}

		internal override void add(int address, int length)
		{
			_index._handler.prepareComparison(length);
			_indexTrans.add(address, length);
		}

		internal override int address()
		{
			return _visitor._key;
		}

		internal override int length()
		{
			return _visitor._value;
		}

		internal override void remove(int address, int length)
		{
			_index._handler.prepareComparison(length);
			_indexTrans.remove(address, length);
		}
	}
}
