namespace com.db4o.inside.freespace
{
	internal class FreespaceIxAddress : com.db4o.inside.freespace.FreespaceIx
	{
		internal FreespaceIxAddress(com.db4o.YapFile file, com.db4o.MetaIndex metaIndex) : 
			base(file, metaIndex)
		{
		}

		internal override void add(int address, int length)
		{
			_index._handler.prepareComparison(address);
			_indexTrans.add(length, address);
		}

		internal override int address()
		{
			return _visitor._value;
		}

		internal override int length()
		{
			return _visitor._key;
		}

		internal override void remove(int address, int length)
		{
			_index._handler.prepareComparison(address);
			_indexTrans.remove(length, address);
		}
	}
}
