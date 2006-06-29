namespace com.db4o.inside.freespace
{
	internal class FreespaceIxAddress : com.db4o.inside.freespace.FreespaceIx
	{
		internal FreespaceIxAddress(com.db4o.YapFile file, com.db4o.MetaIndex metaIndex) : 
			base(file, metaIndex)
		{
		}

		internal override void Add(int address, int length)
		{
			_index._handler.PrepareComparison(address);
			_indexTrans.Add(length, address);
		}

		internal override int Address()
		{
			return _visitor._value;
		}

		internal override int Length()
		{
			return _visitor._key;
		}

		internal override void Remove(int address, int length)
		{
			_index._handler.PrepareComparison(address);
			_indexTrans.Remove(length, address);
		}
	}
}
